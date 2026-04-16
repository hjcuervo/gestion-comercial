package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago.EstadoFormaPago;
import com.arquitecsoft.gestion.domain.contrato.repository.GcContratoFormaPagoRepository;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.FacturaResponse;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcFactura;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcFormaPagoGestion;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcFormaPagoGestion.TipoGestion;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcFacturaRepository;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcFormaPagoGestionRepository;
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
import java.time.LocalDate;

@Service
public class FacturaService {

    private final GcFacturaRepository facturaRepository;
    private final GcContratoFormaPagoRepository formaPagoRepository;
    private final GcFormaPagoGestionRepository gestionRepository;
    private final GcEmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public FacturaService(GcFacturaRepository facturaRepository,
                          GcContratoFormaPagoRepository formaPagoRepository,
                          GcFormaPagoGestionRepository gestionRepository,
                          GcEmpresaRepository empresaRepository,
                          SecurityUtils securityUtils) {
        this.facturaRepository = facturaRepository;
        this.formaPagoRepository = formaPagoRepository;
        this.gestionRepository = gestionRepository;
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public FacturaResponse obtenerPorId(Long id) {
        GcFactura factura = facturaRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Factura no encontrada con ID: " + id));
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
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa no encontrada"));

        // Verificar unicidad
        if (facturaRepository.findByNumeroFacturaAndPrefijo(request.getNumeroFactura(), request.getPrefijo()).isPresent()) {
            throw new BusinessException("DUPLICATE_ERROR",
                "Ya existe una factura con número " + request.getNumeroFactura() +
                (StringUtils.hasText(request.getPrefijo()) ? " prefijo " + request.getPrefijo() : ""));
        }

        GcFactura factura = new GcFactura();
        factura.setEmpresa(empresa);

        if (request.getEmpresaFacturacionId() != null) {
            GcEmpresa empresaFact = empresaRepository.findById(request.getEmpresaFacturacionId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND", "Empresa de facturación no encontrada"));
            factura.setEmpresaFacturacion(empresaFact);
        }

        factura.setNumeroFactura(request.getNumeroFactura().trim());
        factura.setPrefijo(request.getPrefijo() != null ? request.getPrefijo().trim() : null);
        factura.setFechaEmision(request.getFechaEmision());
        factura.setFechaVencimiento(request.getFechaVencimiento());
        factura.setValorBase(request.getValorBase());
        factura.setValorIva(request.getValorIva());
        factura.setValorTotal(request.getValorTotal());
        factura.setMoneda(StringUtils.hasText(request.getMoneda()) ? request.getMoneda() : "COP");
        factura.setFactroId(request.getFactroId());
        factura.setObservaciones(request.getObservaciones());
        factura.setCreadoPor(securityUtils.getCurrentUserId());

        factura = facturaRepository.save(factura);
        factura = facturaRepository.findByIdWithRelations(factura.getId()).orElse(factura);
        return FacturaResponse.fromEntity(factura);
    }

    /**
     * RN-01: Una forma de pago solo puede cruzarse con UNA factura
     * RN-03: Al cruzar, estado → FACTURADA, se actualiza valor_facturado
     * RN-04: Solo se pueden cruzar formas de pago PENDIENTES
     */
    @Transactional
    public void cruzarConFormaPago(Long facturaId, Long formaPagoId, BigDecimal valorFacturado) {
        GcFactura factura = facturaRepository.findById(facturaId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Factura no encontrada"));

        GcContratoFormaPago fp = formaPagoRepository.findById(formaPagoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Forma de pago no encontrada"));

        if (fp.getEstado() != EstadoFormaPago.PENDIENTE) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se pueden cruzar formas de pago PENDIENTES. Estado actual: " + fp.getEstado());
        }

        if (fp.getFacturaId() != null) {
            throw new BusinessException("BUSINESS_ERROR", "Esta forma de pago ya está cruzada con otra factura");
        }

        // Cruzar
        fp.setFacturaId(facturaId);
        fp.setEstado(EstadoFormaPago.FACTURADA);
        fp.setValorFacturado(valorFacturado != null ? valorFacturado : factura.getValorTotal());
        fp.setModificadoPor(securityUtils.getCurrentUserId());
        formaPagoRepository.save(fp);

        // Registrar en bitácora automáticamente
        GcFormaPagoGestion gestion = new GcFormaPagoGestion();
        gestion.setFormaPago(fp);
        gestion.setTipoGestion(TipoGestion.FACTURA_CRUZADA);
        gestion.setDescripcion("Cruzada con factura " +
            (factura.getPrefijo() != null ? factura.getPrefijo() + "-" : "") +
            factura.getNumeroFactura() +
            " por " + fp.getValorFacturado());
        gestion.setFechaGestion(LocalDate.now());
        gestion.setCreadoPor(securityUtils.getCurrentUserId());
        gestionRepository.save(gestion);
    }

    /**
     * RN-05: Se puede descruzar — forma de pago vuelve a PENDIENTE
     */
    @Transactional
    public void descruzarFormaPago(Long formaPagoId) {
        GcContratoFormaPago fp = formaPagoRepository.findById(formaPagoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Forma de pago no encontrada"));

        if (fp.getFacturaId() == null) {
            throw new BusinessException("BUSINESS_ERROR", "Esta forma de pago no está cruzada con ninguna factura");
        }

        fp.setFacturaId(null);
        fp.setEstado(EstadoFormaPago.PENDIENTE);
        fp.setValorFacturado(null);
        fp.setModificadoPor(securityUtils.getCurrentUserId());
        formaPagoRepository.save(fp);
    }
}
