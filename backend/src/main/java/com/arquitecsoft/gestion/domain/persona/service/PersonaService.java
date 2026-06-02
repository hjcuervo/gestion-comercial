package com.arquitecsoft.gestion.domain.persona.service;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.catalogo.entity.GcOrigen;
import com.arquitecsoft.gestion.domain.catalogo.repository.GcOrigenRepository;
import com.arquitecsoft.gestion.domain.persona.dto.*;
import com.arquitecsoft.gestion.domain.persona.entity.GcPersona;
import com.arquitecsoft.gestion.domain.persona.entity.GcPersonaEmpresa;
import com.arquitecsoft.gestion.domain.persona.entity.GcPersonaEmpresa.RolContacto;
import com.arquitecsoft.gestion.domain.persona.repository.GcPersonaEmpresaRepository;
import com.arquitecsoft.gestion.domain.persona.repository.GcPersonaRepository;
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
public class PersonaService {

    private final GcPersonaRepository personaRepository;
    private final GcPersonaEmpresaRepository personaEmpresaRepository;
    private final GcEmpresaRepository empresaRepository;
    private final GcOrigenRepository origenRepository;
    private final SecurityUtils securityUtils;

    public PersonaService(GcPersonaRepository personaRepository,
                          GcPersonaEmpresaRepository personaEmpresaRepository,
                          GcEmpresaRepository empresaRepository,
                          GcOrigenRepository origenRepository,
                          SecurityUtils securityUtils) {
        this.personaRepository = personaRepository;
        this.personaEmpresaRepository = personaEmpresaRepository;
        this.empresaRepository = empresaRepository;
        this.origenRepository = origenRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public PageResponse<PersonaResponse> listar(String q, Long empresaId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("apellidos", "nombres").ascending());

        Page<GcPersona> pageResult;
        if (empresaId != null) {
            pageResult = personaRepository.findByEmpresaId(empresaId, pageable);
        } else {
            pageResult = personaRepository.findWithFilters(q, pageable);
        }

        return PageResponse.from(pageResult, PersonaResponse::fromEntitySimple);
    }

    @Transactional(readOnly = true)
    public PersonaResponse obtenerPorId(Long id) {
        GcPersona persona = personaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Persona no encontrada con ID: " + id));

        return PersonaResponse.fromEntity(persona);
    }

    @Transactional
    public PersonaResponse crear(PersonaCreateRequest request) {
        GcPersona persona = new GcPersona();
        persona.setNombres(request.getNombres().trim());
        persona.setApellidos(request.getApellidos().trim());
        aplicarEnriquecimiento(persona, request.getTipoDocumento(), request.getNumeroDocumento(),
                request.getPropietarioId(), request.getOrigenCodigo(), request.getReportaAId(),
                request.getIdioma(), request.getNotas());
        persona.setActivo(1);
        persona.setCreadoPor(securityUtils.getCurrentUserId());

        persona = personaRepository.save(persona);

        return PersonaResponse.fromEntity(persona);
    }

    @Transactional
    public PersonaResponse actualizar(Long id, PersonaUpdateRequest request) {
        GcPersona persona = personaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Persona no encontrada con ID: " + id));

        if (StringUtils.hasText(request.getNombres())) {
            persona.setNombres(request.getNombres().trim());
        }

        if (StringUtils.hasText(request.getApellidos())) {
            persona.setApellidos(request.getApellidos().trim());
        }

        if (request.getActivo() != null) {
            persona.setActivo(request.getActivo() ? 1 : 0);
        }

        // Enriquecimiento (solo si el campo viene en el request)
        if (request.getTipoDocumento() != null) persona.setTipoDocumento(trimToNull(request.getTipoDocumento()));
        if (request.getNumeroDocumento() != null) persona.setNumeroDocumento(trimToNull(request.getNumeroDocumento()));
        if (request.getPropietarioId() != null) persona.setPropietarioId(request.getPropietarioId());
        if (request.getOrigenCodigo() != null) {
            persona.setOrigen(request.getOrigenCodigo().isBlank() ? null : resolverOrigen(request.getOrigenCodigo()));
        }
        if (request.getReportaAId() != null) {
            persona.setReportaA(resolverReportaA(request.getReportaAId(), persona.getId()));
        }
        if (request.getIdioma() != null) persona.setIdioma(trimToNull(request.getIdioma()));
        if (request.getNotas() != null) persona.setNotas(trimToNull(request.getNotas()));

        persona.setModificadoPor(securityUtils.getCurrentUserId());
        persona = personaRepository.save(persona);

        return PersonaResponse.fromEntity(persona);
    }

    private void aplicarEnriquecimiento(GcPersona persona, String tipoDoc, String numDoc, Long propietarioId,
                                        String origenCodigo, Long reportaAId, String idioma, String notas) {
        persona.setTipoDocumento(trimToNull(tipoDoc));
        persona.setNumeroDocumento(trimToNull(numDoc));
        persona.setPropietarioId(propietarioId);
        persona.setOrigen(resolverOrigen(origenCodigo));
        persona.setReportaA(resolverReportaA(reportaAId, persona.getId()));
        persona.setIdioma(trimToNull(idioma));
        persona.setNotas(trimToNull(notas));
    }

