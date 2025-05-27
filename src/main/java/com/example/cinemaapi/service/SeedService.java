package com.example.cinemaapi.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.cinemaapi.model.Customer;
import com.example.cinemaapi.model.CustomerType;
import com.example.cinemaapi.model.TicketOffice;
import com.example.cinemaapi.model.TicketOfficeStatus;
import com.example.cinemaapi.repository.CustomerRepository;
import com.example.cinemaapi.repository.TicketOfficeRepository;

@Service
@RequiredArgsConstructor
public class SeedService {

    private final TicketOfficeRepository ticketOfficeRepository;
    private final CustomerRepository customerRepository;

    @PostConstruct
    public void init() {
        for (int i = 1; i <= 3; i++) {
            TicketOffice office = new TicketOffice();
            office.setNumber(i);
            office.setStatus(TicketOfficeStatus.ACTIVE);
            ticketOfficeRepository.save(office);
        }

        // Criar clientes
        createCustomer("Customer 1", CustomerType.OLD);
        createCustomer("Customer 2", CustomerType.OLD);
        createCustomer("Customer 3", CustomerType.PREGNANT);
        createCustomer("Customer 4", CustomerType.NORMAL);
        createCustomer("Customer 5", CustomerType.NORMAL);
    }

    private void createCustomer(String name, CustomerType type) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setType(type);
        customerRepository.save(customer);
    }
}
