package com.example.cinemaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinemaapi.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}