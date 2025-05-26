package com.example.cinemaapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "guiche")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Guiche {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer numero;

    @Builder.Default
    @Column(name = "em_pausa", nullable = false)
    private boolean emPausa = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    @OneToMany(mappedBy = "guiche", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Fila> filas = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Guiche))
            return false;
        return id != null && id.equals(((Guiche) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
