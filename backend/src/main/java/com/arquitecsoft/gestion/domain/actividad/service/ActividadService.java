package com.arquitecsoft.gestion.domain.actividad.service;

import com.arquitecsoft.gestion.domain.actividad.dto.*;
import com.arquitecsoft.gestion.domain.actividad.entity.GcActividad;
import com.arquitecsoft.gestion.domain.actividad.entity.GcCompromiso;
import com.arquitecsoft.gestion.domain.actividad.entity.GcCompromiso.EstadoCompromiso;
import com.arquitecsoft.gestion.domain.actividad.repository.GcActividadRepository;
import com.arquitecsoft.gestion.domain.actividad.repository.GcCompromisoRepository;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.oportunidad.repository.GcOportunidadRepository;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActividadService {

    private final GcActividadRepository actividadRepository;
    private final GcCompromisoRepository compromisoRepository;
    private final GcOportunidadRepository oportunidadRepository;
    private final SecurityUtils securityUtils;

    public ActividadService(GcActividadRepository actividadRepository,
                            GcCompromisoRepository compromisoRepository,
                            GcOportunidadRepository oportunidadRepository,
                            SecurityUtils securityUtils) {
        this.actividadRepository = actividadRepository;
        this.compromisoRepository = compromisoRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.securityUtils = securityUtils;
    }

    // ==================== ACTIVIDADES ====================

    @Transactional(readOnly = true)
    public PageResponse<ActividadResponse> listarActividades(Long oportunidadId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Page<GcActividad> pageResult = actividadRepository.findWithFilters(oportunidadId, pageable);
        return PageResponse.from(pageResult, ActividadResponse::fromEntitySimple);
    }

    @Transactional(readOnly = true)
    public List<ActividadResponse> listarActividadesPorOportunidad(Long oportunidadId) {
        return actividadRepository.findByOportunidadId(oportunidadId).stream()
                .map(ActividadResponse::fromEntitySimple)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActividadResponse obtenerActividadPorId(Long id) {
        GcActividad actividad = actividadRepository.findByIdWithOportunidad(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Actividad no encontrada con ID: " + id));
        return ActividadResponse.fromEntity(actividad);
    }

    @Transactional
    public ActividadResponse crearActividad(ActividadCreateRequest request) {
        // Validar oportunidad
        GcOportunidad oportunidad = oportunidadRepository.findById(request.getOportunidadId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Oportunidad no encontrada con ID: " + request.getOportunidadId()));

        // No permitir actividades en oportunidades cerradas
        if (oportunidad.isCerrada()) {
            throw new BusinessException("BUSINESS_ERROR", "No se pueden registrar actividades en una oportunidad cerrada");
        }

        GcActividad actividad = new GcActividad();
        actividad.setOportunidad(oportunidad);
        actividad.setTipoActividadId(request.getTipoActividadId());
        actividad.setFechaHora(request.getFechaHora());
        actividad.setDuracionMinutos(request.getDuracionMinutos());
        actividad.setNotas(request.getNotas());
        actividad.setCreadoPor(securityUtils.getCurrentUserId());

        actividad = actividadRepository.save(actividad);

        // Recargar con relaciones
        actividad = actividadRepository.findByIdWithOportunidad(actividad.getId()).orElse(actividad);

        return ActividadResponse.fromEntity(actividad);
    }

    // ==================== COMPROMISOS ====================

    @Transactional(readOnly = true)
    public List<CompromisoResponse> listarCompromisosPorActividad(Long actividadId) {
        return compromisoRepository.findByActividadId(actividadId).stream()
                .map(CompromisoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompromisoResponse> listarCompromisosPendientesPorOportunidad(Long oportunidadId) {
        return compromisoRepository.findPendientesByOportunidadId(oportunidadId).stream()
                .map(CompromisoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CompromisoResponse crearCompromiso(Long actividadId, CompromisoCreateRequest request) {
        GcActividad actividad = actividadRepository.findByIdWithOportunidad(actividadId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Actividad no encontrada con ID: " + actividadId));

        GcCompromiso compromiso = new GcCompromiso();
        compromiso.setActividad(actividad);
        compromiso.setDescripcion(request.getDescripcion().trim());
        compromiso.setFechaCompromiso(request.getFechaCompromiso());
        compromiso.setEstado(EstadoCompromiso.PENDIENTE);
        compromiso.setCreadoPor(securityUtils.getCurrentUserId());

        compromiso = compromisoRepository.save(compromiso);

        return CompromisoResponse.fromEntity(compromiso);
    }

    @Transactional
    public CompromisoResponse actualizarCompromiso(Long compromisoId, CompromisoUpdateRequest request) {
        GcCompromiso compromiso = compromisoRepository.findByIdWithActividad(compromisoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Compromiso no encontrado con ID: " + compromisoId));

        if (StringUtils.hasText(request.getDescripcion())) {
            compromiso.setDescripcion(request.getDescripcion().trim());
        }

        if (request.getFechaCompromiso() != null) {
            compromiso.setFechaCompromiso(request.getFechaCompromiso());
        }

        if (StringUtils.hasText(request.getEstado())) {
            EstadoCompromiso nuevoEstado;
            try {
                nuevoEstado = EstadoCompromiso.valueOf(request.getEstado());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de compromiso invalido: " + request.getEstado());
            }

            // Si se completa o cancela, registrar fecha
            if ((nuevoEstado == EstadoCompromiso.COMPLETADO || nuevoEstado == EstadoCompromiso.CANCELADO)
                && compromiso.getEstado() != EstadoCompromiso.COMPLETADO
                && compromiso.getEstado() != EstadoCompromiso.CANCELADO) {
                compromiso.setFechaCompletado(LocalDateTime.now());
            }

            compromiso.setEstado(nuevoEstado);
        }

        if (request.getNotasCierre() != null) {
            compromiso.setNotasCierre(request.getNotasCierre());
        }

        compromiso = compromisoRepository.save(compromiso);

        return CompromisoResponse.fromEntity(compromiso);
    }
}
