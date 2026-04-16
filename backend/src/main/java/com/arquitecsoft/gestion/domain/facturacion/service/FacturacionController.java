package com.arquitecsoft.gestion.domain.facturacion.controller;

import com.arquitecsoft.gestion.domain.facturacion.dto.EmisionPendienteResponse;
import com.arquitecsoft.gestion.domain.facturacion.dto.GestionCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.GestionResponse;
import com.arquitecsoft.gestion.domain.facturacion.service.EmisionPendienteService;
import com.arquitecsoft.gestion.domain.facturacion.service.FormaPagoGestionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/facturacion")
public class FacturacionController {

    private final EmisionPendienteService emisionPendienteService;
    private final FormaPagoGestionService gestionService;

    public FacturacionController(EmisionPendienteService emisionPendienteService,
                                  FormaPagoGestionService gestionService) {
        this.emisionPendienteService = emisionPendienteService;
        this.gestionService = gestionService;
    }

    /**
     * Emisiones pendientes — datos para el tablero.
     * Params opcionales: anio, mes, estado (PENDIENTE|FACTURADA)
     */
    @GetMapping("/emisiones-pendientes")
    public ResponseEntity<List<EmisionPendienteResponse>> emisionesPendientes(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(emisionPendienteService.obtenerPendientes(anio, mes, estado));
    }

    /**
     * KPIs del tablero de facturación.
     */
    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> kpis() {
        return ResponseEntity.ok(emisionPendienteService.obtenerKpis());
    }

    /**
     * Bitácora de gestión de una forma de pago.
     */
    @GetMapping("/gestiones/{formaPagoId}")
    public ResponseEntity<List<GestionResponse>> listarGestiones(@PathVariable Long formaPagoId) {
        return ResponseEntity.ok(gestionService.listarPorFormaPago(formaPagoId));
    }

    /**
     * Registrar nueva entrada de gestión en la bitácora.
     */
    @PostMapping("/gestiones/{formaPagoId}")
    public ResponseEntity<GestionResponse> registrarGestion(
            @PathVariable Long formaPagoId,
            @Valid @RequestBody GestionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gestionService.registrar(formaPagoId, request));
    }
}
