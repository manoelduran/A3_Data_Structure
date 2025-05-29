package com.example.cinemaapi.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .min(Comparator
                        .comparingInt((TicketOffice office) -> queueRepository.countActiveByTicketOffice(office))
                        .thenComparing(TicketOffice::getNumber) // Desempate pelo ID mais baixo
                )
                .orElseThrow(() -> new RuntimeException("Erro ao selecionar guichê"));
        int position = queueRepository.countActiveByTicketOffice(selectedOffice);

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

        List<Queue> queues = queueRepository.findByTicketOfficeAndServedFalseOrderByPriorityDescPositionAsc(guiche);
        List<TicketOffice> availableTicketOffices = ticketOfficeRepository
                .findByStatusAndIdNot(TicketOfficeStatus.ACTIVE, guicheId);

        if (availableTicketOffices.isEmpty()) {
            throw new IllegalStateException("Não há guichês disponíveis para redistribuição");
        }

        // Mapa para manter controle em tempo real da quantidade de clientes por guichê
        Map<Long, Integer> filaPorGuiche = new HashMap<>();
        for (TicketOffice g : availableTicketOffices) {
            filaPorGuiche.put(g.getId(), queueRepository.countActiveByTicketOffice(g));
        }

        for (Queue queue : queues) {
            // Encontrar o guichê com menor fila
            TicketOffice newTicketOffice = availableTicketOffices.stream()
                    .min(Comparator.comparingInt(g -> filaPorGuiche.get(g.getId())))
                    .orElseThrow(() -> new IllegalStateException("Erro ao selecionar guichê"));

            // Atualizar guichê e posição
            queue.setTicketOffice(newTicketOffice);
            Integer lastPosition = queueRepository.findMaxPositionByTicketOffice(newTicketOffice);
            queue.setPosition(lastPosition != null ? lastPosition + 1 : 1);

            queueRepository.save(queue);
            reorder(newTicketOffice.getId());

            // Atualizar contagem no mapa
            filaPorGuiche.put(newTicketOffice.getId(), filaPorGuiche.get(newTicketOffice.getId()) + 1);
        }

        log.info("Redistribuídos {} clientes do guichê {}", queues.size(), guiche.getNumber());
    }

    @Transactional(readOnly = true)
    public List<Queue> getHistoryByTicketOffice(Long ticketOfficeId) {
        TicketOffice ticketOffice = ticketOfficeRepository.findById(ticketOfficeId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        return queueRepository.findByTicketOfficeAndServedTrueOrderByAttendedAtDesc(ticketOffice);
    }

    @Transactional
    public Queue dequeue(Long ticketOfficeId) {
        TicketOffice ticketOffice = ticketOfficeRepository.findById(ticketOfficeId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        // Busca apenas os clientes que ainda não foram atendidos, ordenados por posição
        List<Queue> queues = queueRepository
                .findByTicketOfficeAndServedFalseOrderByPriorityAscPositionAsc(ticketOffice);

        if (!queues.isEmpty()) {
            // Pega o primeiro da fila
            Queue next = queues.remove(0);

            // Marca como atendido em vez de remover
            next.setServed(true);
            next.setAttendedAt(LocalDateTime.now());
            queueRepository.save(next);

            // Reorganiza as posições dos demais
            for (int i = 0; i < queues.size(); i++) {
                queues.get(i).setPosition(i + 1);
            }

            queueRepository.saveAll(queues);

            log.info("Cliente atendido no guichê {}", ticketOffice.getNumber());
            return next;
        }

        throw new EntityNotFoundException("Nenhum cliente na fila do guichê " + ticketOfficeId);
    }
}
