package com.example.cinemaapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.PathVariable;
import com.example.cinemaapi.dto.AddToQueueDTO;
import com.example.cinemaapi.model.Customer;
import com.example.cinemaapi.model.Queue;
import com.example.cinemaapi.service.QueueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    @PostMapping("/enqueue")
    public ResponseEntity<Queue> enqueueCustomer(@RequestBody AddToQueueDTO dto) {
        return ResponseEntity.ok(queueService.enqueue(dto.getCustomerId()));
    }

    @PostMapping("/dequeue/{ticketOfficeId}")
    public ResponseEntity<Queue> dequeueCustomer(@PathVariable Long ticketOfficeId) {
        return ResponseEntity.ok(queueService.dequeue(ticketOfficeId));
    }
}
