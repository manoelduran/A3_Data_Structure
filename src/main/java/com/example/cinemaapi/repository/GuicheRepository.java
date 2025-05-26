package com.example.cinemaapi.repository;

import java.util.List;
import java.util.Optional;
import com.example.cinemaapi.model.Guiche;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuicheRepository extends JpaRepository<Guiche, Long> {
    List<Guiche> findByEmPausaFalseAndAtivoTrueOrderById();

    List<Guiche> findByEmPausaFalseAndAtivoTrueAndIdNot(Long guicheId);

    Optional<Guiche> findByNumero(Integer numero);
}