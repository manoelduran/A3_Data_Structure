package com.example.cinemaapi.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fila", uniqueConstraints = @UniqueConstraint(columnNames = { "guiche_id", "cliente_id" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fila {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "guiche_id")
    @JsonBackReference
    private Guiche guiche;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    @JsonBackReference
    private Cliente cliente;

    @Column(nullable = false)
    private Integer prioridade;

    @Column(nullable = false)
    private Integer posicao;

    @PrePersist
    @PreUpdate
    private void calcularPrioridade() {
        if (this.prioridade == null && this.cliente != null) {
            this.prioridade = this.cliente.getTipo().getPrioridade();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Fila))
            return false;
        return id != null && id.equals(((Fila) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}