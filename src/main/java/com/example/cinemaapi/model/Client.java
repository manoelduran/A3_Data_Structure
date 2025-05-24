package com.example.cinemaapi.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class Client implements Comparable<Client> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ClientType type;

    private LocalDateTime joinedAt;

    private LocalDateTime attendedAt;

    @ManyToOne
    @JoinColumn(name = "ticket_counter_id")
    @JsonBackReference
    private TicketCounter ticketCounter;

    public Client() {
        this.joinedAt = LocalDateTime.now();
    }

    public Client(String name, ClientType type) {
        this.name = name;
        this.type = type;
        this.joinedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ClientType getType() {
        return type;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public TicketCounter getTicketCounter() {
        return ticketCounter;
    }

    public void setTicketCounter(TicketCounter ticketCounter) {
        this.ticketCounter = ticketCounter;
    }

    public LocalDateTime getAttendedAt() {
        return attendedAt;
    }

    public void setAttendedAt(LocalDateTime attendedAt) {
        this.attendedAt = attendedAt;
    }

    @Override
    public int compareTo(Client other) {
        return Integer.compare(this.type.getPriority(), other.type.getPriority());
    }

    public Duration getWaitingTime() {
        if (attendedAt == null || joinedAt == null)
            return Duration.ZERO;
        return Duration.between(joinedAt, attendedAt);
    }

    public enum ClientType {
        IDOSO(0),
        GRAVIDA(1),
        NORMAL(2);

        private final int priority;

        ClientType(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }
}
