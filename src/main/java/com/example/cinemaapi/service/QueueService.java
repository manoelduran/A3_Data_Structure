package com.example.cinemaapi.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinemaapi.model.Customer;
import com.example.cinemaapi.model.Queue;
import com.example.cinemaapi.model.TicketOffice;
import com.example.cinemaapi.repository.CustomerRepository;
import com.example.cinemaapi.model.TicketOfficeStatus;
import com.example.cinemaapi.repository.QueueRepository;
import com.example.cinemaapi.repository.TicketOfficeRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {
    private final QueueRepository queueRepository;
    private final TicketOfficeRepository ticketOfficeRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public Queue enqueue(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        // VERIFICA SE JÁ ESTÁ EM ALGUMA FILA
        boolean alreadyInQueue = queueRepository.existsByCustomerAndServedFalse(customer);
        if (alreadyInQueue) {
            throw new RuntimeException("Cliente já está em uma fila.");
        }
        // Busca todos os guichês ativos e não pausados
        List<TicketOffice> activeOffices = ticketOfficeRepository.findByStatus(TicketOfficeStatus.ACTIVE);

        if (activeOffices.isEmpty()) {
            throw new RuntimeException("Nenhum guichê ativo disponível");
        }

        // Escolhe o guichê com a menor fila
        TicketOffice selectedOffice = activeOffices.stream()
                .min(Comparator.comparingInt(office -> queueRepository.countByTicketOffice(office)))
                .orElseThrow(() -> new RuntimeException("Erro ao selecionar guichê"));

        int position = queueRepository.countByTicketOffice(selectedOffice);

        Queue queue = Queue.builder()
                .customer(customer)
                .ticketOffice(selectedOffice)
                .position(position + 1)
                .build();

        Queue savedQueue = queueRepository.save(queue);
        reorder(selectedOffice.getId());
        return savedQueue;
    }

    @Transactional
    public void reorder(Long ticketOfficeId) {
        List<Queue> queue = queueRepository.findByTicketOfficeId(ticketOfficeId);

        queue.sort(Comparator
                .comparing((Queue q) -> q.getCustomer().getType().getPriorityOrder())
                .thenComparing(Queue::getCreatedAt)); // Desempate por ordem de chegada

        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).setPosition(i + 1);
        }

        queueRepository.saveAll(queue);
    }

    @Transactional
    public void redistribuirClientes(Long guicheId) {
        TicketOffice guiche = ticketOfficeRepository.findById(guicheId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        List<Queue> queues = queueRepository.findByTicketOfficeOrderByPriorityDescPositionAsc(guiche);
        TicketOfficeStatus activeStatus = TicketOfficeStatus.ACTIVE;
        List<TicketOffice> availableTicketOffices = ticketOfficeRepository
                .findByStatusAndIdNot(activeStatus, guicheId);
        if (availableTicketOffices.isEmpty()) {
            throw new IllegalStateException("Não há guichês disponíveis para redistribuição");
        }

        for (Queue queue : queues) {
            // Encontrar o guichê com menor fila
            TicketOffice newTicketOffice = availableTicketOffices.stream()
                    .min(Comparator.comparingInt(g -> queueRepository.countByTicketOffice(g)))
                    .orElseThrow(() -> new IllegalStateException("Erro ao selecionar guichê"));

            // Mover cliente para o novo guichê
            queue.setTicketOffice(newTicketOffice);

            // Atualizar posição mantendo a prioridade
            Integer lastPosition = queueRepository.findMaxPositionByTicketOffice(newTicketOffice);
            queue.setPosition(lastPosition != null ? lastPosition + 1 : 1);

            queueRepository.save(queue);
            reorder(newTicketOffice.getId());
        }

        log.info("Redistribuídos {} clientes do guichê {}", queues.size(), guiche.getNumber());
    }

    @Transactional
    public void dequeue(Long ticketOfficeId) {
        TicketOffice ticketOffice = ticketOfficeRepository.findById(ticketOfficeId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        Optional<Queue> next = queueRepository.findFirstByTicketOfficeOrderByPriorityDescPositionAsc(ticketOffice);

        if (next.isPresent()) {
            queueRepository.delete(next.get());

            // Reorganizar posições dos clientes restantes
            List<Queue> lastQueues = queueRepository.findByTicketOfficeOrderByPriorityDescPositionAsc(ticketOffice);
            for (int i = 0; i < lastQueues.size(); i++) {
                Queue f = lastQueues.get(i);
                f.setPosition(i + 1);
                queueRepository.save(f);
            }

            log.info("Cliente atendido no guichê {}", ticketOffice.getNumber());
        }
    }

    public List<Queue> list() {
        return queueRepository.findAllByOrderByTicketOfficeAscPriorityDescPositionAsc();
    }
}