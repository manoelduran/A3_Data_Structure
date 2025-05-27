package com.example.cinemaapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.example.cinemaapi.dto.CreateGuicheDTO;
import com.example.cinemaapi.model.TicketOffice;
import com.example.cinemaapi.service.TicketOfficeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ticket-offices")
@RequiredArgsConstructor
public class TicketOfficeController {
    private final TicketOfficeService ticketOfficeService;

    @PostMapping
    public ResponseEntity<TicketOffice> create(@RequestBody CreateGuicheDTO request) {
        return ResponseEntity.ok(ticketOfficeService.create(request.getNumber()));
    }

    @PutMapping("/{id}/stop")
    public ResponseEntity<TicketOffice> stop(@PathVariable Long id) {
        return ResponseEntity.ok(ticketOfficeService.pause(id));
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<TicketOffice> back(@PathVariable Long id) {
        return ResponseEntity.ok(ticketOfficeService.back(id));
    }

    @GetMapping
    public ResponseEntity<List<TicketOffice>> list() {
        return ResponseEntity.ok(ticketOfficeService.list());
    }
}