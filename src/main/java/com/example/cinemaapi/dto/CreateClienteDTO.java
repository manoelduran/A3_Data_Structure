package com.example.cinemaapi.dto;

import com.example.cinemaapi.model.TipoCliente;
import lombok.Data;

@Data
public class CreateClienteDTO {
    private String nome;
    private TipoCliente tipo;
}
