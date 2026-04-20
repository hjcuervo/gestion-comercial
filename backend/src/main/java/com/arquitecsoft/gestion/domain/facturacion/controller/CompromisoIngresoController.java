package com.arquitecsoft.gestion.domain.facturacion.controller;

import com.arquitecsoft.gestion.domain.facturacion.dto.*;
import com.arquitecsoft.gestion.domain.facturacion.service.CompromisoGestionService;
import com.arquitecsoft.gestion.domain.facturacion.service.CompromisoIngresoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints REST del módulo de Compromisos de Ingreso (Mundo 3).
 *
 * Rutas híbridas:
 *  - Anidadas bajo /contratos para listar y crear (son operaciones sobre
 *    "los compromisos DE un contrato").
 *  - Planas bajo /compromisos para comandos que operan sobre un compromiso
 *    individual.
 *
 * Todos los endpoints son autenticados vía JwtAuthenticationFilter. El
 * usuarioId para auditoría lo obtiene SecurityUtils desde el token — NO se
 * espera en el body.
 */
@RestController
public class CompromisoIngresoController {

    private final CompromisoIngresoService compromisoService;
    private final CompromisoGestionService gestionService;

    public CompromisoIngresoController(CompromisoIngresoService compromisoService,
                                        CompromisoGestionService gestionService) {
        this.compromisoService = compromisoService;
        this.gestionService = gestionService;
    }

    // ========== Anidados bajo /contratos ==========

    @GetMapping("/api/v1/contratos/{contratoId}/compromisos")
    public ResponseEntity<List<CompromisoIngresoResponse>> listarPorContrato(
            @PathVariable Long contratoId) {
        return ResponseEntity.ok(compromisoService.listarPorContrato(contratoId));
    }

    /**
     * Crear compromiso — el contratoId del path prevalece sobre el del body
     * para evitar mismatches. El body puede omitir el campo.
     */
    @PostMapping("/api/v1/contratos/{contratoId}/compromisos")
    public ResponseEntity<CompromisoIngresoResponse> crear(
            @PathVariable Long contratoId,
            @Valid @RequestBody CompromisoIngresoCreateRequest request) {
        request.setContratoId(contratoId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compromisoService.crear(request));
    }

    // ========== Planos bajo /compromisos ==========

    @GetMapping("/api/v1/compromisos/{id}")
    public ResponseEntity<CompromisoIngresoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(compromisoService.obtenerPorId(id));
    }

    // ----- Comandos de transición -----

    @PostMapping("/api/v1/compromisos/{id}/iniciar-gestion")
    public ResponseEntity<CompromisoIngresoResponse> iniciarGestion(@PathVariable Long id) {
        return ResponseEntity.ok(compromisoService.iniciarGestion(id));
    }

    @PostMapping("/api/v1/compromisos/{id}/confirmar")
    public ResponseEntity<CompromisoIngresoResponse> confirmar(@PathVariable Long id) {
        return ResponseEntity.ok(compromisoService.confirmar(id));
    }

    @PostMapping("/api/v1/compromisos/{id}/reprogramar")
    public ResponseEntity<CompromisoIngresoResponse> reprogramar(
            @PathVariable Long id,
            @RequestBody CompromisoComandoRequest request) {
        return ResponseEntity.ok(compromisoService.reprogramar(id, request));
    }

    @PostMapping("/api/v1/compromisos/{id}/ajustar-monto")
    public ResponseEntity<CompromisoIngresoResponse> ajustarMonto(
            @PathVariable Long id,
            @RequestBody CompromisoComandoRequest request) {
        return ResponseEntity.ok(compromisoService.ajustarMonto(id, request));
    }

    @PostMapping("/api/v1/compromisos/{id}/marcar-perdido")
    public ResponseEntity<CompromisoIngresoResponse> marcarPerdido(
            @PathVariable Long id,
            @RequestBody CompromisoComandoRequest request) {
        return ResponseEntity.ok(compromisoService.marcarPerdido(id, request));
    }

    @PostMapping("/api/v1/compromisos/{id}/reactivar")
    public ResponseEntity<CompromisoIngresoResponse> reactivar(@PathVariable Long id) {
        return ResponseEntity.ok(compromisoService.reactivar(id));
    }

    // ----- Facturación contra el compromiso -----

    @PostMapping("/api/v1/compromisos/{id}/facturas")
    public ResponseEntity<CompromisoIngresoResponse> registrarFactura(
            @PathVariable Long id,
            @Valid @RequestBody RegistrarFacturaRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(compromisoService.registrarFactura(id, request));
    }

    @PostMapping("/api/v1/compromisos/{id}/facturas/revertir")
    public ResponseEntity<CompromisoIngresoResponse> revertirFactura(
            @PathVariable Long id,
            @Valid @RequestBody RevertirFacturaRequest request) {
        return ResponseEntity.ok(compromisoService.revertirFactura(id, request));
    }

    // ----- Timeline y gestiones (bitácora libre) -----

    @GetMapping("/api/v1/compromisos/{id}/eventos")
    public ResponseEntity<List<CompromisoEventoResponse>> listarEventos(@PathVariable Long id) {
        return ResponseEntity.ok(compromisoService.listarEventos(id));
    }

    @GetMapping("/api/v1/compromisos/{id}/gestiones")
    public ResponseEntity<List<CompromisoGestionResponse>> listarGestiones(@PathVariable Long id) {
        return ResponseEntity.ok(gestionService.listarPorCompromiso(id));
    }

    @PostMapping("/api/v1/compromisos/{id}/gestiones")
    public ResponseEntity<CompromisoGestionResponse> crearGestion(
            @PathVariable Long id,
            @Valid @RequestBody CompromisoGestionCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(gestionService.registrar(id, request));
    }
}
