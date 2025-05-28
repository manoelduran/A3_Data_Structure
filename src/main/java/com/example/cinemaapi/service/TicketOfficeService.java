package com.example.cinemaapi.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinemaapi.model.TicketOffice;
import com.example.cinemaapi.model.TicketOfficeStatus;
import com.example.cinemaapi.repository.TicketOfficeRepository;

import jakarta.persistence.EntityNotFoundException;

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

        ticketOffice.setStatus(TicketOfficeStatus.PAUSED);
        ticketOffice = ticketOfficeRepository.save(ticketOffice);

        queueService.redistribuirClientes(ticketOfficeId);

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

        ticketOffice.setStatus(TicketOfficeStatus.ACTIVE);
        return ticketOfficeRepository.save(ticketOffice);
    }

    public List<TicketOffice> list() {
        List<TicketOffice> offices = ticketOfficeRepository.findAll();

        for (TicketOffice office : offices) {
            office.setQueue(
                    office.getQueue()
                            .stream()
                            .filter(entry -> !entry.isServed())
                            .toList());
        }

        return offices;
    }
}