package com.example.cinemaapi.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinemaapi.model.Guiche;
import com.example.cinemaapi.repository.GuicheRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuicheService {
    private final GuicheRepository guicheRepository;
    private final FilaService filaService;

    @Transactional
    public Guiche criarGuiche(Integer numero) {
        if ((guicheRepository.findByNumero(numero).isPresent())) {
            throw new IllegalStateException("Já existe um guichê com este número");
        }

        return guicheRepository.save(
                Guiche.builder()
                        .numero(numero)
                        .emPausa(false)
                        .ativo(true)
                        .build());
    }

    @Transactional
    public Guiche entrarEmPausa(Long guicheId) {
        Guiche guiche = guicheRepository.findById(guicheId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        if (guiche.isEmPausa()) {
            throw new IllegalStateException("Guichê já está em pausa");
        }

        guiche.setEmPausa(true);
        guiche = guicheRepository.save(guiche);

        filaService.redistribuirClientes(guicheId);

        log.info("Guichê {} entrou em pausa. Clientes redistribuídos.", guiche.getNumero());
        return guiche;
    }

    @Transactional
    public Guiche retornarDoPausa(Long guicheId) {
        Guiche guiche = guicheRepository.findById(guicheId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        if (!guiche.isEmPausa()) {
            throw new IllegalStateException("Guichê já está ativo");
        }

        guiche.setEmPausa(false);
        return guicheRepository.save(guiche);
    }

    public List<Guiche> listarTodos() {
        return guicheRepository.findAll();
    }
}