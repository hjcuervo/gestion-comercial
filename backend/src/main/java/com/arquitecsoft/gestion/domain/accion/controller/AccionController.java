package com.arquitecsoft.gestion.domain.accion.controller;

import com.arquitecsoft.gestion.domain.accion.dto.AccionResponse;
import com.arquitecsoft.gestion.domain.accion.dto.CompletarAccionRequest;
import com.arquitecsoft.gestion.domain.accion.dto.EventoResponse;
import com.arquitecsoft.gestion.domain.accion.dto.OmitirAccionRequest;
import com.arquitecsoft.gestion.domain.accion.dto.PosponerAccionRequest;
import com.arquitecsoft.gestion.domain.accion.dto.ReasignarAccionRequest;
import com.arquitecsoft.gestion.domain.accion.service.AccionService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acciones")
public class AccionController {

    private final AccionService accionService;

    public AccionController(AccionService accionService) {
        this.accionService = accionService;
    }

    /** Cola "Hoy" del usuario actual (dosificada a tope_visible). */
    @GetMapping("/cola")
    public ResponseEntity<PageResponse<AccionResponse>> cola(
            @RequestParam(defaultValue = "50", name = "tope_visible") int topeVisible) {
        return ResponseEntity.ok(accionService.obtenerCola(topeVisible));
    }

    @PostMapping("/{id}/tomar")
    public ResponseEntity<AccionResponse> tomar(@PathVariable Long id) {
        return ResponseEntity.ok(accionService.tomar(id));
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<AccionResponse> completar(@PathVariable Long id,
            @Valid @RequestBody CompletarAccionRequest request) {
        return ResponseEntity.ok(accionService.completar(id, request.getDisposicionId()));
    }

    @PostMapping("/{id}/posponer")
    public ResponseEntity<AccionResponse> posponer(@PathVariable Long id,
            @Valid @RequestBody PosponerAccionRequest request) {
        return ResponseEntity.ok(accionService.posponer(id, request.getMotivo(), request.getNuevaFecha()));
    }

    @PostMapping("/{id}/omitir")
    public ResponseEntity<AccionResponse> omitir(@PathVariable Long id,
            @Valid @RequestBody OmitirAccionRequest request) {
        return ResponseEntity.ok(accionService.omitir(id, request.getMotivo()));
    }

    // Reasignación. TODO: restringir a rol Admin al cablear la API de roles del proyecto.
    @PostMapping("/{id}/reasignar")
    public ResponseEntity<AccionResponse> reasignar(@PathVariable Long id,
            @Valid @RequestBody ReasignarAccionRequest request) {
        return ResponseEntity.ok(accionService.reasignar(id, request.getNuevoResponsableId()));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<List<EventoResponse>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(accionService.obtenerHistorial(id));
    }
}
