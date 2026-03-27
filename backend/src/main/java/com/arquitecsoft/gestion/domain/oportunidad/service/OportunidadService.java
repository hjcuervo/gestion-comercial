package com.arquitecsoft.gestion.domain.oportunidad.service;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.oportunidad.dto.*;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad.EstadoMacro;
import com.arquitecsoft.gestion.domain.oportunidad.repository.GcOportunidadRepository;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline;
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

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OportunidadService {

    private final GcOportunidadRepository oportunidadRepository;
    private final GcEmpresaRepository empresaRepository;
    private final GcPipelineRepository pipelineRepository;
    private final GcEtapaRepository etapaRepository;
    private final SecurityUtils securityUtils;

    public OportunidadService(GcOportunidadRepository oportunidadRepository,
                              GcEmpresaRepository empresaRepository,
                              GcPipelineRepository pipelineRepository,
                              GcEtapaRepository etapaRepository,
                              SecurityUtils securityUtils) {
        this.oportunidadRepository = oportunidadRepository;
        this.empresaRepository = empresaRepository;
        this.pipelineRepository = pipelineRepository;
        this.etapaRepository = etapaRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public PageResponse<OportunidadResponse> listar(String q, Long empresaId, Long pipelineId,
                                                     Long etapaId, String estadoMacro,
                                                     int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("fechaCreacion").descending());
        EstadoMacro estadoEnum = null;
        if (StringUtils.hasText(estadoMacro)) {
            try { estadoEnum = EstadoMacro.valueOf(estadoMacro); }
            catch (IllegalArgumentException e) { throw new BusinessException("VALIDATION_ERROR", "Estado macro invalido: " + estadoMacro); }
        }
        Page<GcOportunidad> pageResult = oportunidadRepository.findWithFilters(q, empresaId, pipelineId, etapaId, estadoEnum, pageable);
        return PageResponse.from(pageResult, OportunidadResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public OportunidadResponse obtenerPorId(Long id) {
        GcOportunidad oportunidad = oportunidadRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Oportunidad no encontrada con ID: " + id));
        return OportunidadResponse.fromEntity(oportunidad);
    }

    @Transactional
    public OportunidadResponse crear(OportunidadCreateRequest request) {
        GcEmpresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + request.getEmpresaId()));
        GcPipeline pipeline = pipelineRepository.findById(request.getPipelineId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Pipeline no encontrado con ID: " + request.getPipelineId()));

        GcEtapa etapa;
        if (request.getEtapaId() != null) {
            etapa = etapaRepository.findById(request.getEtapaId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "Etapa no encontrada con ID: " + request.getEtapaId()));
            if (!etapa.getPipeline().getId().equals(pipeline.getId())) {
                throw new BusinessException("VALIDATION_ERROR", "La etapa no pertenece al pipeline especificado");
            }
        } else {
            List<GcEtapa> etapas = etapaRepository.findActivasByPipelineId(pipeline.getId());
            if (etapas.isEmpty()) throw new BusinessException("VALIDATION_ERROR", "El pipeline no tiene etapas activas");
            etapa = etapas.get(0);
        }

        GcOportunidad oportunidad = new GcOportunidad();
        oportunidad.setNombre(request.getNombre().trim());
        oportunidad.setEmpresa(empresa);
        oportunidad.setPipeline(pipeline);
        oportunidad.setEtapa(etapa);
        oportunidad.setEstadoMacro(EstadoMacro.ABIERTA);
        oportunidad.setValorEstimado(request.getValorEstimado());
        oportunidad.setMoneda(request.getMoneda() != null ? request.getMoneda() : "COP");
        oportunidad.setProbabilidad(request.getProbabilidad() != null ? request.getProbabilidad() : etapa.getProbabilidadSugerida());
        oportunidad.setFechaEstimadaCierre(request.getFechaEstimadaCierre());
        oportunidad.setFuente(request.getFuente());
        oportunidad.setTipoServicio(request.getTipoServicio());
        oportunidad.setCreadoPor(securityUtils.getCurrentUserId());

        oportunidad = oportunidadRepository.save(oportunidad);
        oportunidad = oportunidadRepository.findByIdWithRelations(oportunidad.getId()).orElse(oportunidad);
        return OportunidadResponse.fromEntity(oportunidad);
    }

    @Transactional
    public OportunidadResponse actualizar(Long id, OportunidadUpdateRequest request) {
        GcOportunidad oportunidad = oportunidadRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Oportunidad no encontrada con ID: " + id));

        // GANADA se puede editar (está en pipeline contractual), PERDIDA/NO_CONCRETADA/CONTRATADA no
        if (oportunidad.isCerrada() || oportunidad.isContratada()) {
            throw new BusinessException("BUSINESS_ERROR", "No se puede editar una oportunidad en estado " + oportunidad.getEstadoMacro());
        }

        if (StringUtils.hasText(request.getNombre())) oportunidad.setNombre(request.getNombre().trim());
        if (request.getValorEstimado() != null) oportunidad.setValorEstimado(request.getValorEstimado());
        if (StringUtils.hasText(request.getMoneda())) oportunidad.setMoneda(request.getMoneda());
        if (request.getProbabilidad() != null) oportunidad.setProbabilidad(request.getProbabilidad());
        if (request.getFechaEstimadaCierre() != null) oportunidad.setFechaEstimadaCierre(request.getFechaEstimadaCierre());
        if (request.getFuente() != null) oportunidad.setFuente(request.getFuente());
        if (request.getTipoServicio() != null) oportunidad.setTipoServicio(request.getTipoServicio());

        oportunidad.setModificadoPor(securityUtils.getCurrentUserId());
        oportunidad = oportunidadRepository.save(oportunidad);
        return OportunidadResponse.fromEntity(oportunidad);
    }

    @Transactional
    public OportunidadResponse moverEtapa(Long id, MoverEtapaRequest request) {
        GcOportunidad oportunidad = oportunidadRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Oportunidad no encontrada con ID: " + id));

        // Permitir mover ABIERTA, SEGUIMIENTO y GANADA (pipeline contractual)
        if (oportunidad.isCerrada() || oportunidad.isContratada()) {
            throw new BusinessException("BUSINESS_ERROR", "No se puede mover una oportunidad en estado " + oportunidad.getEstadoMacro());
        }

        GcEtapa nuevaEtapa = etapaRepository.findById(request.getEtapaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Etapa no encontrada con ID: " + request.getEtapaId()));

        if (!nuevaEtapa.getPipeline().getId().equals(oportunidad.getPipeline().getId())) {
            throw new BusinessException("VALIDATION_ERROR", "La etapa no pertenece al pipeline de la oportunidad");
        }

        oportunidad.setEtapa(nuevaEtapa);
        if (nuevaEtapa.getProbabilidadSugerida() != null && !oportunidad.isEnProcesoContractual()) {
            oportunidad.setProbabilidad(nuevaEtapa.getProbabilidadSugerida());
        }
        if (oportunidad.getEstadoMacro() == EstadoMacro.ABIERTA) {
            oportunidad.setEstadoMacro(EstadoMacro.SEGUIMIENTO);
        }

        oportunidad.setModificadoPor(securityUtils.getCurrentUserId());
        oportunidad = oportunidadRepository.save(oportunidad);
        return OportunidadResponse.fromEntity(oportunidad);
    }

    @Transactional
    public OportunidadResponse cerrar(Long id, CerrarOportunidadRequest request) {
        GcOportunidad oportunidad = oportunidadRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Oportunidad no encontrada con ID: " + id));

        if (oportunidad.isCerrada() || oportunidad.isContratada() || oportunidad.isEnProcesoContractual()) {
            throw new BusinessException("BUSINESS_ERROR", "La oportunidad no se puede cerrar en estado " + oportunidad.getEstadoMacro());
        }

        EstadoMacro nuevoEstado;
        try { nuevoEstado = EstadoMacro.valueOf(request.getEstadoMacro()); }
        catch (IllegalArgumentException e) { throw new BusinessException("VALIDATION_ERROR", "Estado de cierre invalido: " + request.getEstadoMacro()); }

        if (nuevoEstado != EstadoMacro.GANADA && nuevoEstado != EstadoMacro.PERDIDA && nuevoEstado != EstadoMacro.NO_CONCRETADA) {
            throw new BusinessException("VALIDATION_ERROR", "Estado de cierre debe ser GANADA, PERDIDA o NO_CONCRETADA");
        }

        oportunidad.setEstadoMacro(nuevoEstado);
        oportunidad.setMotivoCierreId(request.getMotivoCierreId());
        oportunidad.setComentarioCierre(request.getComentario());
        oportunidad.setFechaCierre(LocalDateTime.now());
        oportunidad.setModificadoPor(securityUtils.getCurrentUserId());

        if (nuevoEstado == EstadoMacro.GANADA) {
            oportunidad.setProbabilidad(100);

            // Mover al pipeline de contratación
            if (request.getPipelineContratacionId() != null) {
                GcPipeline pipelineContratacion = pipelineRepository.findById(request.getPipelineContratacionId())
                        .orElseThrow(() -> new BusinessException("NOT_FOUND",
                            "Pipeline de contratación no encontrado con ID: " + request.getPipelineContratacionId()));

                if (!"CONTRATACION".equals(pipelineContratacion.getAmbito())) {
                    throw new BusinessException("VALIDATION_ERROR", "El pipeline seleccionado no es de ámbito CONTRATACION");
                }

                List<GcEtapa> etapas = etapaRepository.findActivasByPipelineId(pipelineContratacion.getId());
                if (etapas.isEmpty()) {
                    throw new BusinessException("VALIDATION_ERROR", "El pipeline de contratación no tiene etapas activas");
                }

                oportunidad.setPipeline(pipelineContratacion);
                oportunidad.setEtapa(etapas.get(0));
            }
        } else {
            oportunidad.setProbabilidad(0);
        }

        oportunidad = oportunidadRepository.save(oportunidad);
        return OportunidadResponse.fromEntity(oportunidad);
    }
}
