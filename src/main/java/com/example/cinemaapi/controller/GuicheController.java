package com.example.cinemaapi.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import com.example.cinemaapi.dto.CreateGuicheDTO;
import com.example.cinemaapi.model.Guiche;
import com.example.cinemaapi.service.GuicheService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/guiches")
@RequiredArgsConstructor
public class GuicheController {
    private final GuicheService guicheService;

    @PostMapping
    public ResponseEntity<Guiche> criarGuiche(@RequestBody CreateGuicheDTO request) {
        return ResponseEntity.ok(guicheService.criarGuiche(request.getNumero()));
    }

    @PutMapping("/{id}/stop")
    public ResponseEntity<Guiche> entrarEmPausa(@PathVariable Long id) {
        return ResponseEntity.ok(guicheService.entrarEmPausa(id));
    }

    @PutMapping("/{id}/active")
    public ResponseEntity<Guiche> retornarDoPausa(@PathVariable Long id) {
        return ResponseEntity.ok(guicheService.retornarDoPausa(id));
    }

    @GetMapping
    public ResponseEntity<List<Guiche>> listarTodos() {
        return ResponseEntity.ok(guicheService.listarTodos());
    }
}