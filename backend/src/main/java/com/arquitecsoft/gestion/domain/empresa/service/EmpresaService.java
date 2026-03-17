package com.arquitecsoft.gestion.domain.empresa.service;

import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaCreateRequest;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaResponse;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaUpdateRequest;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa.EstadoEmpresa;
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
    public PageResponse<EmpresaResponse> listar(String q, String estado, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("razonSocial").ascending());

        EstadoEmpresa estadoEnum = null;
        if (StringUtils.hasText(estado)) {
            try {
                estadoEnum = EstadoEmpresa.valueOf(estado);
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de empresa inválido: " + estado);
            }
        }

        Page<GcEmpresa> pageResult = empresaRepository.findWithFilters(q, estadoEnum, pageable);

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
        // Validar identificación tributaria única
        if (StringUtils.hasText(request.getIdentificacionTributaria())) {
            if (empresaRepository.existsByIdentificacionTributaria(request.getIdentificacionTributaria())) {
                throw new BusinessException("DUPLICATE_ERROR",
                    "Ya existe una empresa con la identificación tributaria: " + request.getIdentificacionTributaria());
            }
        }

        GcEmpresa empresa = new GcEmpresa();
        empresa.setRazonSocial(request.getRazonSocial().trim());
        empresa.setTipoDoc(request.getTipoDoc());
        empresa.setIdentificacionTributaria(request.getIdentificacionTributaria());
        empresa.setDv(request.getDv());
        empresa.setPais(request.getPais());
        empresa.setDepartamento(request.getDepartamento());
        empresa.setCiudad(request.getCiudad());
        empresa.setDireccionFisica(request.getDireccionFisica());
        empresa.setSitioWeb(request.getSitioWeb());
        empresa.setEstado(EstadoEmpresa.ACTIVA);
        empresa.setCreadoPor(securityUtils.getCurrentUserId());

        empresa = empresaRepository.save(empresa);

        return EmpresaResponse.fromEntity(empresa);
    }

    @Transactional
    public EmpresaResponse actualizar(Long id, EmpresaUpdateRequest request) {
        GcEmpresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + id));

        // Actualizar razón social
        if (StringUtils.hasText(request.getRazonSocial())) {
            empresa.setRazonSocial(request.getRazonSocial().trim());
        }

        // Actualizar tipo documento
        if (request.getTipoDoc() != null) {
            empresa.setTipoDoc(request.getTipoDoc());
        }

        // Actualizar identificación tributaria
        if (request.getIdentificacionTributaria() != null) {
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

        // Actualizar DV
        if (request.getDv() != null) {
            empresa.setDv(request.getDv());
        }

        // Actualizar ubicación
        if (request.getPais() != null) {
            empresa.setPais(request.getPais());
        }
        if (request.getDepartamento() != null) {
            empresa.setDepartamento(request.getDepartamento());
        }
        if (request.getCiudad() != null) {
            empresa.setCiudad(request.getCiudad());
        }
        if (request.getDireccionFisica() != null) {
            empresa.setDireccionFisica(request.getDireccionFisica());
        }

        // Actualizar sitio web
        if (request.getSitioWeb() != null) {
            empresa.setSitioWeb(request.getSitioWeb());
        }

        // Actualizar estado
        if (StringUtils.hasText(request.getEstado())) {
            EstadoEmpresa nuevoEstado;
            try {
                nuevoEstado = EstadoEmpresa.valueOf(request.getEstado());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de empresa inválido: " + request.getEstado());
            }
            empresa.setEstado(nuevoEstado);
        }

        empresa.setModificadoPor(securityUtils.getCurrentUserId());
        empresa = empresaRepository.save(empresa);

        return EmpresaResponse.fromEntity(empresa);
    }
}
