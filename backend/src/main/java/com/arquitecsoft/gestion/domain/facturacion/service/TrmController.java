package com.arquitecsoft.gestion.domain.facturacion.controller;

import com.arquitecsoft.gestion.domain.facturacion.dto.TrmCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.TrmResponse;
import com.arquitecsoft.gestion.domain.facturacion.service.TrmService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/facturacion/trm")
public class TrmController {

    private final TrmService trmService;

    public TrmController(TrmService trmService) {
        this.trmService = trmService;
    }

    @GetMapping
    public ResponseEntity<List<TrmResponse>> listar() {
        return ResponseEntity.ok(trmService.listar());
    }

    @PostMapping
    public ResponseEntity<TrmResponse> crear(@Valid @RequestBody TrmCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trmService.crear(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        trmService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
