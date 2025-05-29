package com.example.cinemaapi.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ticket_office_id")
    @JsonBackReference
    private TicketOffice ticketOffice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id")
    @JsonIgnoreProperties("queue")
    private Customer customer;

    @Column(nullable = false)
    private Integer priority;

    @Column(name = "served", nullable = false)
    @Builder.Default
    private boolean served = false;

    @Column(nullable = false)
    private Integer position;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "attended_at")
    private LocalDateTime attendedAt;

    @PrePersist
    @PreUpdate
    private void prePersistAndUpdate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (this.priority == null && this.customer != null) {
            this.priority = this.customer.getType().getPriorityOrder();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Queue))
            return false;
        return id != null && id.equals(((Queue) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}