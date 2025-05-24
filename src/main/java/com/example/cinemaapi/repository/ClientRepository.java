package com.example.cinemaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinemaapi.model.Client;

public interface ClientRepository extends JpaRepository<Client, String> {
}
