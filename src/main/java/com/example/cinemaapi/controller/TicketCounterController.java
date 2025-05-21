package com.example.cinemaapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cinemaapi.dto.ClientDTO;
import com.example.cinemaapi.model.Client;
import com.example.cinemaapi.model.TicketCounter;
import com.example.cinemaapi.service.CinemaService;

@RestController
@RequestMapping("/api/cinema")
public class TicketCounterController {

    private final CinemaService cinemaService;

    public TicketCounterController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping("/clients")
    public Client addClient(@RequestBody ClientDTO dto) {
        return cinemaService.addClient(dto.name, dto.type);
    }

    @GetMapping("/ticketCounters")
    public List<TicketCounter> listTicketCounters() {
        return cinemaService.listTicketCounters();
    }

    @PostMapping("/ticketCounters/{id}/stop")
    public void pausarGuiche(@PathVariable int id) {
        cinemaService.stopTicketCounter(id);
    }

    @PostMapping("/ticketCounters/{id}/assist")
    public Client assisClient(@PathVariable int id) {
        return cinemaService.assisClient(id);
    }
}