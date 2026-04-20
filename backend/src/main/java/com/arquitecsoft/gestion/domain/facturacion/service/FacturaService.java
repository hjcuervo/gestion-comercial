package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaAnularRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaResponse;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcFactura;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcCompromisoFacturaRepository;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcFacturaRepository;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio de facturas.
 *
 * Responsabilidades: CRUD puro de la entidad GcFactura (crear, listar,
 * obtener, anular). Las operaciones de aplicación/reversión contra
 * compromisos viven en CompromisoIngresoService.registrarFactura /
 * revertirFactura, NO aquí.
 *
 * Nota sobre anulación: al anular una factura, el servicio detecta si hay
 * aplicaciones vigentes contra compromisos y bloquea la anulación con un
 * mensaje claro. El usuario debe revertir cada aplicación primero (lo que
 * dispara eventos FACTURA_REVERTIDA en cada compromiso), y solo entonces
 * anular la factura. Esto preserva la integridad del modelo de eventos.
 */
@Service
public class FacturaService {

    private final GcFacturaRepository facturaRepository;
    private final GcCompromisoFacturaRepository aplicacionRepository;
    private final GcEmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public FacturaService(GcFacturaRepository facturaRepository,
                          GcCompromisoFacturaRepository aplicacionRepository,
                          GcEmpresaRepository empresaRepository,
                          SecurityUtils securityUtils) {
        this.facturaRepository = facturaRepository;
        this.aplicacionRepository = aplicacionRepository;
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public FacturaResponse obtenerPorId(Long id) {
        GcFactura factura = facturaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Factura no encontrada con ID: " + id));
        return FacturaResponse.fromEntity(factura);
    }

    @Transactional(readOnly = true)
    public PageResponse<FacturaResponse> listar(Long empresaId, String moneda, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("fechaEmision").descending());
        Page<GcFactura> pageResult = facturaRepository.findWithFilters(empresaId, moneda, pageable);
        return PageResponse.from(pageResult, FacturaResponse::fromEntity);
    }

    @Transactional
    public FacturaResponse crear(FacturaCreateRequest request) {
        GcEmpresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Empresa no encontrada con ID: " + request.getEmpresaId()));

        // Validar duplicado (numero + prefijo)
        if (facturaRepository.findByNumeroFacturaAndPrefijo(
                request.getNumeroFactura(), request.getPrefijo()).isPresent()) {
            throw new BusinessException("BUSINESS_ERROR",
                "Ya existe una factura con el mismo número y prefijo.");
        }

        GcFactura factura = new GcFactura();
        factura.setEmpresa(empresa);

        if (request.getEmpresaFacturacionId() != null) {
            GcEmpresa empresaFact = empresaRepository.findById(request.getEmpresaFacturacionId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND",
                        "Empresa de facturación no encontrada con ID: " + request.getEmpresaFacturacionId()));
            factura.setEmpresaFacturacion(empresaFact);
        }

        factura.setNumeroFactura(request.getNumeroFactura().trim());
        factura.setPrefijo(request.getPrefijo());
        factura.setFechaEmision(request.getFechaEmision());
        factura.setFechaVencimiento(request.getFechaVencimiento());
        factura.setValorBase(request.getValorBase());
        factura.setValorIva(request.getValorIva());
        factura.setValorTotal(request.getValorTotal());
        factura.setMoneda(StringUtils.hasText(request.getMoneda()) ? request.getMoneda() : "COP");
        factura.setFactroId(request.getFactroId());
        factura.setObservaciones(request.getObservaciones());
        factura.setAnulada(false);
        factura.setCreadoPor(securityUtils.getCurrentUserId());

        factura = facturaRepository.save(factura);
        return FacturaResponse.fromEntity(factura);
    }

    /**
     * Anula una factura.
     *
     * Requisito: no debe tener aplicaciones vigentes contra compromisos. Si
     * las tiene, el usuario debe revertir cada una primero vía
     * CompromisoIngresoService.revertirFactura (lo que emite eventos
     * FACTURA_REVERTIDA en cada compromiso afectado).
     */
    @Transactional
    public FacturaResponse anular(Long id, FacturaAnularRequest request) {
        GcFactura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Factura no encontrada con ID: " + id));

        if (Boolean.TRUE.equals(factura.getAnulada())) {
            throw new BusinessException("BUSINESS_ERROR", "La factura ya está anulada.");
        }

        BigDecimal aplicadoVigente = aplicacionRepository.sumAplicadoVigenteByFacturaId(id);
        if (aplicadoVigente != null && aplicadoVigente.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("BUSINESS_ERROR",
                "La factura tiene aplicaciones vigentes contra compromisos " +
                "(" + aplicadoVigente + "). Revertir cada aplicación antes de anular.");
        }

        factura.setAnulada(true);
        factura.setFechaAnulacion(LocalDateTime.now());
        factura.setMotivoAnulacion(request.getMotivo());
        factura = facturaRepository.save(factura);

        return FacturaResponse.fromEntity(factura);
    }
}
