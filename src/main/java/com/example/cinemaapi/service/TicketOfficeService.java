package com.example.cinemaapi.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinemaapi.model.Queue;
import com.example.cinemaapi.model.TicketOffice;
import com.example.cinemaapi.model.TicketOfficeReason;
import com.example.cinemaapi.model.TicketOfficeStatus;
import com.example.cinemaapi.repository.TicketOfficeRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketOfficeService {
    private final TicketOfficeRepository ticketOfficeRepository;
    private final QueueService queueService;

    @Transactional
    public TicketOffice create(Integer number) {
        if ((ticketOfficeRepository.findByNumber(number).isPresent())) {
            throw new IllegalStateException("Já existe um guichê com este número");
        }
        return ticketOfficeRepository.save(
                TicketOffice.builder()
                        .number(number)
                        .status(TicketOfficeStatus.ACTIVE)
                        .build());
    }

    @Transactional
    public TicketOffice pause(Long ticketOfficeId) {
        TicketOffice ticketOffice = ticketOfficeRepository.findById(ticketOfficeId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        if (ticketOffice.getStatus() == TicketOfficeStatus.PAUSED) {
            throw new IllegalStateException("Guichê já está em pausa");
        }
        ticketOffice.setPausedAt(LocalDateTime.now());
        ticketOffice.setAttendanceTimeInSeconds(ticketOffice.getAttendanceTimeInSeconds() + 2);
        // sortei um motivo aleatório para a pausa
        ticketOffice.setPauseReason(
                TicketOfficeReason.values()[(int) (Math.random() * TicketOfficeReason.values().length)]);
        ticketOffice.setStatus(TicketOfficeStatus.PAUSED);
        ticketOffice = ticketOfficeRepository.save(ticketOffice);

        queueService.redistributeCustomer(ticketOfficeId);

        log.info("Guichê {} entrou em pausa. Clientes redistribuídos.", ticketOffice.getNumber());
        return ticketOffice;
    }

    @Transactional
    public TicketOffice back(Long ticketOfficeId) {
        TicketOffice ticketOffice = ticketOfficeRepository.findById(ticketOfficeId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        if (ticketOffice.getStatus() != TicketOfficeStatus.PAUSED) {
            throw new IllegalStateException("Somente guichês em pausa podem voltar à ativa");
        }
        ticketOffice.setPausedAt(null);
        ticketOffice.setPauseReason(null);
        ticketOffice.setStatus(TicketOfficeStatus.ACTIVE);
        return ticketOfficeRepository.save(ticketOffice);
    }

    public List<TicketOffice> list() {
        List<TicketOffice> offices = ticketOfficeRepository.findAll(Sort.by("number"));

        for (TicketOffice office : offices) {
            office.setQueue(
                    office.getQueue()
                            .stream()
                            .filter(entry -> !entry.isServed())
                            .sorted((q1, q2) -> Integer.compare(q1.getPosition(), q2.getPosition()))
                            .toList());
        }

        return offices;
    }

    public List<Queue> getHistory(Long ticketOfficeId) {
        TicketOffice ticketOffice = ticketOfficeRepository.findById(ticketOfficeId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        return ticketOffice.getQueue()
                .stream()
                .filter(Queue::isServed)
                .sorted(Comparator.comparing(Queue::getAttendedAt))
                .toList();
    }
}