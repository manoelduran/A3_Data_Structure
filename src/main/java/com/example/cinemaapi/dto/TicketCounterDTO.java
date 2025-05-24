package com.example.cinemaapi.dto;

import java.util.List;

public class TicketCounterDTO {
    public int id;
    public boolean isPaused;
    public ClientDTO currentClient;
    public List<ClientDTO> queue;

    public TicketCounterDTO() {
    }

    public TicketCounterDTO(int id, boolean isPaused, ClientDTO currentClient, List<ClientDTO> queue) {
        this.id = id;
        this.isPaused = isPaused;
        this.currentClient = currentClient;
        this.queue = queue;
    }
}