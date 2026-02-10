package com.arquitecsoft.gestion.domain.persona.controller;

import com.arquitecsoft.gestion.domain.persona.dto.*;
import com.arquitecsoft.gestion.domain.persona.service.PersonaService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/personas")
public class PersonaController {

    private final PersonaService personaService;

    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PersonaResponse>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, name = "empresa_id") Long empresaId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {

        if (pageSize > 100) {
            pageSize = 100;
        }

        PageResponse<PersonaResponse> response = personaService.listar(q, empresaId, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponse> obtenerPorId(@PathVariable Long id) {
        PersonaResponse response = personaService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PersonaResponse> crear(@Valid @RequestBody PersonaCreateRequest request) {
        PersonaResponse response = personaService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PersonaUpdateRequest request) {
        PersonaResponse response = personaService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/empresas")
    public ResponseEntity<PersonaEmpresaResponse> asociarEmpresa(
            @PathVariable Long id,
            @Valid @RequestBody PersonaEmpresaCreateRequest request) {
        PersonaEmpresaResponse response = personaService.asociarEmpresa(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
