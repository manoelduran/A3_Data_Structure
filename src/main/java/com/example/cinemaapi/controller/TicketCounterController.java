package com.example.cinemaapi.controller;

import java.util.List;
import java.time.Duration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.cinemaapi.dto.ClientDTO;
import com.example.cinemaapi.dto.TicketCounterDTO;
import com.example.cinemaapi.model.Client;
import com.example.cinemaapi.service.CinemaService;

@RestController
@RequestMapping("/api/cinema")
public class TicketCounterController {

    private final CinemaService cinemaService;

    public TicketCounterController(CinemaService cinemaService) {
        this.cinemaService = cinemaService;
    }

    @PostMapping("/clients")
    public Client addClient(@RequestBody ClientDTO dto) {
        System.out.print(dto);
        return cinemaService.addClient(dto.name, dto.type);
    }

    @GetMapping("/ticket-counters")
    public List<TicketCounterDTO> listTicketCounters() {
        System.err.println("Listando guichês");
        List<TicketCounterDTO> ok = cinemaService.listTicketCounters();
        System.err.print(ok);
        return cinemaService.listTicketCounters();
    }

    @PostMapping("/ticket-counters/{id}/stop")
    public void pausarGuiche(@PathVariable int id) {
        cinemaService.stopTicketCounter(id);
    }

    @PostMapping("/ticket-counters/{id}/assist")
    public Client assisClient(@PathVariable int id) {
        return cinemaService.assistClient(id);
    }

    @GetMapping("/average-waiting-time")
    public ResponseEntity<String> getAverageWaitingTime() {
        Duration avg = cinemaService.getAverageWaitingTime();
        return ResponseEntity.ok("Tempo médio: " + avg.toMinutes() + " minutos");
    }
}