package com.example.cinemaapi.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinemaapi.model.Customer;
import com.example.cinemaapi.model.CustomerType;
import com.example.cinemaapi.repository.CustomerRepository;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional
    public Customer create(String name, CustomerType type) {
        return customerRepository.save(
                Customer.builder()
                        .name(name)
                        .type(type)
                        .build());
    }

    public List<Customer> list() {
        return customerRepository.findAll();
    }

    public Optional<Customer> findById(Long id) {
        return customerRepository.findById(id);
    }
}
