package com.example.cinemaapi.dto;

import com.example.cinemaapi.model.Queue;
import com.example.cinemaapi.model.TicketOffice;

public class DequeueResponse {
    private Queue next;
    private TicketOffice ticketOffice;

    public DequeueResponse(Queue next, TicketOffice ticketOffice) {
        this.next = next;
        this.ticketOffice = ticketOffice;
    }

    public Queue getNext() {
        return next;
    }

    public TicketOffice getTicketOffice() {
        return ticketOffice;
    }
}