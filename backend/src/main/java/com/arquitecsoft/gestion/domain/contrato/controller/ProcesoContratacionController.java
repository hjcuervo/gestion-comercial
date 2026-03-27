package com.arquitecsoft.gestion.domain.contrato.controller;

import com.arquitecsoft.gestion.domain.contrato.dto.*;
import com.arquitecsoft.gestion.domain.contrato.service.ProcesoContratacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/procesos-contratacion")
public class ProcesoContratacionController {

    private final ProcesoContratacionService procesoService;

    public ProcesoContratacionController(ProcesoContratacionService procesoService) {
        this.procesoService = procesoService;
    }

    @PostMapping
    public ResponseEntity<ProcesoContratacionResponse> iniciarProceso(
            @Valid @RequestBody ProcesoContratacionCreateRequest request) {
        ProcesoContratacionResponse response = procesoService.iniciarProceso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcesoContratacionResponse> obtenerPorId(@PathVariable Long id) {
        ProcesoContratacionResponse response = procesoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProcesoContratacionResponse>> listar(
            @RequestParam(required = false, name = "oportunidad_id") Long oportunidadId,
            @RequestParam(required = false) String estado) {
        if (oportunidadId != null) {
            return ResponseEntity.ok(procesoService.listarPorOportunidad(oportunidadId));
        }
        if (estado != null) {
            return ResponseEntity.ok(procesoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(procesoService.listarPorEstado("EN_CURSO"));
    }

    @PostMapping("/{id}/mover-etapa")
    public ResponseEntity<ProcesoContratacionResponse> moverEtapa(
            @PathVariable Long id,
            @Valid @RequestBody MoverEtapaProcesoRequest request) {
        ProcesoContratacionResponse response = procesoService.moverEtapa(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/completar")
    public ResponseEntity<ProcesoContratacionResponse> completarProceso(@PathVariable Long id) {
        ProcesoContratacionResponse response = procesoService.completarProceso(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<ProcesoContratacionResponse> cancelarProceso(@PathVariable Long id) {
        ProcesoContratacionResponse response = procesoService.cancelarProceso(id);
        return ResponseEntity.ok(response);
    }
}
