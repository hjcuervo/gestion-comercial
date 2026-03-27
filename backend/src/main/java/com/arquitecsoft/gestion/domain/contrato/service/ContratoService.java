package com.arquitecsoft.gestion.domain.contrato.service;

import com.arquitecsoft.gestion.domain.contrato.dto.*;
import com.arquitecsoft.gestion.domain.contrato.entity.*;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato.EstadoContrato;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago.EstadoFormaPago;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoModificacion.TipoModificacion;
import com.arquitecsoft.gestion.domain.contrato.entity.GcProcesoContratacion.EstadoProceso;
import com.arquitecsoft.gestion.domain.contrato.repository.*;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContratoService {

    private final GcContratoRepository contratoRepository;
    private final GcContratoFormaPagoRepository formaPagoRepository;
    private final GcContratoModificacionRepository modificacionRepository;
    private final GcProcesoContratacionRepository procesoRepository;
    private final GcTipoContratoRepository tipoContratoRepository;
    private final GcEmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public ContratoService(
            GcContratoRepository contratoRepository,
            GcContratoFormaPagoRepository formaPagoRepository,
            GcContratoModificacionRepository modificacionRepository,
            GcProcesoContratacionRepository procesoRepository,
            GcTipoContratoRepository tipoContratoRepository,
            GcEmpresaRepository empresaRepository,
            SecurityUtils securityUtils) {
        this.contratoRepository = contratoRepository;
        this.formaPagoRepository = formaPagoRepository;
        this.modificacionRepository = modificacionRepository;
        this.procesoRepository = procesoRepository;
        this.tipoContratoRepository = tipoContratoRepository;
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }

    // ==================== CONTRATO ====================

    @Transactional(readOnly = true)
    public ContratoResponse obtenerPorId(Long id) {
        GcContrato contrato = contratoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Contrato no encontrado con ID: " + id));
        return ContratoResponse.fromEntity(contrato);
    }

    @Transactional(readOnly = true)
    public PageResponse<ContratoResponse> listar(String estado, Long empresaId, String moneda, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("fechaCreacion").descending());

        EstadoContrato estadoEnum = null;
        if (StringUtils.hasText(estado)) {
            try { estadoEnum = EstadoContrato.valueOf(estado); }
            catch (IllegalArgumentException e) { throw new BusinessException("VALIDATION_ERROR", "Estado inválido: " + estado); }
        }

        Page<GcContrato> pageResult = contratoRepository.findWithFilters(estadoEnum, empresaId, moneda, pageable);
        return PageResponse.from(pageResult, ContratoResponse::fromEntitySimple);
    }

    @Transactional(readOnly = true)
    public List<ContratoResponse> listarPorOportunidad(Long oportunidadId) {
        return contratoRepository.findByOportunidadId(oportunidadId).stream()
                .map(ContratoResponse::fromEntitySimple)
                .collect(Collectors.toList());
    }

    /**
     * RN-05: Solo se puede crear cuando el proceso está COMPLETADO
     * RN-06: Hereda datos de la oportunidad
     * RN-08: Nace en estado VIGENTE
     */
    @Transactional
    public ContratoResponse crearDesdeProcesoCompletado(ContratoCreateRequest request) {
        GcProcesoContratacion proceso = procesoRepository.findByIdWithRelations(request.getProcesoContratacionId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Proceso de contratación no encontrado con ID: " + request.getProcesoContratacionId()));

        if (proceso.getEstado() != EstadoProceso.COMPLETADO) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se puede crear un contrato cuando el proceso está COMPLETADO. Estado actual: " + proceso.getEstado());
        }

        GcTipoContrato tipoContrato = tipoContratoRepository.findById(request.getTipoContratoId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Tipo de contrato no encontrado con ID: " + request.getTipoContratoId()));

        GcContrato contrato = new GcContrato();
        contrato.setProcesoContratacion(proceso);
        contrato.setOportunidad(proceso.getOportunidad());
        contrato.setEmpresa(proceso.getEmpresa());
        contrato.setTipoContrato(tipoContrato);

        // Empresa de facturación (filial)
        if (request.getEmpresaFacturacionId() != null) {
            GcEmpresa empresaFact = empresaRepository.findById(request.getEmpresaFacturacionId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND",
                        "Empresa de facturación no encontrada con ID: " + request.getEmpresaFacturacionId()));
            contrato.setEmpresaFacturacion(empresaFact);
        }

        contrato.setNumeroContratoInterno(request.getNumeroContratoInterno());
        contrato.setNumeroContratoCliente(request.getNumeroContratoCliente());
        contrato.setObjeto(request.getObjeto());

        // Hereda moneda y valor de la oportunidad si no se especifican
        contrato.setMoneda(StringUtils.hasText(request.getMoneda()) ? request.getMoneda()
                : (proceso.getOportunidad().getMoneda() != null ? proceso.getOportunidad().getMoneda() : "COP"));
        contrato.setValorContrato(request.getValorContrato() != null ? request.getValorContrato()
                : proceso.getOportunidad().getValorEstimado());
        contrato.setValorAjuste(BigDecimal.ZERO);

        contrato.setFechaInicio(request.getFechaInicio());
        contrato.setFechaFin(request.getFechaFin());
        contrato.setEstado(EstadoContrato.VIGENTE);
        contrato.setResponsableGestion(request.getResponsableGestion());
        contrato.setInterventor(request.getInterventor());
        contrato.setObservaciones(request.getObservaciones());
        contrato.setCreadoPor(securityUtils.getCurrentUserId());

        contrato = contratoRepository.save(contrato);
        contrato = contratoRepository.findByIdWithRelations(contrato.getId()).orElse(contrato);

        return ContratoResponse.fromEntity(contrato);
    }

    @Transactional
    public ContratoResponse suspender(Long id) {
        return cambiarEstado(id, EstadoContrato.SUSPENDIDO, EstadoContrato.VIGENTE, "suspender");
    }

    @Transactional
    public ContratoResponse reiniciar(Long id) {
        return cambiarEstado(id, EstadoContrato.VIGENTE, EstadoContrato.SUSPENDIDO, "reiniciar");
    }

    @Transactional
    public ContratoResponse terminar(Long id) {
        return cambiarEstado(id, EstadoContrato.TERMINADO, EstadoContrato.VIGENTE, "terminar");
    }

    @Transactional
    public ContratoResponse liquidar(Long id) {
        return cambiarEstado(id, EstadoContrato.LIQUIDADO, EstadoContrato.TERMINADO, "liquidar");
    }

    private ContratoResponse cambiarEstado(Long id, EstadoContrato nuevoEstado, EstadoContrato estadoRequerido, String accion) {
        GcContrato contrato = contratoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Contrato no encontrado con ID: " + id));

        if (contrato.getEstado() != estadoRequerido) {
            throw new BusinessException("BUSINESS_ERROR",
                "Para " + accion + " el contrato debe estar en estado " + estadoRequerido + ". Estado actual: " + contrato.getEstado());
        }

        contrato.setEstado(nuevoEstado);
        contrato.setModificadoPor(securityUtils.getCurrentUserId());
        contrato = contratoRepository.save(contrato);

        return ContratoResponse.fromEntity(contrato);
    }

    // ==================== FORMAS DE PAGO ====================

    @Transactional(readOnly = true)
    public List<FormaPagoResponse> listarFormasPago(Long contratoId) {
        return formaPagoRepository.findByContratoIdOrderByFechaEstimadaPagoAsc(contratoId).stream()
                .map(FormaPagoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * RN-09: Suma no puede exceder valor total
     * RN-11: Solo contratos VIGENTES
     */
    @Transactional
    public FormaPagoResponse crearFormaPago(Long contratoId, FormaPagoCreateRequest request) {
        GcContrato contrato = contratoRepository.findByIdWithRelations(contratoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Contrato no encontrado con ID: " + contratoId));

        if (!contrato.isVigente()) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se pueden agregar formas de pago a contratos VIGENTES. Estado actual: " + contrato.getEstado());
        }

        // Validar que la suma no exceda el valor total
        BigDecimal sumaActual = formaPagoRepository.sumValorByContratoId(contratoId);
        BigDecimal nuevaSuma = sumaActual.add(request.getValor());
        if (nuevaSuma.compareTo(contrato.getValorTotal()) > 0) {
            throw new BusinessException("BUSINESS_ERROR",
                "La suma de las formas de pago (" + nuevaSuma + ") excede el valor total del contrato (" + contrato.getValorTotal() + ")");
        }

        GcContratoFormaPago fp = new GcContratoFormaPago();
        fp.setContrato(contrato);
        fp.setDescripcion(request.getDescripcion().trim());
        fp.setValor(request.getValor());
        fp.setFechaEstimadaPago(request.getFechaEstimadaPago());
        fp.setEstado(EstadoFormaPago.PENDIENTE);

        if (request.getEmpresaFacturacionId() != null) {
            GcEmpresa empresaFact = empresaRepository.findById(request.getEmpresaFacturacionId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND",
                        "Empresa de facturación no encontrada con ID: " + request.getEmpresaFacturacionId()));
            fp.setEmpresaFacturacion(empresaFact);
        }

        fp.setCreadoPor(securityUtils.getCurrentUserId());
        fp = formaPagoRepository.save(fp);

        return FormaPagoResponse.fromEntity(fp);
    }

    @Transactional
    public void eliminarFormaPago(Long formaPagoId) {
        GcContratoFormaPago fp = formaPagoRepository.findById(formaPagoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Forma de pago no encontrada con ID: " + formaPagoId));

        if (fp.getEstado() == EstadoFormaPago.FACTURADA) {
            throw new BusinessException("BUSINESS_ERROR", "No se puede eliminar una forma de pago ya facturada");
        }

        formaPagoRepository.delete(fp);
    }

    // ==================== MODIFICACIONES ====================

    @Transactional(readOnly = true)
    public List<ModificacionResponse> listarModificaciones(Long contratoId) {
        return modificacionRepository.findByContratoIdOrderByFechaModificacionContratoDesc(contratoId).stream()
                .map(ModificacionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * RN-12: Adiciones incrementan valor_ajuste
     * RN-13: Prórrogas modifican fecha_fin
     * RN-14: Solo contratos VIGENTES
     */
    @Transactional
    public ModificacionResponse crearModificacion(Long contratoId, ModificacionCreateRequest request) {
        GcContrato contrato = contratoRepository.findByIdWithRelations(contratoId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Contrato no encontrado con ID: " + contratoId));

        if (!contrato.isModificable()) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se pueden modificar contratos en estado VIGENTE. Estado actual: " + contrato.getEstado());
        }

        TipoModificacion tipo;
        try { tipo = TipoModificacion.valueOf(request.getTipoModificacion()); }
        catch (IllegalArgumentException e) { throw new BusinessException("VALIDATION_ERROR", "Tipo de modificación inválido: " + request.getTipoModificacion()); }

        GcContratoModificacion mod = new GcContratoModificacion();
        mod.setContrato(contrato);
        mod.setTipoModificacion(tipo);
        mod.setModificaTiempo(request.getModificaTiempo() != null ? request.getModificaTiempo() : false);
        mod.setModificaValor(request.getModificaValor() != null ? request.getModificaValor() : false);
        mod.setValorModificacion(request.getValorModificacion());
        mod.setNuevaFechaFin(request.getNuevaFechaFin());
        mod.setFechaModificacionContrato(request.getFechaModificacion());
        mod.setObservaciones(request.getObservaciones());
        mod.setCreadoPor(securityUtils.getCurrentUserId());

        mod = modificacionRepository.save(mod);

        // Aplicar efectos al contrato
        if (Boolean.TRUE.equals(request.getModificaValor()) && request.getValorModificacion() != null) {
            BigDecimal ajusteActual = contrato.getValorAjuste() != null ? contrato.getValorAjuste() : BigDecimal.ZERO;
            contrato.setValorAjuste(ajusteActual.add(request.getValorModificacion()));
        }
        if (Boolean.TRUE.equals(request.getModificaTiempo()) && request.getNuevaFechaFin() != null) {
            contrato.setFechaFin(request.getNuevaFechaFin());
        }

        contrato.setModificadoPor(securityUtils.getCurrentUserId());
        contratoRepository.save(contrato);

        return ModificacionResponse.fromEntity(mod);
    }
}
