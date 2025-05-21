
package com.example.cinemaapi.model;

import java.util.LinkedList;
import java.util.Queue;

public class TicketCounter {
    private int id;
    private boolean active;
    private Queue<Client> queue;

    public TicketCounter(int id) {
        this.id = id;
        this.active = true;
        this.queue = new LinkedList<>();
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

    public Queue<Client> getQueue() {
        return queue;
    }

    public void addClient(Client client) {
        queue.add(client);
    }

    public Client assistClient() {
        return queue.poll();
    }
}
