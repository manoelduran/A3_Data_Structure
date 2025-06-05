package com.example.cinemaapi.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ticket_office")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer number;

    @Column(name = "attendance_time")
    @Min(value = 1, message = "O tempo de atendimento deve ser pelo menos 1 segundo")
    private Long attendanceTimeInSeconds;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TicketOfficeStatus status = TicketOfficeStatus.ACTIVE;

    @OneToMany(mappedBy = "ticketOffice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Queue> queue = new ArrayList<>();

    // Método para obter o tempo de atendimento em milissegundos
    public long getAttendanceTimeInMillis() {
        return this.attendanceTimeInSeconds * 1000;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TicketOffice))
            return false;
        return id != null && id.equals(((TicketOffice) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
