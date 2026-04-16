package com.arquitecsoft.gestion.domain.facturacion.controller;

import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaResponse;
import com.arquitecsoft.gestion.domain.facturacion.service.FacturaService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/facturacion/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<FacturaResponse>> listar(
            @RequestParam(required = false, name = "empresa_id") Long empresaId,
            @RequestParam(required = false) String moneda,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {
        if (pageSize > 100) pageSize = 100;
        return ResponseEntity.ok(facturaService.listar(empresaId, moneda, page, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<FacturaResponse> crear(@Valid @RequestBody FacturaCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(facturaService.crear(request));
    }

    /**
     * Cruza una factura con una forma de pago.
     * Body opcional: { "valorFacturado": 9500000 }
     * Si no se envía valorFacturado, se usa el valor_total de la factura.
     */
    @PostMapping("/{facturaId}/cruzar/{formaPagoId}")
    public ResponseEntity<Void> cruzarConFormaPago(
            @PathVariable Long facturaId,
            @PathVariable Long formaPagoId,
            @RequestBody(required = false) Map<String, Object> body) {
        BigDecimal valorFacturado = null;
        if (body != null && body.containsKey("valorFacturado")) {
            valorFacturado = new BigDecimal(body.get("valorFacturado").toString());
        }
        facturaService.cruzarConFormaPago(facturaId, formaPagoId, valorFacturado);
        return ResponseEntity.ok().build();
    }

    /**
     * Descruza una forma de pago de su factura.
     */
    @PostMapping("/descruzar/{formaPagoId}")
    public ResponseEntity<Void> descruzarFormaPago(@PathVariable Long formaPagoId) {
        facturaService.descruzarFormaPago(formaPagoId);
        return ResponseEntity.ok().build();
    }
}
