package com.arquitecsoft.gestion.domain.actividad.controller;

import com.arquitecsoft.gestion.domain.actividad.dto.*;
import com.arquitecsoft.gestion.domain.actividad.service.ActividadService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/actividades")
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    // ==================== ACTIVIDADES ====================

    @GetMapping
    public ResponseEntity<PageResponse<ActividadResponse>> listarActividades(
            @RequestParam(required = false, name = "oportunidad_id") Long oportunidadId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {

        if (pageSize > 100) {
            pageSize = 100;
        }

        PageResponse<ActividadResponse> response = actividadService.listarActividades(oportunidadId, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActividadResponse> obtenerActividad(@PathVariable Long id) {
        ActividadResponse response = actividadService.obtenerActividadPorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ActividadResponse> crearActividad(@Valid @RequestBody ActividadCreateRequest request) {
        ActividadResponse response = actividadService.crearActividad(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== COMPROMISOS ====================

    @GetMapping("/{actividadId}/compromisos")
    public ResponseEntity<List<CompromisoResponse>> listarCompromisos(@PathVariable Long actividadId) {
        List<CompromisoResponse> response = actividadService.listarCompromisosPorActividad(actividadId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{actividadId}/compromisos")
    public ResponseEntity<CompromisoResponse> crearCompromiso(
            @PathVariable Long actividadId,
            @Valid @RequestBody CompromisoCreateRequest request) {
        CompromisoResponse response = actividadService.crearCompromiso(actividadId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/compromisos/{compromisoId}")
    public ResponseEntity<CompromisoResponse> actualizarCompromiso(
            @PathVariable Long compromisoId,
            @Valid @RequestBody CompromisoUpdateRequest request) {
        CompromisoResponse response = actividadService.actualizarCompromiso(compromisoId, request);
        return ResponseEntity.ok(response);
    }
}
