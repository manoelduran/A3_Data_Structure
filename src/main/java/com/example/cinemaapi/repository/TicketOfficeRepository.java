package com.example.cinemaapi.repository;

import java.util.List;
import java.util.Optional;
import com.example.cinemaapi.model.TicketOffice;
import com.example.cinemaapi.model.TicketOfficeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketOfficeRepository extends JpaRepository<TicketOffice, Long> {
    List<TicketOffice> findByStatus(TicketOfficeStatus status);

    List<TicketOffice> findByStatusAndIdNot(TicketOfficeStatus status, Long id);

    Optional<TicketOffice> findByNumber(Integer number);
}