package com.arquitecsoft.gestion.domain.contrato.service;

import com.arquitecsoft.gestion.domain.contrato.dto.*;
import com.arquitecsoft.gestion.domain.contrato.entity.*;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato.EstadoContrato;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoModificacion.TipoModificacion;
import com.arquitecsoft.gestion.domain.contrato.entity.GcProcesoContratacion.EstadoProceso;
import com.arquitecsoft.gestion.domain.contrato.repository.*;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.oportunidad.repository.GcOportunidadRepository;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcCompromisoIngresoRepository;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de contratos.
 *
 * NOTA (Mundo 3 rediseñado): La sección antigua de formas de pago fue retirada.
 * La gestión de compromisos de ingreso (antes "formas de pago") vive ahora
 * en CompromisoIngresoService (domain/facturacion/service/).
 */
@Service
public class ContratoService {

    private final GcContratoRepository contratoRepository;
    private final GcContratoModificacionRepository modificacionRepository;
    private final GcCompromisoIngresoRepository compromisoIngresoRepository;
    private final GcProcesoContratacionRepository procesoRepository;
    private final GcTipoContratoRepository tipoContratoRepository;
    private final GcEmpresaRepository empresaRepository;
    private final GcOportunidadRepository oportunidadRepository;
    private final SecurityUtils securityUtils;

    public ContratoService(
            GcContratoRepository contratoRepository,
            GcContratoModificacionRepository modificacionRepository,
            GcCompromisoIngresoRepository compromisoIngresoRepository,
            GcProcesoContratacionRepository procesoRepository,
            GcTipoContratoRepository tipoContratoRepository,
            GcEmpresaRepository empresaRepository,
            GcOportunidadRepository oportunidadRepository,
            SecurityUtils securityUtils) {
        this.contratoRepository = contratoRepository;
        this.modificacionRepository = modificacionRepository;
        this.compromisoIngresoRepository = compromisoIngresoRepository;
        this.procesoRepository = procesoRepository;
        this.tipoContratoRepository = tipoContratoRepository;
        this.empresaRepository = empresaRepository;
        this.oportunidadRepository = oportunidadRepository;
        this.securityUtils = securityUtils;
    }

    // ==================== CONTRATO ====================

    @Transactional(readOnly = true)
    public ContratoResponse obtenerPorId(Long id) {
        GcContrato contrato = contratoRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Contrato no encontrado con ID: " + id));
        // DT-03: findByIdWithRelations NO fetcha la colección 'compromisos' (evita MultipleBagFetchException
        // al haber dos List). La cargamos explícitamente dentro de la transacción y la seteamos para que
        // ContratoResponse.fromEntity no dispare un lazy-load sobre una colección no inicializada.
        contrato.setCompromisos(
                compromisoIngresoRepository.findByContratoIdOrderByFechaEsperadaActualAsc(contrato.getId()));
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

    /**
     * Formaliza un contrato desde una oportunidad en estado GANADA.
     *
     * Flujo:
     *  1. Valida que la oportunidad exista y esté en GANADA.
     *  2. Crea el contrato con datos heredados de la oportunidad (moneda, valor)
     *     permitiendo que el request los sobrescriba.
     *  3. Marca la oportunidad como CONTRATADA (sale de todos los pipelines).
     *
     * IMPORTANTE: contrato.procesoContratacion_id queda en null en este flujo
     * (el nuevo modelo permite contratos formalizados sin proceso previo).
     * La columna fue relajada a nullable en sesiones previas — ver memoria del
     * proyecto.
     *
     * Supuestos que pueden requerir ajuste según la implementación real de
     * GcOportunidad en el repo:
     *  - Estado GANADA existe en GcOportunidad.EstadoMacro
     *  - Estado CONTRATADA existe (confirmado por memoria: CHECK constraint
     *    CK_GC_OPORTUNIDAD_ESTADO ya incluye CONTRATADA)
     *  - oportunidad.getMoneda() y oportunidad.getValorEstimado() existen
     *    (usados también por crearDesdeProcesoCompletado).
     */
    @Transactional
    public ContratoResponse formalizarContrato(FormalizarContratoRequest request) {
        GcOportunidad oportunidad = oportunidadRepository.findById(request.getOportunidadId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Oportunidad no encontrada con ID: " + request.getOportunidadId()));

        // Validar estado
        if (oportunidad.getEstadoMacro() != GcOportunidad.EstadoMacro.GANADA) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se puede formalizar contrato desde una oportunidad en estado GANADA. " +
                "Estado actual: " + oportunidad.getEstadoMacro());
        }

        GcTipoContrato tipoContrato = tipoContratoRepository.findById(request.getTipoContratoId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Tipo de contrato no encontrado con ID: " + request.getTipoContratoId()));

        GcContrato contrato = new GcContrato();
        // procesoContratacion queda null — flujo de formalización directa.
        contrato.setOportunidad(oportunidad);
        contrato.setEmpresa(oportunidad.getEmpresa());
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
                : (oportunidad.getMoneda() != null ? oportunidad.getMoneda() : "COP"));
        contrato.setValorContrato(request.getValorContrato() != null ? request.getValorContrato()
                : oportunidad.getValorEstimado());
        contrato.setValorAjuste(BigDecimal.ZERO);

        contrato.setFechaInicio(request.getFechaInicio());
        contrato.setFechaFin(request.getFechaFin());
        contrato.setEstado(EstadoContrato.VIGENTE);
        contrato.setResponsableGestion(request.getResponsableGestion());
        contrato.setInterventor(request.getInterventor());
        contrato.setObservaciones(request.getObservaciones());
        contrato.setCreadoPor(securityUtils.getCurrentUserId());

        contrato = contratoRepository.save(contrato);

        // Marcar oportunidad como CONTRATADA (sale de los pipelines).
        // Requiere:
        //  - El valor CONTRATADA en el enum GcOportunidad.EstadoMacro
        //  - El CHECK constraint CK_GC_OPORTUNIDAD_ESTADO en Oracle que lo incluya
        oportunidad.setEstadoMacro(GcOportunidad.EstadoMacro.CONTRATADA);
        oportunidadRepository.save(oportunidad);

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
