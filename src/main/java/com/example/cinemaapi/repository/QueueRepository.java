package com.example.cinemaapi.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import com.example.cinemaapi.model.Queue;
import com.example.cinemaapi.model.TicketOffice;
import com.example.cinemaapi.model.Customer;

public interface QueueRepository extends JpaRepository<Queue, Long> {
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.ticketOffice = :ticketOffice AND q.served = false")
    int countActiveByTicketOffice(@Param("ticketOffice") TicketOffice ticketOffice);

    boolean existsByCustomer(Customer customer);

    boolean existsByCustomerAndServedFalse(Customer customer);

    Optional<Queue> findFirstByTicketOffice(TicketOffice ticketOffice);

    List<Queue> findByTicketOfficeId(Long ticketOfficeId);

    List<Queue> findByTicketOffice(TicketOffice ticketOffice);

    List<Queue> findByTicketOfficeAndServedFalseOrderByPriorityAscPositionAsc(TicketOffice ticketOffice);

    List<Queue> findByTicketOfficeAndServedFalseOrderByPriorityAsc(TicketOffice ticketOffice);

    List<Queue> findByTicketOfficeOrderByPriorityDescPositionAsc(TicketOffice ticketOffice);

    List<Queue> findByTicketOfficeAndServedTrueOrderByAttendedAtDesc(TicketOffice ticketOffice);

    List<Queue> findByTicketOfficeAndServedFalseOrderByPriorityDescPositionAsc(TicketOffice ticketOffice);

    List<Queue> findByTicketOfficeOrderByPositionAsc(TicketOffice ticketOffice);

    @Query("SELECT MAX(f.position) FROM Queue f WHERE f.ticketOffice = :ticketOffice")
    Integer findMaxPositionByTicketOffice(@Param("ticketOffice") TicketOffice ticketOffice);

    List<Queue> findAllByOrderByTicketOfficeAscPriorityDescPositionAsc();

    boolean existsByCustomerAndTicketOfficeNot(Customer customer, TicketOffice ticketOffice);
}
