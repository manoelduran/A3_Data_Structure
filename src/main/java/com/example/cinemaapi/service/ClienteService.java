package com.example.cinemaapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import com.example.cinemaapi.model.Cliente;
import com.example.cinemaapi.model.TipoCliente;
import com.example.cinemaapi.repository.ClienteRepository;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente criarCliente(String nome, TipoCliente tipo) {
        return clienteRepository.save(
                Cliente.builder()
                        .nome(nome)
                        .tipo(tipo)
                        .build());
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
}
