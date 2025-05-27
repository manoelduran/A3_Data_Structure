package com.example.cinemaapi.model;

public enum CustomerType {
    OLD, PREGNANT, NORMAL;

    public int getPriorityOrder() {
        return switch (this) {
            case OLD -> 0;
            case PREGNANT -> 1;
            case NORMAL -> 2;
        };
    }
}