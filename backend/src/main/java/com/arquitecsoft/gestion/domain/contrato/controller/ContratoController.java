package com.arquitecsoft.gestion.domain.contrato.controller;

import com.arquitecsoft.gestion.domain.contrato.dto.*;
import com.arquitecsoft.gestion.domain.contrato.service.ContratoService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contratos")
public class ContratoController {

    private final ContratoService contratoService;

    public ContratoController(ContratoService contratoService) {
        this.contratoService = contratoService;
    }

    // ==================== CONTRATO ====================

    @PostMapping
    public ResponseEntity<ContratoResponse> crear(@Valid @RequestBody ContratoCreateRequest request) {
        ContratoResponse response = contratoService.crearDesdeProcesoCompletado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ContratoResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false, name = "empresa_id") Long empresaId,
            @RequestParam(required = false) String moneda,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {
        if (pageSize > 100) pageSize = 100;
        PageResponse<ContratoResponse> response = contratoService.listar(estado, empresaId, moneda, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> obtenerPorId(@PathVariable Long id) {
        ContratoResponse response = contratoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/oportunidad/{oportunidadId}")
    public ResponseEntity<List<ContratoResponse>> listarPorOportunidad(@PathVariable Long oportunidadId) {
        List<ContratoResponse> response = contratoService.listarPorOportunidad(oportunidadId);
        return ResponseEntity.ok(response);
    }

    // ==================== CAMBIOS DE ESTADO ====================

    @PostMapping("/{id}/suspender")
    public ResponseEntity<ContratoResponse> suspender(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.suspender(id));
    }

    @PostMapping("/{id}/reiniciar")
    public ResponseEntity<ContratoResponse> reiniciar(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.reiniciar(id));
    }

    @PostMapping("/{id}/terminar")
    public ResponseEntity<ContratoResponse> terminar(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.terminar(id));
    }

    @PostMapping("/{id}/liquidar")
    public ResponseEntity<ContratoResponse> liquidar(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.liquidar(id));
    }

    // ==================== FORMAS DE PAGO ====================

    @GetMapping("/{contratoId}/formas-pago")
    public ResponseEntity<List<FormaPagoResponse>> listarFormasPago(@PathVariable Long contratoId) {
        return ResponseEntity.ok(contratoService.listarFormasPago(contratoId));
    }

    @PostMapping("/{contratoId}/formas-pago")
    public ResponseEntity<FormaPagoResponse> crearFormaPago(
            @PathVariable Long contratoId,
            @Valid @RequestBody FormaPagoCreateRequest request) {
        FormaPagoResponse response = contratoService.crearFormaPago(contratoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/formas-pago/{formaPagoId}")
    public ResponseEntity<Void> eliminarFormaPago(@PathVariable Long formaPagoId) {
        contratoService.eliminarFormaPago(formaPagoId);
        return ResponseEntity.noContent().build();
    }

    // ==================== MODIFICACIONES ====================

    @GetMapping("/{contratoId}/modificaciones")
    public ResponseEntity<List<ModificacionResponse>> listarModificaciones(@PathVariable Long contratoId) {
        return ResponseEntity.ok(contratoService.listarModificaciones(contratoId));
    }

    @PostMapping("/{contratoId}/modificaciones")
    public ResponseEntity<ModificacionResponse> crearModificacion(
            @PathVariable Long contratoId,
            @Valid @RequestBody ModificacionCreateRequest request) {
        ModificacionResponse response = contratoService.crearModificacion(contratoId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
