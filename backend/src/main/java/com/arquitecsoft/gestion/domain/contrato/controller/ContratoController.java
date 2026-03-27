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

    @GetMapping
    public ResponseEntity<PageResponse<ContratoResponse>> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false, name = "empresa_id") Long empresaId,
            @RequestParam(required = false) String moneda,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {
        if (pageSize > 100) pageSize = 100;
        return ResponseEntity.ok(contratoService.listar(estado, empresaId, moneda, page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(contratoService.obtenerPorId(id));
    }

    @GetMapping("/oportunidad/{oportunidadId}")
    public ResponseEntity<List<ContratoResponse>> listarPorOportunidad(@PathVariable Long oportunidadId) {
        return ResponseEntity.ok(contratoService.listarPorOportunidad(oportunidadId));
    }

    /**
     * Formalizar contrato desde una oportunidad GANADA.
     * Crea el contrato y marca la oportunidad como CONTRATADA.
     */
    @PostMapping("/formalizar")
    public ResponseEntity<ContratoResponse> formalizarContrato(@Valid @RequestBody FormalizarContratoRequest request) {
        ContratoResponse response = contratoService.formalizarContrato(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * @deprecated Usar /formalizar en su lugar
     */
    @PostMapping
    public ResponseEntity<ContratoResponse> crear(@Valid @RequestBody ContratoCreateRequest request) {
        ContratoResponse response = contratoService.crearDesdeProcesoCompletado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

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

    // Formas de pago
    @GetMapping("/{contratoId}/formas-pago")
    public ResponseEntity<List<FormaPagoResponse>> listarFormasPago(@PathVariable Long contratoId) {
        return ResponseEntity.ok(contratoService.listarFormasPago(contratoId));
    }

    @PostMapping("/{contratoId}/formas-pago")
    public ResponseEntity<FormaPagoResponse> crearFormaPago(@PathVariable Long contratoId, @Valid @RequestBody FormaPagoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoService.crearFormaPago(contratoId, request));
    }

    @DeleteMapping("/formas-pago/{formaPagoId}")
    public ResponseEntity<Void> eliminarFormaPago(@PathVariable Long formaPagoId) {
        contratoService.eliminarFormaPago(formaPagoId);
        return ResponseEntity.noContent().build();
    }

    // Modificaciones
    @GetMapping("/{contratoId}/modificaciones")
    public ResponseEntity<List<ModificacionResponse>> listarModificaciones(@PathVariable Long contratoId) {
        return ResponseEntity.ok(contratoService.listarModificaciones(contratoId));
    }

    @PostMapping("/{contratoId}/modificaciones")
    public ResponseEntity<ModificacionResponse> crearModificacion(@PathVariable Long contratoId, @Valid @RequestBody ModificacionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratoService.crearModificacion(contratoId, request));
    }
}
