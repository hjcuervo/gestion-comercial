package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.facturacion.dto.CompromisoGestionCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.CompromisoGestionResponse;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoGestion;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoGestion.TipoGestion;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoIngreso;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcCompromisoGestionRepository;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcCompromisoIngresoRepository;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Notas libres del usuario sobre compromisos de ingreso.
 *
 * NO dispara eventos de máquina de estados — es bitácora comercial (llamadas,
 * correos, validaciones, soportes). Para operaciones que cambian el estado
 * del compromiso, ver CompromisoIngresoService.
 */
@Service
public class CompromisoGestionService {

    private final GcCompromisoGestionRepository gestionRepository;
    private final GcCompromisoIngresoRepository compromisoRepository;
    private final SecurityUtils securityUtils;

    public CompromisoGestionService(GcCompromisoGestionRepository gestionRepository,
                                     GcCompromisoIngresoRepository compromisoRepository,
                                     SecurityUtils securityUtils) {
        this.gestionRepository = gestionRepository;
        this.compromisoRepository = compromisoRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<CompromisoGestionResponse> listarPorCompromiso(Long compromisoId) {
        if (!compromisoRepository.existsById(compromisoId)) {
            throw new BusinessException("NOT_FOUND",
                "Compromiso no encontrado con ID: " + compromisoId);
        }
        return gestionRepository.findByCompromisoIdOrderByFechaCreacionDesc(compromisoId).stream()
                .map(CompromisoGestionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CompromisoGestionResponse registrar(Long compromisoId, CompromisoGestionCreateRequest request) {
        GcCompromisoIngreso compromiso = compromisoRepository.findById(compromisoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Compromiso no encontrado con ID: " + compromisoId));

        if (!StringUtils.hasText(request.getDescripcion())) {
            throw new BusinessException("VALIDATION_ERROR", "La descripción es obligatoria.");
        }
        if (request.getFechaGestion() == null) {
            throw new BusinessException("VALIDATION_ERROR", "La fecha de gestión es obligatoria.");
        }

        TipoGestion tipo;
        try {
            tipo = TipoGestion.valueOf(request.getTipoGestion());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BusinessException("VALIDATION_ERROR",
                "Tipo de gestión inválido: " + request.getTipoGestion());
        }

        GcCompromisoGestion gestion = new GcCompromisoGestion();
        gestion.setCompromiso(compromiso);
        gestion.setTipoGestion(tipo);
        gestion.setDescripcion(request.getDescripcion().trim());
        gestion.setFechaGestion(request.getFechaGestion());
        gestion.setCreadoPor(securityUtils.getCurrentUserId());

        gestion = gestionRepository.save(gestion);

        // Toca actividad en el compromiso también.
        compromiso.setFechaUltimaActividad(java.time.LocalDateTime.now());
        if (Boolean.TRUE.equals(compromiso.getEnRiesgo())) {
            compromiso.setEnRiesgo(false);
        }
        compromisoRepository.save(compromiso);

        return CompromisoGestionResponse.fromEntity(gestion);
    }
}
