package com.example.cinemaapi.model;

import java.util.UUID;

public class Client {

    private String id;
    private String name;
    private ClientType type;

    public Client(String name, ClientType type) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ClientType getType() {
        return type;
    }

    public enum ClientType {
        COMUM,
        ESTUDANTE,
        IDOSO
    }
}
