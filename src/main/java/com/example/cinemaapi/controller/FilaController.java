package com.example.cinemaapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.cinemaapi.dto.AddToQueueDTO;
import java.util.List;
import com.example.cinemaapi.model.Fila;
import com.example.cinemaapi.service.FilaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/queues")
@RequiredArgsConstructor
public class FilaController {
    private final FilaService filaService;

    @PostMapping("/enqueue")
    public ResponseEntity<Fila> adicionarCliente(@RequestBody AddToQueueDTO dto) {
        return ResponseEntity.ok(filaService.adicionarClienteAFila(dto.getClienteId(), dto.getGuicheId()));
    }

    @PostMapping("/dequeue")
    public ResponseEntity<Void> atenderProximoCliente(@RequestParam Long guicheId) {
        filaService.atenderProximoCliente(guicheId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Fila>> visualizarFilas() {
        return ResponseEntity.ok(filaService.visualizarFilas());
    }
}
