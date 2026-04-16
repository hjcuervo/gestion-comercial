package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago;
import com.arquitecsoft.gestion.domain.contrato.repository.GcContratoFormaPagoRepository;
import com.arquitecsoft.gestion.domain.facturacion.dto.GestionCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.GestionResponse;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcFormaPagoGestion;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcFormaPagoGestion.TipoGestion;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcFormaPagoGestionRepository;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormaPagoGestionService {

    private final GcFormaPagoGestionRepository gestionRepository;
    private final GcContratoFormaPagoRepository formaPagoRepository;
    private final SecurityUtils securityUtils;

    public FormaPagoGestionService(GcFormaPagoGestionRepository gestionRepository,
                                    GcContratoFormaPagoRepository formaPagoRepository,
                                    SecurityUtils securityUtils) {
        this.gestionRepository = gestionRepository;
        this.formaPagoRepository = formaPagoRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<GestionResponse> listarPorFormaPago(Long formaPagoId) {
        if (!formaPagoRepository.existsById(formaPagoId)) {
            throw new BusinessException("NOT_FOUND", "Forma de pago no encontrada con ID: " + formaPagoId);
        }
        return gestionRepository.findByFormaPagoIdOrderByFechaCreacionDesc(formaPagoId).stream()
                .map(GestionResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public GestionResponse registrar(Long formaPagoId, GestionCreateRequest request) {
        GcContratoFormaPago fp = formaPagoRepository.findById(formaPagoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Forma de pago no encontrada"));

        TipoGestion tipo;
        try { tipo = TipoGestion.valueOf(request.getTipoGestion()); }
        catch (IllegalArgumentException e) {
            throw new BusinessException("VALIDATION_ERROR", "Tipo de gestión inválido: " + request.getTipoGestion());
        }

        GcFormaPagoGestion gestion = new GcFormaPagoGestion();
        gestion.setFormaPago(fp);
        gestion.setTipoGestion(tipo);
        gestion.setDescripcion(request.getDescripcion().trim());
        gestion.setFechaGestion(request.getFechaGestion());
        gestion.setCreadoPor(securityUtils.getCurrentUserId());

        gestion = gestionRepository.save(gestion);
        return GestionResponse.fromEntity(gestion);
    }
}
