package com.arquitecsoft.gestion.domain.oportunidad.controller;

import com.arquitecsoft.gestion.domain.oportunidad.dto.OportunidadStatsResponse;
import com.arquitecsoft.gestion.domain.oportunidad.service.OportunidadStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/oportunidades/stats")
public class OportunidadStatsController {

    private final OportunidadStatsService statsService;

    public OportunidadStatsController(OportunidadStatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public ResponseEntity<OportunidadStatsResponse> obtenerEstadisticas(
            @RequestParam(required = false, name = "pipeline_id") Long pipelineId,
            @RequestParam(required = false, defaultValue = "12") Integer meses) {
        OportunidadStatsResponse response = statsService.obtenerEstadisticas(pipelineId, meses);
        return ResponseEntity.ok(response);
    }
}
