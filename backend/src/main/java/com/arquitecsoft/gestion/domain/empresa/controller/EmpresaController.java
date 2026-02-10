package com.arquitecsoft.gestion.domain.empresa.controller;

import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaCreateRequest;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaResponse;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaUpdateRequest;
import com.arquitecsoft.gestion.domain.empresa.service.EmpresaService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/empresas")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<EmpresaResponse>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {

        if (pageSize > 100) {
            pageSize = 100;
        }

        PageResponse<EmpresaResponse> response = empresaService.listar(q, tipo, estado, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> obtenerPorId(@PathVariable Long id) {
        EmpresaResponse response = empresaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> crear(@Valid @RequestBody EmpresaCreateRequest request) {
        EmpresaResponse response = empresaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaUpdateRequest request) {
        EmpresaResponse response = empresaService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }
}
