package com.arquitecsoft.gestion.domain.empresa.service;

import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaCreateRequest;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaResponse;
import com.arquitecsoft.gestion.domain.empresa.dto.EmpresaUpdateRequest;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa.EstadoEmpresa;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa.Clasificacion;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa.Tamano;
import com.arquitecsoft.gestion.domain.catalogo.repository.GcOrigenRepository;
import com.arquitecsoft.gestion.domain.catalogo.repository.GcSectorRepository;
import com.arquitecsoft.gestion.domain.empresa.repository.GcDepartamentoRepository;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.empresa.repository.GcMunicipioRepository;
import com.arquitecsoft.gestion.domain.empresa.repository.GcPaisRepository;
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
    private final GcPaisRepository paisRepository;
    private final GcDepartamentoRepository departamentoRepository;
    private final GcMunicipioRepository municipioRepository;
    private final GcSectorRepository sectorRepository;
    private final GcOrigenRepository origenRepository;
    private final SecurityUtils securityUtils;

    public EmpresaService(GcEmpresaRepository empresaRepository,
                          GcPaisRepository paisRepository,
                          GcDepartamentoRepository departamentoRepository,
                          GcMunicipioRepository municipioRepository,
                          GcSectorRepository sectorRepository,
                          GcOrigenRepository origenRepository,
                          SecurityUtils securityUtils) {
        this.empresaRepository = empresaRepository;
        this.paisRepository = paisRepository;
        this.departamentoRepository = departamentoRepository;
        this.municipioRepository = municipioRepository;
        this.sectorRepository = sectorRepository;
        this.origenRepository = origenRepository;
        this.securityUtils = securityUtils;
    }

    private EmpresaResponse enriquecerResponse(GcEmpresa empresa) {
        EmpresaResponse response = EmpresaResponse.fromEntity(empresa);

        if (StringUtils.hasText(empresa.getPais())) {
            paisRepository.findById(empresa.getPais())
                    .ifPresent(p -> response.setPaisNombre(p.getNombre()));
        }
        if (StringUtils.hasText(empresa.getDepartamento())) {
            departamentoRepository.findById(empresa.getDepartamento())
                    .ifPresent(d -> response.setDepartamentoNombre(d.getDescripcion()));
        }
        if (StringUtils.hasText(empresa.getCiudad())) {
            municipioRepository.findById(empresa.getCiudad())
                    .ifPresent(m -> response.setCiudadNombre(m.getDescripcion()));
        }

        return response;
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

        return PageResponse.from(pageResult, this::enriquecerResponse);
    }

    @Transactional(readOnly = true)
    public EmpresaResponse obtenerPorId(Long id) {
        GcEmpresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + id));
        return enriquecerResponse(empresa);
    }

    @Transactional
    public EmpresaResponse crear(EmpresaCreateRequest request) {
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
        empresa.setClasificacion(parseClasificacion(request.getClasificacion(), Clasificacion.PROSPECTO));
        empresa.setTamano(parseTamano(request.getTamano()));
        empresa.setPropietarioId(request.getPropietarioId());
        empresa.setSector(resolverSector(request.getSectorCodigo()));
        empresa.setOrigen(resolverOrigen(request.getOrigenCodigo()));
        empresa.setIngresosAnuales(request.getIngresosAnuales());
        empresa.setDescripcion(request.getDescripcion());
        empresa.setCreadoPor(securityUtils.getCurrentUserId());

        empresa = empresaRepository.save(empresa);
        return enriquecerResponse(empresa);
    }

    @Transactional
    public EmpresaResponse actualizar(Long id, EmpresaUpdateRequest request) {
        GcEmpresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + id));

        if (StringUtils.hasText(request.getRazonSocial())) {
            empresa.setRazonSocial(request.getRazonSocial().trim());
        }
        if (request.getTipoDoc() != null) {
            empresa.setTipoDoc(request.getTipoDoc());
        }
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
        if (request.getDv() != null) {
            empresa.setDv(request.getDv());
        }
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
        if (request.getSitioWeb() != null) {
            empresa.setSitioWeb(request.getSitioWeb());
        }
        if (StringUtils.hasText(request.getEstado())) {
            EstadoEmpresa nuevoEstado;
            try {
                nuevoEstado = EstadoEmpresa.valueOf(request.getEstado());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Estado de empresa inválido: " + request.getEstado());
            }
            empresa.setEstado(nuevoEstado);
        }

        if (request.getClasificacion() != null) {
            empresa.setClasificacion(parseClasificacion(request.getClasificacion(), empresa.getClasificacion()));
        }
        if (request.getTamano() != null) {
            empresa.setTamano(request.getTamano().isBlank() ? null : parseTamano(request.getTamano()));
        }
        if (request.getPropietarioId() != null) {
            empresa.setPropietarioId(request.getPropietarioId());
        }
        if (request.getSectorCodigo() != null) {
            empresa.setSector(request.getSectorCodigo().isBlank() ? null : resolverSector(request.getSectorCodigo()));
        }
        if (request.getOrigenCodigo() != null) {
            empresa.setOrigen(request.getOrigenCodigo().isBlank() ? null : resolverOrigen(request.getOrigenCodigo()));
        }
        if (request.getIngresosAnuales() != null) {
            empresa.setIngresosAnuales(request.getIngresosAnuales());
        }
        if (request.getDescripcion() != null) {
            empresa.setDescripcion(request.getDescripcion());
        }

        empresa.setModificadoPor(securityUtils.getCurrentUserId());
        empresa = empresaRepository.save(empresa);
        return enriquecerResponse(empresa);
    }

    // --- Helpers de enriquecimiento (RB-34) ---

    private Clasificacion parseClasificacion(String valor, Clasificacion porDefecto) {
        if (!StringUtils.hasText(valor)) return porDefecto;
        try {
            return Clasificacion.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("VALIDATION_ERROR", "Clasificación inválida: " + valor);
        }
    }

    private Tamano parseTamano(String valor) {
        if (!StringUtils.hasText(valor)) return null;
        try {
            return Tamano.valueOf(valor);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("VALIDATION_ERROR", "Tamaño inválido: " + valor);
        }
    }

    private com.arquitecsoft.gestion.domain.catalogo.entity.GcSector resolverSector(String codigo) {
        if (!StringUtils.hasText(codigo)) return null;
        return sectorRepository.findById(codigo)
                .orElseThrow(() -> new BusinessException("VALIDATION_ERROR", "Sector no encontrado: " + codigo));
    }

    private com.arquitecsoft.gestion.domain.catalogo.entity.GcOrigen resolverOrigen(String codigo) {
        if (!StringUtils.hasText(codigo)) return null;
        return origenRepository.findById(codigo)
                .orElseThrow(() -> new BusinessException("VALIDATION_ERROR", "Origen no encontrado: " + codigo));
    }
}
