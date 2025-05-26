package com.example.cinemaapi.model;

import lombok.Getter;

public enum TipoCliente {
    IDOSO(3),
    GRAVIDA(2),
    NORMAL(1);

    @Getter
    private final int prioridade;

    TipoCliente(int prioridade) {
        this.prioridade = prioridade;
    }
}