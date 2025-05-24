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
        // Busca todos os guichês ativos e ordena por tamanho da fila
        TicketCounter selectedCounter = counterRepo.findAll().stream()
                .filter(TicketCounter::isActive)
                .min(Comparator.comparingInt(c -> c.getQueue().size()))
                .orElseThrow(() -> new RuntimeException("Nenhum guichê ativo no momento"));

        // Cria o cliente e adiciona ao guichê
        Client client = new Client(name, type);
        selectedCounter.addClient(client); // encapsula a lógica de adicionar e associar o client ao guichê

        clientRepo.save(client);
        return client;
    }

    public List<TicketCounterDTO> listTicketCounters() {
        return counterRepo.findAll().stream()
                .map(counter -> {
                    TicketCounterDTO dto = new TicketCounterDTO();
                    dto.id = counter.getId();
                    dto.queue = ClientMapper.toClientDTOList(
                            counter.getQueue().stream()
                                    .sorted()
                                    .toList());
                    dto.currentClient = ClientMapper.toClientDTO(counter.getCurrentClient());
                    return dto;
                })
                .toList();
    }

    public void stopTicketCounter(int id) {
        TicketCounter ticketCounter = counterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Guichê não encontrado"));

        // Pausa o guichê
        ticketCounter.stop();

        // Captura os clientes da fila e limpa a fila
        List<Client> clientsToRedistribute = new ArrayList<>(ticketCounter.getQueue());
        ticketCounter.getQueue().clear();

        // Obtem os guichês ativos ordenados pelos menores tamanhos de fila
        List<TicketCounter> activeCounters = counterRepo.findAll().stream()
                .filter(TicketCounter::isActive)
                .sorted(Comparator.comparingInt(c -> c.getQueue().size()))
                .toList();

        if (activeCounters.isEmpty()) {
            throw new RuntimeException("Nenhum guichê ativo para redistribuir os clientes");
        }

        // Redistribui os clientes manualmente preservando o objeto original
        for (Client client : clientsToRedistribute) {
            TicketCounter target = activeCounters.stream()
                    .min(Comparator.comparingInt(c -> c.getQueue().size()))
                    .orElseThrow();

            client.setTicketCounter(target);
            target.addClient(client);
        }

        // Salva todos os clientes redistribuídos
        clientRepo.saveAll(clientsToRedistribute);
    }

    public Client assistClient(int id) {
        TicketCounter ticketCounter = counterRepo.findById(id)
                .filter(TicketCounter::isActive)
                .orElseThrow(() -> new RuntimeException("Guichê inválido ou inativo"));

        Client client = ticketCounter.assistClient();

        if (client != null) {
            client.setAttendedAt(java.time.LocalDateTime.now());
            clientRepo.save(client);
        }

        return client;
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
