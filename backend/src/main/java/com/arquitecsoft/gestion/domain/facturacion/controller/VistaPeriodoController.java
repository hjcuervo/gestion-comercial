package com.arquitecsoft.gestion.domain.facturacion.controller;

import com.arquitecsoft.gestion.domain.facturacion.dto.VistaPeriodoResponse;
import com.arquitecsoft.gestion.domain.facturacion.service.VistaPeriodoService;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

/**
 * Endpoint de la vista agregada por periodo (dashboard de compromisos pendientes).
 *
 * GET /api/v1/compromisos/vista-periodo
 *
 * Parámetros:
 *   anioMes     (opcional, "YYYY-MM") — si se omite, toma el mes actual del sistema
 *   contratoId  (opcional)
 *   empresaId   (opcional)
 */
@RestController
public class VistaPeriodoController {

    private final VistaPeriodoService vistaService;

    public VistaPeriodoController(VistaPeriodoService vistaService) {
        this.vistaService = vistaService;
    }

    @GetMapping("/api/v1/compromisos/vista-periodo")
    public ResponseEntity<VistaPeriodoResponse> obtenerVista(
            @RequestParam(required = false) String anioMes,
            @RequestParam(required = false) Long contratoId,
            @RequestParam(required = false) Long empresaId) {

        YearMonth ym = null;
        if (anioMes != null && !anioMes.isBlank()) {
            try {
                ym = YearMonth.parse(anioMes);
            } catch (DateTimeParseException e) {
                throw new BusinessException("VALIDATION_ERROR",
                    "Formato de anioMes inválido. Esperado YYYY-MM. Recibido: " + anioMes);
            }
        }

        return ResponseEntity.ok(vistaService.obtenerVista(ym, contratoId, empresaId));
    }
}
