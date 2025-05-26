package com.example.cinemaapi.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinemaapi.model.Cliente;
import com.example.cinemaapi.model.Fila;
import com.example.cinemaapi.model.Guiche;
import com.example.cinemaapi.repository.ClienteRepository;
import com.example.cinemaapi.repository.FilaRepository;
import com.example.cinemaapi.repository.GuicheRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilaService {
    private final FilaRepository filaRepository;
    private final GuicheRepository guicheRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public Fila adicionarClienteAFila(Long clienteId, Long guicheId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Guiche guiche = guicheRepository.findById(guicheId)
                .orElseThrow(() -> new RuntimeException("Guichê não encontrado"));

        int posicao = filaRepository.countByGuiche(guiche);

        Fila fila = Fila.builder()
                .cliente(cliente)
                .guiche(guiche)
                .posicao(posicao + 1)
                .build();

        return filaRepository.save(fila);
    }

    @Transactional
    public void redistribuirClientes(Long guicheId) {
        Guiche guiche = guicheRepository.findById(guicheId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        List<Fila> filas = filaRepository.findByGuicheOrderByPrioridadeDescPosicaoAsc(guiche);
        List<Guiche> guichesDisponiveis = guicheRepository.findByEmPausaFalseAndAtivoTrueAndIdNot(guicheId);

        if (guichesDisponiveis.isEmpty()) {
            throw new IllegalStateException("Não há guichês disponíveis para redistribuição");
        }

        for (Fila fila : filas) {
            // Encontrar o guichê com menor fila
            Guiche novoGuiche = guichesDisponiveis.stream()
                    .min(Comparator.comparingInt(g -> filaRepository.countByGuiche(g)))
                    .orElseThrow(() -> new IllegalStateException("Erro ao selecionar guichê"));

            // Mover cliente para o novo guichê
            fila.setGuiche(novoGuiche);

            // Atualizar posição mantendo a prioridade
            Integer ultimaPosicao = filaRepository.findMaxPosicaoByGuiche(novoGuiche);
            fila.setPosicao(ultimaPosicao != null ? ultimaPosicao + 1 : 1);

            filaRepository.save(fila);
        }

        log.info("Redistribuídos {} clientes do guichê {}", filas.size(), guiche.getNumero());
    }

    @Transactional
    public void atenderProximoCliente(Long guicheId) {
        Guiche guiche = guicheRepository.findById(guicheId)
                .orElseThrow(() -> new EntityNotFoundException("Guichê não encontrado"));

        Optional<Fila> proximo = filaRepository.findFirstByGuicheOrderByPrioridadeDescPosicaoAsc(guiche);

        if (proximo.isPresent()) {
            filaRepository.delete(proximo.get());

            // Reorganizar posições dos clientes restantes
            List<Fila> filasRestantes = filaRepository.findByGuicheOrderByPrioridadeDescPosicaoAsc(guiche);
            for (int i = 0; i < filasRestantes.size(); i++) {
                Fila f = filasRestantes.get(i);
                f.setPosicao(i + 1);
                filaRepository.save(f);
            }

            log.info("Cliente atendido no guichê {}", guiche.getNumero());
        }
    }

    public List<Fila> visualizarFilas() {
        return filaRepository.findAllByOrderByGuicheAscPrioridadeDescPosicaoAsc();
    }
}