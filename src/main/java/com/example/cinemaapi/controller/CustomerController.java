package com.example.cinemaapi.controller;

import com.example.cinemaapi.model.Customer;
import com.example.cinemaapi.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.cinemaapi.dto.CreateCustomerDTO;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody CreateCustomerDTO request) {
        return ResponseEntity.ok(customerService.create(request.getName(), request.getType()));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> listarTodos() {
        return ResponseEntity.ok(customerService.list());
    }
}
