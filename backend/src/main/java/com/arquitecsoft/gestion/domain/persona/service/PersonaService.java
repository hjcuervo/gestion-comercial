package com.arquitecsoft.gestion.domain.persona.service;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
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
    private final SecurityUtils securityUtils;

    public PersonaService(GcPersonaRepository personaRepository,
                          GcPersonaEmpresaRepository personaEmpresaRepository,
                          GcEmpresaRepository empresaRepository,
                          SecurityUtils securityUtils) {
        this.personaRepository = personaRepository;
        this.personaEmpresaRepository = personaEmpresaRepository;
        this.empresaRepository = empresaRepository;
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
        GcPersona persona = personaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Persona no encontrada con ID: " + id));

        return PersonaResponse.fromEntity(persona);
    }

    @Transactional
    public PersonaResponse crear(PersonaCreateRequest request) {
        // Validar email único si viene
        if (StringUtils.hasText(request.getEmail())) {
            if (personaRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("DUPLICATE_ERROR", 
                    "Ya existe una persona con el email: " + request.getEmail());
            }
        }

        GcPersona persona = new GcPersona();
        persona.setNombres(request.getNombres().trim());
        persona.setApellidos(request.getApellidos().trim());
        persona.setEmail(request.getEmail());
        persona.setTelefono(request.getTelefono());
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

        if (request.getEmail() != null) {
            // Validar email único
            if (StringUtils.hasText(request.getEmail())) {
                personaRepository.findByEmail(request.getEmail())
                        .ifPresent(existente -> {
                            if (!existente.getId().equals(id)) {
                                throw new BusinessException("DUPLICATE_ERROR",
                                    "Ya existe una persona con el email: " + request.getEmail());
                            }
                        });
            }
            persona.setEmail(request.getEmail());
        }

        if (request.getTelefono() != null) {
            persona.setTelefono(request.getTelefono());
        }

        if (request.getActivo() != null) {
            persona.setActivo(request.getActivo() ? 1 : 0);
        }

        persona.setModificadoPor(securityUtils.getCurrentUserId());
        persona = personaRepository.save(persona);

        return PersonaResponse.fromEntity(persona);
    }

    @Transactional
    public PersonaEmpresaResponse asociarEmpresa(Long personaId, PersonaEmpresaCreateRequest request) {
        GcPersona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Persona no encontrada con ID: " + personaId));

        GcEmpresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada con ID: " + request.getEmpresaId()));

        // Validar que no exista la relación
        if (personaEmpresaRepository.existsByPersonaIdAndEmpresaId(personaId, request.getEmpresaId())) {
            throw new BusinessException("DUPLICATE_ERROR", 
                "La persona ya está asociada a esta empresa");
        }

        GcPersonaEmpresa personaEmpresa = new GcPersonaEmpresa(persona, empresa);
        personaEmpresa.setCargo(request.getCargo());
        personaEmpresa.setPuesto(request.getPuesto());
        personaEmpresa.setEmailEmpresarial(request.getEmailEmpresarial());
        personaEmpresa.setTelefonoEmpresarial(request.getTelefonoEmpresarial());
        personaEmpresa.setEsContactoPrincipal(request.getEsContactoPrincipal() != null && request.getEsContactoPrincipal() ? 1 : 0);

        // Validar y asignar rol de contacto
        if (StringUtils.hasText(request.getRolContacto())) {
            try {
                personaEmpresa.setRolContacto(RolContacto.valueOf(request.getRolContacto()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR", "Rol de contacto inválido: " + request.getRolContacto());
            }
        }

        personaEmpresa = personaEmpresaRepository.save(personaEmpresa);

        return PersonaEmpresaResponse.fromEntity(personaEmpresa);
    }
}
