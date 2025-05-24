package com.example.cinemaapi.dto;

import com.example.cinemaapi.model.Client.ClientType;

public class ClientDTO {
    public Long id;
    public String name;
    public ClientType type;

    public ClientDTO() {
    }

    public ClientDTO(Long id, String name, ClientType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }
}
