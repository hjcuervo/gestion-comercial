package com.arquitecsoft.gestion.domain.contrato.service;

import com.arquitecsoft.gestion.domain.contrato.dto.*;
import com.arquitecsoft.gestion.domain.contrato.entity.GcProcesoContratacion;
import com.arquitecsoft.gestion.domain.contrato.entity.GcProcesoContratacion.EstadoProceso;
import com.arquitecsoft.gestion.domain.contrato.repository.GcProcesoContratacionRepository;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad.EstadoMacro;
import com.arquitecsoft.gestion.domain.oportunidad.repository.GcOportunidadRepository;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline;
import com.arquitecsoft.gestion.domain.pipeline.repository.GcEtapaRepository;
import com.arquitecsoft.gestion.domain.pipeline.repository.GcPipelineRepository;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcesoContratacionService {

    private final GcProcesoContratacionRepository procesoRepository;
    private final GcOportunidadRepository oportunidadRepository;
    private final GcPipelineRepository pipelineRepository;
    private final GcEtapaRepository etapaRepository;
    private final SecurityUtils securityUtils;

    public ProcesoContratacionService(
            GcProcesoContratacionRepository procesoRepository,
            GcOportunidadRepository oportunidadRepository,
            GcPipelineRepository pipelineRepository,
            GcEtapaRepository etapaRepository,
            SecurityUtils securityUtils) {
        this.procesoRepository = procesoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.pipelineRepository = pipelineRepository;
        this.etapaRepository = etapaRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public ProcesoContratacionResponse obtenerPorId(Long id) {
        GcProcesoContratacion proceso = procesoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Proceso de contratación no encontrado con ID: " + id));
        return ProcesoContratacionResponse.fromEntity(proceso);
    }

    @Transactional(readOnly = true)
    public List<ProcesoContratacionResponse> listarPorOportunidad(Long oportunidadId) {
        return procesoRepository.findByOportunidadId(oportunidadId).stream()
                .map(ProcesoContratacionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProcesoContratacionResponse> listarPorEstado(String estado) {
        EstadoProceso estadoEnum;
        try {
            estadoEnum = EstadoProceso.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("VALIDATION_ERROR", "Estado inválido: " + estado);
        }
        return procesoRepository.findByEstado(estadoEnum).stream()
                .map(ProcesoContratacionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * RN-01: Solo se puede crear desde oportunidad GANADA
     * RN-02: Solo puede existir un proceso EN_CURSO por oportunidad
     */
    @Transactional
    public ProcesoContratacionResponse iniciarProceso(ProcesoContratacionCreateRequest request) {
        // Validar oportunidad existe y está GANADA
        GcOportunidad oportunidad = oportunidadRepository.findByIdWithRelations(request.getOportunidadId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Oportunidad no encontrada con ID: " + request.getOportunidadId()));

        if (oportunidad.getEstadoMacro() != EstadoMacro.GANADA) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se puede iniciar un proceso de contratación para oportunidades con estado GANADA. Estado actual: " + oportunidad.getEstadoMacro());
        }

        // Validar que no existe un proceso EN_CURSO para esta oportunidad
        if (procesoRepository.existsByOportunidadIdAndEstado(request.getOportunidadId(), EstadoProceso.EN_CURSO)) {
            throw new BusinessException("BUSINESS_ERROR",
                "Ya existe un proceso de contratación en curso para esta oportunidad");
        }

        // Validar pipeline
        GcPipeline pipeline = pipelineRepository.findByIdWithEtapas(request.getPipelineId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Pipeline no encontrado con ID: " + request.getPipelineId()));

        // Validar que el pipeline es de contratación
        if (!"CONTRATACION".equals(pipeline.getAmbito())) {
            throw new BusinessException("VALIDATION_ERROR",
                "El pipeline seleccionado no es de ámbito CONTRATACION. Ámbito actual: " + pipeline.getAmbito());
        }

        // Obtener primera etapa activa
        List<GcEtapa> etapas = etapaRepository.findActivasByPipelineId(pipeline.getId());
        if (etapas.isEmpty()) {
            throw new BusinessException("VALIDATION_ERROR",
                "El pipeline no tiene etapas activas configuradas");
        }
        GcEtapa primeraEtapa = etapas.get(0);

        // Crear proceso
        GcProcesoContratacion proceso = new GcProcesoContratacion();
        proceso.setOportunidad(oportunidad);
        proceso.setEmpresa(oportunidad.getEmpresa());
        proceso.setPipeline(pipeline);
        proceso.setEtapa(primeraEtapa);
        proceso.setEstado(EstadoProceso.EN_CURSO);
        proceso.setFechaInicio(LocalDate.now());
        proceso.setResponsable(request.getResponsable());
        proceso.setObservaciones(request.getObservaciones());
        proceso.setCreadoPor(securityUtils.getCurrentUserId());

        proceso = procesoRepository.save(proceso);
        proceso = procesoRepository.findByIdWithRelations(proceso.getId()).orElse(proceso);

        return ProcesoContratacionResponse.fromEntity(proceso);
    }

    /**
     * Avanzar el proceso a otra etapa del pipeline
     */
    @Transactional
    public ProcesoContratacionResponse moverEtapa(Long procesoId, MoverEtapaProcesoRequest request) {
        GcProcesoContratacion proceso = procesoRepository.findByIdWithRelations(procesoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Proceso de contratación no encontrado con ID: " + procesoId));

        if (!proceso.isEnCurso()) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se pueden mover procesos en estado EN_CURSO. Estado actual: " + proceso.getEstado());
        }

        GcEtapa nuevaEtapa = etapaRepository.findById(request.getEtapaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Etapa no encontrada con ID: " + request.getEtapaId()));

        // Validar que la etapa pertenece al mismo pipeline
        if (!nuevaEtapa.getPipeline().getId().equals(proceso.getPipeline().getId())) {
            throw new BusinessException("VALIDATION_ERROR",
                "La etapa no pertenece al pipeline del proceso");
        }

        proceso.setEtapa(nuevaEtapa);
        proceso.setModificadoPor(securityUtils.getCurrentUserId());
        proceso = procesoRepository.save(proceso);

        return ProcesoContratacionResponse.fromEntity(proceso);
    }

    /**
     * RN-05: Completar el proceso habilita la creación del contrato
     */
    @Transactional
    public ProcesoContratacionResponse completarProceso(Long procesoId) {
        GcProcesoContratacion proceso = procesoRepository.findByIdWithRelations(procesoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Proceso de contratación no encontrado con ID: " + procesoId));

        if (!proceso.isEnCurso()) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se pueden completar procesos en estado EN_CURSO. Estado actual: " + proceso.getEstado());
        }

        proceso.setEstado(EstadoProceso.COMPLETADO);
        proceso.setFechaCompletado(LocalDate.now());
        proceso.setModificadoPor(securityUtils.getCurrentUserId());
        proceso = procesoRepository.save(proceso);

        return ProcesoContratacionResponse.fromEntity(proceso);
    }

    /**
     * RN-03: Si el proceso se cancela, la oportunidad puede volver a gestionarse
     */
    @Transactional
    public ProcesoContratacionResponse cancelarProceso(Long procesoId) {
        GcProcesoContratacion proceso = procesoRepository.findByIdWithRelations(procesoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Proceso de contratación no encontrado con ID: " + procesoId));

        if (!proceso.isEnCurso()) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se pueden cancelar procesos en estado EN_CURSO. Estado actual: " + proceso.getEstado());
        }

        proceso.setEstado(EstadoProceso.CANCELADO);
        proceso.setModificadoPor(securityUtils.getCurrentUserId());
        proceso = procesoRepository.save(proceso);

        return ProcesoContratacionResponse.fromEntity(proceso);
    }
}
