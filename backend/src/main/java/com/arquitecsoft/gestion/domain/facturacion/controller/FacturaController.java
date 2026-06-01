package com.arquitecsoft.gestion.domain.facturacion.controller;

import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaAnularRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaResponse;
import com.arquitecsoft.gestion.domain.facturacion.service.FacturaService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints REST de facturas.
 *
 * CRUD puro. Las operaciones de aplicación/reversión contra compromisos
 * viven en CompromisoIngresoController.registrarFactura y .revertirFactura.
 */
@RestController
@RequestMapping("/api/v1/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<FacturaResponse>> listar(
            @RequestParam(required = false, name = "empresa_id") Long empresaId,
            @RequestParam(required = false) String moneda,
            @RequestParam(required = false) Boolean disponibles,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {
        if (pageSize > 100) pageSize = 100;
        return ResponseEntity.ok(facturaService.listar(empresaId, moneda, disponibles, page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<FacturaResponse> crear(@Valid @RequestBody FacturaCreateRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(facturaService.crear(request));
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<FacturaResponse> anular(
            @PathVariable Long id,
            @Valid @RequestBody FacturaAnularRequest request) {
        return ResponseEntity.ok(facturaService.anular(id, request));
    }
}
