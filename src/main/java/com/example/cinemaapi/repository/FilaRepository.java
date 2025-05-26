package com.example.cinemaapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.Query;
import com.example.cinemaapi.model.Fila;
import com.example.cinemaapi.model.Guiche;
import com.example.cinemaapi.model.Cliente;

public interface FilaRepository extends JpaRepository<Fila, Long> {
    int countByGuiche(Guiche guiche);

    boolean existsByCliente(Cliente cliente);

    Optional<Fila> findFirstByGuicheOrderByPrioridadeDescPosicaoAsc(Guiche guiche);

    List<Fila> findByGuicheOrderByPrioridadeDescPosicaoAsc(Guiche guiche);

    @Query("SELECT MAX(f.posicao) FROM Fila f WHERE f.guiche = :guiche")
    Integer findMaxPosicaoByGuiche(@Param("guiche") Guiche guiche);

    List<Fila> findAllByOrderByGuicheAscPrioridadeDescPosicaoAsc();

    boolean existsByClienteAndGuicheNot(Cliente cliente, Guiche guiche);
}
