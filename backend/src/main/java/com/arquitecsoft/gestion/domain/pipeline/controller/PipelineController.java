package com.arquitecsoft.gestion.domain.pipeline.controller;

import com.arquitecsoft.gestion.domain.pipeline.dto.*;
import com.arquitecsoft.gestion.domain.pipeline.service.PipelineService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pipelines")
public class PipelineController {

    private final PipelineService pipelineService;

    public PipelineController(PipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    // ==================== PIPELINE ====================

    @GetMapping
    public ResponseEntity<PageResponse<PipelineResponse>> listarPipelines(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String estado,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {

        if (pageSize > 100) {
            pageSize = 100;
        }

        PageResponse<PipelineResponse> response = pipelineService.listarPipelines(q, estado, page, pageSize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<PipelineResponse>> listarPipelinesActivos() {
        List<PipelineResponse> response = pipelineService.listarPipelinesActivos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PipelineResponse> obtenerPipeline(@PathVariable Long id) {
        PipelineResponse response = pipelineService.obtenerPipelinePorId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PipelineResponse> crearPipeline(@Valid @RequestBody PipelineCreateRequest request) {
        PipelineResponse response = pipelineService.crearPipeline(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PipelineResponse> actualizarPipeline(
            @PathVariable Long id,
            @Valid @RequestBody PipelineUpdateRequest request) {
        PipelineResponse response = pipelineService.actualizarPipeline(id, request);
        return ResponseEntity.ok(response);
    }

    // ==================== ETAPAS ====================

    @GetMapping("/{pipelineId}/etapas")
    public ResponseEntity<List<EtapaResponse>> listarEtapas(@PathVariable Long pipelineId) {
        List<EtapaResponse> response = pipelineService.listarEtapasPorPipeline(pipelineId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{pipelineId}/etapas")
    public ResponseEntity<EtapaResponse> crearEtapa(
            @PathVariable Long pipelineId,
            @Valid @RequestBody EtapaCreateRequest request) {
        EtapaResponse response = pipelineService.crearEtapa(pipelineId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{pipelineId}/etapas/{etapaId}")
    public ResponseEntity<EtapaResponse> actualizarEtapa(
            @PathVariable Long pipelineId,
            @PathVariable Long etapaId,
            @Valid @RequestBody EtapaUpdateRequest request) {
        EtapaResponse response = pipelineService.actualizarEtapa(pipelineId, etapaId, request);
        return ResponseEntity.ok(response);
    }
}
