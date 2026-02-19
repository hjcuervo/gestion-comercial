package com.arquitecsoft.gestion.domain.oportunidad.controller;

import com.arquitecsoft.gestion.domain.oportunidad.dto.*;
import com.arquitecsoft.gestion.domain.oportunidad.service.OportunidadService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/oportunidades")
public class OportunidadController {

    private final OportunidadService oportunidadService;

    public OportunidadController(OportunidadService oportunidadService) {
        this.oportunidadService = oportunidadService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<OportunidadResponse>> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false, name = "empresa_id") Long empresaId,
            @RequestParam(required = false, name = "pipeline_id") Long pipelineId,
            @RequestParam(required = false, name = "etapa_id") Long etapaId,
            @RequestParam(required = false, name = "estado") String estadoMacro,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {

        if (pageSize > 100) {
            pageSize = 100;
        }

        PageResponse<OportunidadResponse> response = oportunidadService.listar(
                q, empresaId, pipelineId, etapaId, estadoMacro, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OportunidadResponse> obtenerPorId(@PathVariable Long id) {
        OportunidadResponse response = oportunidadService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OportunidadResponse> crear(@Valid @RequestBody OportunidadCreateRequest request) {
        OportunidadResponse response = oportunidadService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OportunidadResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody OportunidadUpdateRequest request) {
        OportunidadResponse response = oportunidadService.actualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/mover-etapa")
    public ResponseEntity<OportunidadResponse> moverEtapa(
            @PathVariable Long id,
            @Valid @RequestBody MoverEtapaRequest request) {
        OportunidadResponse response = oportunidadService.moverEtapa(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<OportunidadResponse> cerrar(
            @PathVariable Long id,
            @Valid @RequestBody CerrarOportunidadRequest request) {
        OportunidadResponse response = oportunidadService.cerrar(id, request);
        return ResponseEntity.ok(response);
    }
}
