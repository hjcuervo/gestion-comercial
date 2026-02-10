package com.arquitecsoft.gestion.domain.empresa.service;

import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaCreateRequest;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaResponse;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaUpdateRequest;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa.EstadoEmpresa;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa.TipoEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
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

@Service
public class EmpresaService {

    private final GcEmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public EmpresaService(GcEmpresaRepository empresaRepository, SecurityUtils securityUtils) {
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public PageResponse<EmpresaResponse> listar(String q, String tipo, String estado, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("razonSocial").ascending());

        TipoEmpresa tipoEnum = null;
        if (StringUtils.hasText(tipo)) {
            try {
                tipoEnum = TipoEmpresa.valueOf(tipo);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Tipo de empresa inválido: " + tipo);
            }
        }

        EstadoEmpresa estadoEnum = null;
        if (StringUtils.hasText(estado)) {
            try {
                estadoEnum = EstadoEmpresa.valueOf(estado);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de empresa inválido: " + estado);
            }
        }

        Page<GcEmpresa> pageResult = empresaRepository.findWithFilters(q, tipoEnum, estadoEnum, pageable);

        return PageResponse.from(pageResult, EmpresaResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse obtenerPorId(Long id) {
        GcEmpresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + id));

        return EmpresaResponse.fromEntity(empresa);
    }

    @Transactional
    public EmpresaResponse crear(EmpresaCreateRequest request) {
        // Validar tipo
        TipoEmpresa tipo;
        try {
            tipo = TipoEmpresa.valueOf(request.getTipo());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("VALIDATION_ERROR", "Tipo de empresa inválido: " + request.getTipo());
        }

        // Validar identificación tributaria única
        if (StringUtils.hasText(request.getIdentificacionTributaria())) {
            if (empresaRepository.existsByIdentificacionTributaria(request.getIdentificacionTributaria())) {
                throw new BusinessException("DUPLICATE_ERROR", 
                    "Ya existe una empresa con la identificación tributaria: " + request.getIdentificacionTributaria());
            }
        }

        GcEmpresa empresa = new GcEmpresa();
        empresa.setTipo(tipo);
        empresa.setRazonSocial(request.getRazonSocial().trim());
        empresa.setIdentificacionTributaria(request.getIdentificacionTributaria());
        empresa.setSitioWeb(request.getSitioWeb());
        empresa.setPais(request.getPais());
        empresa.setEstado(EstadoEmpresa.ACTIVA);
        empresa.setCreadoPor(securityUtils.getCurrentUserId());

        empresa = empresaRepository.save(empresa);

        return EmpresaResponse.fromEntity(empresa);
    }

    @Transactional
    public EmpresaResponse actualizar(Long id, EmpresaUpdateRequest request) {
        GcEmpresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + id));

        // Actualizar tipo si viene
        if (StringUtils.hasText(request.getTipo())) {
            try {
                empresa.setTipo(TipoEmpresa.valueOf(request.getTipo()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Tipo de empresa inválido: " + request.getTipo());
            }
        }

        // Actualizar razón social si viene
        if (StringUtils.hasText(request.getRazonSocial())) {
            empresa.setRazonSocial(request.getRazonSocial().trim());
        }

        // Actualizar identificación tributaria si viene
        if (request.getIdentificacionTributaria() != null) {
            // Validar que no exista otra empresa con esa identificación
            if (StringUtils.hasText(request.getIdentificacionTributaria())) {
                empresaRepository.findByIdentificacionTributaria(request.getIdentificacionTributaria())
                        .ifPresent(existente -> {
                            if (!existente.getId().equals(id)) {
                                throw new BusinessException("DUPLICATE_ERROR",
                                    "Ya existe una empresa con la identificación tributaria: " + request.getIdentificacionTributaria());
                            }
                        });
            }
            empresa.setIdentificacionTributaria(request.getIdentificacionTributaria());
        }

        // Actualizar otros campos si vienen
        if (request.getSitioWeb() != null) {
            empresa.setSitioWeb(request.getSitioWeb());
        }

        if (request.getPais() != null) {
            empresa.setPais(request.getPais());
        }

        // Actualizar estado si viene
        if (StringUtils.hasText(request.getEstado())) {
            EstadoEmpresa nuevoEstado;
            try {
                nuevoEstado = EstadoEmpresa.valueOf(request.getEstado());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de empresa inválido: " + request.getEstado());
            }

            // Validar que no se inactive si tiene oportunidades activas
            if (nuevoEstado == EstadoEmpresa.INACTIVA && empresa.getEstado() == EstadoEmpresa.ACTIVA) {
                // Esta validación se puede habilitar cuando exista la entidad Oportunidad
                // long oportunidadesActivas = empresaRepository.countOportunidadesActivas(id);
                // if (oportunidadesActivas > 0) {
                //     throw new BusinessException("BUSINESS_ERROR",
                //         "No se puede inactivar la empresa porque tiene " + oportunidadesActivas + " oportunidades activas");
                // }
            }

            empresa.setEstado(nuevoEstado);
        }

        empresa.setModificadoPor(securityUtils.getCurrentUserId());
        empresa = empresaRepository.save(empresa);

        return EmpresaResponse.fromEntity(empresa);
    }
}
