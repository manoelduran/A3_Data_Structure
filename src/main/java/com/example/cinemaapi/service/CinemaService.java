package com.example.cinemaapi.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Duration;

import com.example.cinemaapi.dto.TicketCounterDTO;
import com.example.cinemaapi.mappers.ClientMapper;
import com.example.cinemaapi.model.Client;
import com.example.cinemaapi.model.TicketCounter;
import com.example.cinemaapi.repository.ClientRepository;
import com.example.cinemaapi.repository.TicketCounterRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CinemaService {
    private final TicketCounterRepository counterRepo;
    @Autowired
    private final ClientRepository clientRepo;

    @Autowired
    public CinemaService(TicketCounterRepository counterRepo, ClientRepository clientRepo) {
        this.counterRepo = counterRepo;
        this.clientRepo = clientRepo;

        if (counterRepo.count() == 0) {
            for (int i = 1; i <= 3; i++) {
                counterRepo.save(new TicketCounter(i));
            }
        }
    }

    public Client addClient(String name, Client.ClientType type) {
        List<TicketCounter> counters = counterRepo.findAll()
                .stream().filter(TicketCounter::isActive)
                .sorted(Comparator.comparingInt(c -> c.getQueue().size()))
                .toList();

        if (counters.isEmpty()) {
            throw new RuntimeException("Nenhum guichê ativo no momento");
        }

        TicketCounter selected = counters.get(0);
        Client client = new Client(name, type);

        // Associa client ao guichê
        client.setTicketCounter(selected);

        selected.getQueue().add(client);

        // Persistir apenas o client já é suficiente se o relacionamento estiver correto
        clientRepo.save(client);

        return client;
    }

    public List<TicketCounterDTO> listTicketCounters() {
        return counterRepo.findAll().stream()
                .map(counter -> {
                    TicketCounterDTO dto = new TicketCounterDTO();
                    dto.id = counter.getId();
                    dto.isPaused = !counter.isActive();
                    dto.queue = ClientMapper.toClientDTOList(counter.getQueue());
                    dto.currentClient = ClientMapper.toClientDTO(counter.getCurrentClient());
                    return dto;
                })
                .toList();
    }

    public void stopTicketCounter(int id) {
        TicketCounter ticketCounter = counterRepo.findAll().stream().filter(g -> g.getId() == id).findFirst()
                .orElseThrow(() -> new RuntimeException("Guichê não encontrado"));
        ticketCounter.stop();

        List<Client> clients = new ArrayList<>(ticketCounter.getQueue());
        ticketCounter.getQueue().clear();

        clients.forEach(client -> addClient(client.getName(), client.getType()));
    }

    public Client assisClient(int id) {
        TicketCounter ticketCounter = counterRepo.findAll().stream().filter(g -> g.getId() == id && g.isActive())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Guichê inválido ou inativo"));

        return ticketCounter.assistClient();
    }

    public Duration getAverageWaitingTime() {
        List<Client> attendedClients = counterRepo.findAll().stream()
                .flatMap(counter -> counter.getQueue().stream()) // ainda na fila
                .filter(c -> c.getAttendedAt() != null)
                .toList();

        if (attendedClients.isEmpty())
            return Duration.ZERO;

        long totalSeconds = attendedClients.stream()
                .map(Client::getWaitingTime)
                .mapToLong(Duration::getSeconds)
                .sum();

        return Duration.ofSeconds(totalSeconds / attendedClients.size());
    }
}
