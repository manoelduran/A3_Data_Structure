package com.example.cinemaapi.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
public class TicketCounter {
    @Id
    private int id;

    private boolean active;
    @OneToMany(mappedBy = "ticketCounter", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Client> queue = new ArrayList<>();

    @OneToOne
    private Client currentClient;

    public TicketCounter() {
    }

    public TicketCounter(int id) {
        this.id = id;
        this.active = true;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void stop() {
        this.active = false;
    }

    public void active() {
        this.active = true;
    }

    public List<Client> getQueue() {
        return queue;
    }

    public void addClient(Client client) {
        client.setTicketCounter(this);
        this.queue.add(client);
    }

    public Client getCurrentClient() {
        return currentClient;
    }

    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
    }

    public Client assistClient() {
        if (queue.isEmpty())
            return null;
        Client client = queue.remove(0);
        this.setCurrentClient(client); // caso você queira saber quem está sendo atendido
        return client;
    }
}
