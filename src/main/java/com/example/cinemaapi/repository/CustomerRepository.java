package com.example.cinemaapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cinemaapi.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}