    private GcOrigen resolverOrigen(String codigo) {
        if (!StringUtils.hasText(codigo)) return null;
        return origenRepository.findById(codigo)
                .orElseThrow(() -> new BusinessException("VALIDATION_ERROR", "Origen no encontrado: " + codigo));
    }

    private GcPersona resolverReportaA(Long reportaAId, Long personaActualId) {
        if (reportaAId == null) return null;
        if (reportaAId.equals(personaActualId)) {
            throw new BusinessException("VALIDATION_ERROR", "Una persona no puede reportarse a sí misma");
        }
        GcPersona jefe = personaRepository.findById(reportaAId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Persona (reporta a) no encontrada: " + reportaAId));

        // Detección de ciclos: subir por la cadena de jefes; si reaparece la persona actual, hay ciclo.
        if (personaActualId != null) {
            java.util.Set<Long> visitados = new java.util.HashSet<>();
            GcPersona cursor = jefe;
            while (cursor != null && cursor.getReportaA() != null) {
                Long jefeId = cursor.getReportaA().getId();
                if (personaActualId.equals(jefeId)) {
                    throw new BusinessException("VALIDATION_ERROR", "La relación de reporte crearía un ciclo");
                }
                if (!visitados.add(jefeId)) break; // corta ante un ciclo preexistente
                cursor = personaRepository.findById(jefeId).orElse(null);
            }
        }
        return jefe;
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    @Transactional
    public PersonaEmpresaResponse asociarEmpresa(Long personaId, PersonaEmpresaCreateRequest request) {
        GcPersona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Persona no encontrada con ID: " + personaId));

        GcEmpresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + request.getEmpresaId()));

        // Si ya existe un vínculo ACTIVO, es duplicado. Si existe pero cerrado (activo=0),
        // se reactiva en vez de crear uno nuevo (coherente con el soft-close de desasociar).
        GcPersonaEmpresa personaEmpresa = personaEmpresaRepository
                .findByPersonaIdAndEmpresaId(personaId, request.getEmpresaId())
                .orElse(null);
        if (personaEmpresa != null && personaEmpresa.isActivo()) {
            throw new BusinessException("DUPLICATE_ERROR",
                "La persona ya está asociada a esta empresa");
        }
        if (personaEmpresa == null) {
            personaEmpresa = new GcPersonaEmpresa(persona, empresa);
        }
        personaEmpresa.setCargo(request.getCargo());
        personaEmpresa.setPuesto(request.getPuesto());
        personaEmpresa.setEmailEmpresarial(request.getEmailEmpresarial());
        personaEmpresa.setTelefonoEmpresarial(request.getTelefonoEmpresarial());
        personaEmpresa.setEsContactoPrincipal(Boolean.TRUE.equals(request.getEsContactoPrincipal()) ? 1 : 0);
        personaEmpresa.setEsEmpresaPrincipal(Boolean.TRUE.equals(request.getEsEmpresaPrincipal()) ? 1 : 0);
        personaEmpresa.setFechaInicio(request.getFechaInicio());
        personaEmpresa.setFechaFin(request.getFechaFin());
        personaEmpresa.setActivo(1);

        if (StringUtils.hasText(request.getRolContacto())) {
            try {
                personaEmpresa.setRolContacto(RolContacto.valueOf(request.getRolContacto()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Rol de contacto inválido: " + request.getRolContacto());
            }
        }

        // RB-32: a lo sumo un contacto principal por empresa (entre vínculos activos)
        if (personaEmpresa.isContactoPrincipal()) {
            for (GcPersonaEmpresa otro : personaEmpresaRepository.findByEmpresaId(request.getEmpresaId())) {
                if (otro.isActivo() && otro.isContactoPrincipal()) {
                    otro.setEsContactoPrincipal(0);
                    personaEmpresaRepository.save(otro);
                }
            }
        }
        // RB-33: a lo sumo una empresa principal por persona
        if (personaEmpresa.isEmpresaPrincipal()) {
            for (GcPersonaEmpresa otro : personaEmpresaRepository.findByPersonaId(personaId)) {
                if (otro.isEmpresaPrincipal()) {
                    otro.setEsEmpresaPrincipal(0);
                    personaEmpresaRepository.save(otro);
                }
            }
        }

        personaEmpresa = personaEmpresaRepository.save(personaEmpresa);

        return PersonaEmpresaResponse.fromEntity(personaEmpresa);
    }

    @Transactional
    public void desasociarEmpresa(Long personaId, Long empresaId) {
        // Validar que la persona existe
        if (!personaRepository.existsById(personaId)) {
            throw new BusinessException("NOT_FOUND", "Persona no encontrada con ID: " + personaId);
        }

        GcPersonaEmpresa personaEmpresa = personaEmpresaRepository
                .findByPersonaIdAndEmpresaId(personaId, empresaId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "No existe asociación entre la persona " + personaId + " y la empresa " + empresaId));

        // Soft-close: se preserva el histórico del vínculo (F-RP6).
        personaEmpresa.setActivo(0);
        personaEmpresa.setFechaFin(java.time.LocalDate.now());
        personaEmpresaRepository.save(personaEmpresa);
    }
}
