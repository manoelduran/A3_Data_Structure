package com.example.cinemaapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.cinemaapi.dto.DequeueResponse;
import com.example.cinemaapi.model.Queue;
import com.example.cinemaapi.service.QueueService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class QueueController {
    private final QueueService queueService;

    @PostMapping("/enqueue/{customerId}")
    public ResponseEntity<Queue> enqueueCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(queueService.enqueue(customerId));
    }

    @PostMapping("/dequeue/{ticketOfficeId}")
    public ResponseEntity<DequeueResponse> dequeueCustomer(@PathVariable Long ticketOfficeId) {
        return ResponseEntity.ok(queueService.dequeue(ticketOfficeId));
    }
}
