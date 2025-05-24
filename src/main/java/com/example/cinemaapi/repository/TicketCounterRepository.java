package com.example.cinemaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinemaapi.model.TicketCounter;

public interface TicketCounterRepository extends JpaRepository<TicketCounter, Integer> {
}
