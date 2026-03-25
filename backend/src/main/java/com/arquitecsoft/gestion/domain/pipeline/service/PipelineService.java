package com.arquitecsoft.gestion.domain.pipeline.service;

import com.arquitecsoft.gestion.domain.pipeline.dto.*;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa.EstadoEtapa;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline.EstadoPipeline;
import com.arquitecsoft.gestion.domain.pipeline.repository.GcEtapaRepository;
import com.arquitecsoft.gestion.domain.pipeline.repository.GcPipelineRepository;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PipelineService {

    private final GcPipelineRepository pipelineRepository;
    private final GcEtapaRepository etapaRepository;
    private final SecurityUtils securityUtils;

    public PipelineService(GcPipelineRepository pipelineRepository,
                           GcEtapaRepository etapaRepository,
                           SecurityUtils securityUtils) {
        this.pipelineRepository = pipelineRepository;
        this.etapaRepository = etapaRepository;
        this.securityUtils = securityUtils;
    }

    // ==================== PIPELINE ====================

    @Transactional(readOnly = true)
    public PageResponse<PipelineResponse> listarPipelines(String q, String estado, String ambito, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("nombre").ascending());

        EstadoPipeline estadoEnum = null;
        if (StringUtils.hasText(estado)) {
            try {
                estadoEnum = EstadoPipeline.valueOf(estado);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de pipeline invalido: " + estado);
            }
        }

        String ambitoFilter = StringUtils.hasText(ambito) ? ambito : null;

        Page<GcPipeline> pageResult = pipelineRepository.findWithFilters(q, estadoEnum, ambitoFilter, pageable);

        return PageResponse.from(pageResult, PipelineResponse::fromEntitySimple);
    }

    @Transactional(readOnly = true)
    public List<PipelineResponse> listarPipelinesActivos(String ambito) {
        List<GcPipeline> pipelines;
        if (StringUtils.hasText(ambito)) {
            pipelines = pipelineRepository.findAllActivosByAmbito(ambito);
        } else {
            pipelines = pipelineRepository.findAllActivos();
        }
        return pipelines.stream()
                .map(PipelineResponse::fromEntitySimple)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PipelineResponse obtenerPipelinePorId(Long id) {
        GcPipeline pipeline = pipelineRepository.findByIdWithEtapas(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Pipeline no encontrado con ID: " + id));

        return PipelineResponse.fromEntity(pipeline);
    }

    @Transactional
    public PipelineResponse crearPipeline(PipelineCreateRequest request) {
        if (pipelineRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
            throw new BusinessException("DUPLICATE_ERROR", "Ya existe un pipeline con el nombre: " + request.getNombre());
        }

        GcPipeline pipeline = new GcPipeline();
        pipeline.setNombre(request.getNombre().trim());
        pipeline.setAmbito(StringUtils.hasText(request.getAmbito()) ? request.getAmbito() : "COMERCIAL");
        pipeline.setVersion(1);
        pipeline.setEstado(EstadoPipeline.ACTIVO);
        pipeline.setEsDefault(0);
        pipeline.setCreadoPor(securityUtils.getCurrentUserId());

        pipeline = pipelineRepository.save(pipeline);

        return PipelineResponse.fromEntity(pipeline);
    }

    @Transactional
    public PipelineResponse actualizarPipeline(Long id, PipelineUpdateRequest request) {
        GcPipeline pipeline = pipelineRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Pipeline no encontrado con ID: " + id));

        if (StringUtils.hasText(request.getNombre())) {
            if (!pipeline.getNombre().equalsIgnoreCase(request.getNombre().trim()) &&
                    pipelineRepository.existsByNombreIgnoreCase(request.getNombre().trim())) {
                throw new BusinessException("DUPLICATE_ERROR", "Ya existe un pipeline con el nombre: " + request.getNombre());
            }
            pipeline.setNombre(request.getNombre().trim());
        }

        if (request.getActivo() != null) {
            pipeline.setEstado(request.getActivo() ? EstadoPipeline.ACTIVO : EstadoPipeline.INACTIVO);
        }

        pipeline.setModificadoPor(securityUtils.getCurrentUserId());
        pipeline = pipelineRepository.save(pipeline);

        return PipelineResponse.fromEntity(pipeline);
    }

    // ==================== ETAPA ====================

    @Transactional(readOnly = true)
    public List<EtapaResponse> listarEtapasPorPipeline(Long pipelineId) {
        if (!pipelineRepository.existsById(pipelineId)) {
            throw new BusinessException("NOT_FOUND", "Pipeline no encontrado con ID: " + pipelineId);
        }

        return etapaRepository.findByPipelineIdOrderByOrden(pipelineId).stream()
                .map(EtapaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public EtapaResponse crearEtapa(Long pipelineId, EtapaCreateRequest request) {
        GcPipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Pipeline no encontrado con ID: " + pipelineId));

        if (etapaRepository.existsByPipelineIdAndNombreIgnoreCase(pipelineId, request.getNombre().trim())) {
            throw new BusinessException("DUPLICATE_ERROR",
                "Ya existe una etapa con el nombre '" + request.getNombre() + "' en este pipeline");
        }

        GcEtapa etapa = new GcEtapa();
        etapa.setPipeline(pipeline);
        etapa.setNombre(request.getNombre().trim());
        etapa.setColor(request.getColor());
        etapa.setProbabilidadSugerida(request.getProbabilidadSugerida());
        etapa.setModoBloqueo(request.getModoBloqueo() != null ? request.getModoBloqueo() : 0);
        etapa.setEstado(EstadoEtapa.ACTIVA);

        if (request.getOrden() != null) {
            etapa.setOrden(request.getOrden());
        } else {
            Integer maxOrden = etapaRepository.findMaxOrdenByPipelineId(pipelineId);
            etapa.setOrden(maxOrden != null ? maxOrden + 1 : 1);
        }

        etapa = etapaRepository.save(etapa);

        return EtapaResponse.fromEntity(etapa);
    }

    @Transactional
    public EtapaResponse actualizarEtapa(Long pipelineId, Long etapaId, EtapaUpdateRequest request) {
        if (!pipelineRepository.existsById(pipelineId)) {
            throw new BusinessException("NOT_FOUND", "Pipeline no encontrado con ID: " + pipelineId);
        }

        GcEtapa etapa = etapaRepository.findById(etapaId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Etapa no encontrada con ID: " + etapaId));

        if (!etapa.getPipeline().getId().equals(pipelineId)) {
            throw new BusinessException("VALIDATION_ERROR", "La etapa no pertenece al pipeline especificado");
        }

        if (StringUtils.hasText(request.getNombre())) {
            if (!etapa.getNombre().equalsIgnoreCase(request.getNombre().trim()) &&
                    etapaRepository.existsByPipelineIdAndNombreIgnoreCase(pipelineId, request.getNombre().trim())) {
                throw new BusinessException("DUPLICATE_ERROR",
                    "Ya existe una etapa con el nombre '" + request.getNombre() + "' en este pipeline");
            }
            etapa.setNombre(request.getNombre().trim());
        }

        if (request.getOrden() != null) etapa.setOrden(request.getOrden());
        if (request.getProbabilidadSugerida() != null) etapa.setProbabilidadSugerida(request.getProbabilidadSugerida());
        if (request.getColor() != null) etapa.setColor(request.getColor());
        if (request.getModoBloqueo() != null) etapa.setModoBloqueo(request.getModoBloqueo());

        if (StringUtils.hasText(request.getEstado())) {
            try {
                etapa.setEstado(EstadoEtapa.valueOf(request.getEstado()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de etapa invalido: " + request.getEstado());
            }
        }

        etapa = etapaRepository.save(etapa);
        return EtapaResponse.fromEntity(etapa);
    }
}
