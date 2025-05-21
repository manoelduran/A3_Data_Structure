package com.example.cinemaapi.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cinemaapi.model.Client;
import com.example.cinemaapi.model.TicketCounter;

@Service
public class CinemaService {
    private final List<TicketCounter> ticketCounters = new ArrayList<>();

    public CinemaService() {
        for (int i = 1; i <= 3; i++) {
            ticketCounters.add(new TicketCounter(i));
        }
    }

    public Client addClient(String name, Client.ClientType type) {
        TicketCounter smallerQueue = ticketCounters.stream()
                .filter(TicketCounter::isActive)
                .min(Comparator.comparingInt(g -> g.getQueue().size()))
                .orElseThrow(() -> new RuntimeException("Nenhum guichê ativo disponível"));

        Client client = new Client(name, type);
        smallerQueue.addClient(client);
        return client;
    }

    public List<TicketCounter> listTicketCounters() {
        return ticketCounters;
    }

    public void stopTicketCounter(int id) {
        TicketCounter ticketCounter = ticketCounters.stream().filter(g -> g.getId() == id).findFirst()
                .orElseThrow(() -> new RuntimeException("Guichê não encontrado"));
        ticketCounter.stop();

        List<Client> clients = new ArrayList<>(ticketCounter.getQueue());
        ticketCounter.getQueue().clear();

        clients.forEach(client -> addClient(client.getName(), client.getType()));
    }

    public Client assisClient(int id) {
        TicketCounter ticketCounter = ticketCounters.stream().filter(g -> g.getId() == id && g.isActive()).findFirst()
                .orElseThrow(() -> new RuntimeException("Guichê inválido ou inativo"));

        return ticketCounter.assistClient();
    }
}
