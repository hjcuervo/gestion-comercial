package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import com.arquitecsoft.gestion.domain.contrato.repository.GcContratoRepository;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.repository.GcEmpresaRepository;
import com.arquitecsoft.gestion.domain.facturacion.dto.*;
import com.arquitecsoft.gestion.domain.facturacion.entity.*;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoEvento.TipoEvento;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoIngreso.EstadoCompromiso;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoIngreso.TipoCompromiso;
import com.arquitecsoft.gestion.domain.facturacion.repository.*;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio core del módulo de Compromisos de Ingreso.
 *
 * Encapsula:
 *  - Creación de compromisos (evento COMPROMISO_CREADO)
 *  - Comandos de transición de estado (validados contra CompromisoEstadoMachine)
 *  - Registro y reversión de facturas (recalcula acumulado, fuerza estados
 *    automáticos CUMPLIDO / PARCIALMENTE_CUMPLIDO)
 *  - Emisión de eventos en GC_COMPROMISO_EVENTO en cada operación
 *
 * Reglas clave (spec §5):
 *  - El monto presupuestado original es INMUTABLE.
 *  - Los compromisos NUNCA se eliminan.
 *  - Toda operación que cambie estado/monto/fecha emite evento.
 *  - Las aplicaciones de factura revertidas NO se borran, se marcan.
 */
@Service
public class CompromisoIngresoService {

    private final GcCompromisoIngresoRepository compromisoRepository;
    private final GcCompromisoFacturaRepository aplicacionRepository;
    private final GcCompromisoEventoRepository eventoRepository;
    private final GcContratoRepository contratoRepository;
    private final GcFacturaRepository facturaRepository;
    private final GcEmpresaRepository empresaRepository;
    private final SecurityUtils securityUtils;

    public CompromisoIngresoService(
            GcCompromisoIngresoRepository compromisoRepository,
            GcCompromisoFacturaRepository aplicacionRepository,
            GcCompromisoEventoRepository eventoRepository,
            GcContratoRepository contratoRepository,
            GcFacturaRepository facturaRepository,
            GcEmpresaRepository empresaRepository,
            SecurityUtils securityUtils) {
        this.compromisoRepository = compromisoRepository;
        this.aplicacionRepository = aplicacionRepository;
        this.eventoRepository = eventoRepository;
        this.contratoRepository = contratoRepository;
        this.facturaRepository = facturaRepository;
        this.empresaRepository = empresaRepository;
        this.securityUtils = securityUtils;
    }

    // ==================== CONSULTAS ====================

    @Transactional(readOnly = true)
    public CompromisoIngresoResponse obtenerPorId(Long id) {
        GcCompromisoIngreso c = cargar(id);
        return CompromisoIngresoResponse.fromEntity(c);
    }

    @Transactional(readOnly = true)
    public List<CompromisoIngresoResponse> listarPorContrato(Long contratoId) {
        return compromisoRepository.findByContratoIdOrderByFechaEsperadaActualAsc(contratoId).stream()
                .map(CompromisoIngresoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CompromisoEventoResponse> listarEventos(Long compromisoId) {
        return eventoRepository.findByCompromisoIdOrderByFechaDesc(compromisoId).stream()
                .map(CompromisoEventoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ==================== CREACIÓN ====================

    /**
     * Crea un compromiso en estado PENDIENTE_GESTION y emite COMPROMISO_CREADO.
     *
     * El monto presupuestado establecido aquí queda INMUTABLE — ajustes
     * posteriores van contra montoEsperadoActual vía ajustarMonto().
     */
    @Transactional
    public CompromisoIngresoResponse crear(CompromisoIngresoCreateRequest request) {
        if (request.getMontoPresupuestado() == null ||
            request.getMontoPresupuestado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("VALIDATION_ERROR",
                "El monto presupuestado debe ser mayor a cero.");
        }
        if (request.getFechaEsperada() == null) {
            throw new BusinessException("VALIDATION_ERROR",
                "La fecha esperada es obligatoria.");
        }
        if (!StringUtils.hasText(request.getDescripcion())) {
            throw new BusinessException("VALIDATION_ERROR",
                "La descripción del compromiso es obligatoria.");
        }

        GcContrato contrato = contratoRepository.findById(request.getContratoId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Contrato no encontrado con ID: " + request.getContratoId()));

        if (!contrato.isVigente()) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se pueden agregar compromisos a contratos VIGENTES. " +
                "Estado actual: " + contrato.getEstado());
        }

        GcCompromisoIngreso c = new GcCompromisoIngreso();
        c.setContrato(contrato);
        c.setDescripcion(request.getDescripcion().trim());
        c.setMontoPresupuestado(request.getMontoPresupuestado());
        c.setMontoEsperadoActual(request.getMontoPresupuestado());
        c.setMontoFacturadoAcumulado(BigDecimal.ZERO);
        c.setFechaEsperadaOriginal(request.getFechaEsperada());
        c.setFechaEsperadaActual(request.getFechaEsperada());
        c.setEstado(EstadoCompromiso.PENDIENTE_GESTION);
        c.setEnRiesgo(false);

        // Tipo
        if (StringUtils.hasText(request.getTipo())) {
            try {
                c.setTipo(TipoCompromiso.valueOf(request.getTipo()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException("VALIDATION_ERROR",
                    "Tipo de compromiso inválido: " + request.getTipo());
            }
        } else {
            c.setTipo(TipoCompromiso.NUEVO);
        }

        // Empresa de facturación
        if (request.getEmpresaFacturacionId() != null) {
            GcEmpresa empresa = empresaRepository.findById(request.getEmpresaFacturacionId())
                    .orElseThrow(() -> new BusinessException("NOT_FOUND",
                        "Empresa de facturación no encontrada con ID: " + request.getEmpresaFacturacionId()));
            c.setEmpresaFacturacion(empresa);
        }

        // Moneda — si no viene, hereda del contrato
        c.setMoneda(StringUtils.hasText(request.getMoneda())
                ? request.getMoneda()
                : (contrato.getMoneda() != null ? contrato.getMoneda() : "COP"));

        // Reemplazo (spec §3.6, §3.11)
        if (request.getReemplazaAId() != null) {
            GcCompromisoIngreso original = cargar(request.getReemplazaAId());
            if (original.getEstado() != EstadoCompromiso.NO_LOGRADO) {
                throw new BusinessException("BUSINESS_ERROR",
                    "Solo se puede reemplazar un compromiso en estado NO_LOGRADO. " +
                    "Estado del original: " + original.getEstado());
            }
            c.setReemplazaA(original);
            if (c.getTipo() == TipoCompromiso.NUEVO) {
                c.setTipo(TipoCompromiso.REEMPLAZO);
            }
        }

        c.setCreadoPor(securityUtils.getCurrentUserId());
        c = compromisoRepository.save(c);

        registrarEvento(c, TipoEvento.COMPROMISO_CREADO, null, c.getEstado(),
                null, null, null, null, null, null);

        tocarActividad(c);
        return CompromisoIngresoResponse.fromEntity(c);
    }

    // ==================== COMANDOS DE TRANSICIÓN ====================

    @Transactional
    public CompromisoIngresoResponse iniciarGestion(Long id) {
        GcCompromisoIngreso c = cargar(id);
        return transicionar(c, EstadoCompromiso.EN_GESTION,
                TipoEvento.GESTION_INICIADA, null);
    }

    @Transactional
    public CompromisoIngresoResponse confirmar(Long id) {
        GcCompromisoIngreso c = cargar(id);
        return transicionar(c, EstadoCompromiso.COMPROMETIDO,
                TipoEvento.COMPROMISO_CONFIRMADO, null);
    }

    /**
     * Reprogramación — cambia fechaEsperadaActual. Mantiene fechaEsperadaOriginal.
     * Obligatorios: nuevaFecha, motivo.
     */
    @Transactional
    public CompromisoIngresoResponse reprogramar(Long id, CompromisoComandoRequest req) {
        if (req.getNuevaFecha() == null) {
            throw new BusinessException("VALIDATION_ERROR",
                "La nueva fecha esperada es obligatoria.");
        }
        if (!StringUtils.hasText(req.getMotivo())) {
            throw new BusinessException("VALIDATION_ERROR",
                "El motivo de reprogramación es obligatorio.");
        }

        GcCompromisoIngreso c = cargar(id);
        CompromisoEstadoMachine.validarTransicion(c.getEstado(), EstadoCompromiso.REPROGRAMADO);

        EstadoCompromiso estadoAnterior = c.getEstado();
        LocalDate fechaAnterior = c.getFechaEsperadaActual();

        c.setFechaEsperadaActual(req.getNuevaFecha());
        c.setEstado(EstadoCompromiso.REPROGRAMADO);
        c.setModificadoPor(securityUtils.getCurrentUserId());
        c = compromisoRepository.save(c);

        registrarEvento(c, TipoEvento.FECHA_REPROGRAMADA, estadoAnterior, c.getEstado(),
                null, null, fechaAnterior, req.getNuevaFecha(), null, req.getMotivo());

        tocarActividad(c);
        return CompromisoIngresoResponse.fromEntity(c);
    }

    /**
     * Ajuste de monto esperado (no cambia el presupuestado original).
     * No cambia estado — emite evento MONTO_AJUSTADO.
     */
    @Transactional
    public CompromisoIngresoResponse ajustarMonto(Long id, CompromisoComandoRequest req) {
        if (req.getNuevoMonto() == null || req.getNuevoMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("VALIDATION_ERROR",
                "El nuevo monto esperado debe ser mayor a cero.");
        }
        if (!StringUtils.hasText(req.getMotivo())) {
            throw new BusinessException("VALIDATION_ERROR",
                "El motivo del ajuste de monto es obligatorio.");
        }

        GcCompromisoIngreso c = cargar(id);
        if (c.isEstadoFinal()) {
            throw new BusinessException("BUSINESS_ERROR",
                "No se puede ajustar el monto de un compromiso en estado final.");
        }

        BigDecimal montoAnterior = c.getMontoEsperadoActual();
        c.setMontoEsperadoActual(req.getNuevoMonto());
        c.setModificadoPor(securityUtils.getCurrentUserId());
        c = compromisoRepository.save(c);

        registrarEvento(c, TipoEvento.MONTO_AJUSTADO, c.getEstado(), c.getEstado(),
                montoAnterior, req.getNuevoMonto(), null, null, null, req.getMotivo());

        tocarActividad(c);
        return CompromisoIngresoResponse.fromEntity(c);
    }

    @Transactional
    public CompromisoIngresoResponse marcarPerdido(Long id, CompromisoComandoRequest req) {
        if (!StringUtils.hasText(req.getMotivo())) {
            throw new BusinessException("VALIDATION_ERROR",
                "El motivo de pérdida es obligatorio (spec §5.7).");
        }
        GcCompromisoIngreso c = cargar(id);
        CompromisoEstadoMachine.validarTransicion(c.getEstado(), EstadoCompromiso.NO_LOGRADO);

        EstadoCompromiso estadoAnterior = c.getEstado();
        c.setEstado(EstadoCompromiso.NO_LOGRADO);
        c.setMotivoPerdida(req.getMotivo());
        c.setModificadoPor(securityUtils.getCurrentUserId());
        c = compromisoRepository.save(c);

        registrarEvento(c, TipoEvento.COMPROMISO_PERDIDO, estadoAnterior, c.getEstado(),
                null, null, null, null, null, req.getMotivo());

        return CompromisoIngresoResponse.fromEntity(c);
    }

    /**
     * Reactivar un compromiso REPROGRAMADO → EN_GESTION.
     */
    @Transactional
    public CompromisoIngresoResponse reactivar(Long id) {
        GcCompromisoIngreso c = cargar(id);
        if (c.getEstado() != EstadoCompromiso.REPROGRAMADO) {
            throw new BusinessException("BUSINESS_ERROR",
                "Solo se puede reactivar un compromiso en estado REPROGRAMADO. " +
                "Estado actual: " + c.getEstado());
        }
        return transicionar(c, EstadoCompromiso.EN_GESTION,
                TipoEvento.COMPROMISO_REACTIVADO, null);
    }

    // ==================== FACTURACIÓN ====================

    /**
     * Registra una aplicación de factura contra el compromiso.
     * Spec §4.5 / §3.1: el estado se fuerza automáticamente según el acumulado.
     */
    @Transactional
    public CompromisoIngresoResponse registrarFactura(Long compromisoId, RegistrarFacturaRequest req) {
        if (req.getFacturaId() == null) {
            throw new BusinessException("VALIDATION_ERROR", "facturaId es obligatorio.");
        }
        if (req.getMontoAplicado() == null ||
            req.getMontoAplicado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("VALIDATION_ERROR",
                "El monto aplicado debe ser mayor a cero.");
        }

        GcCompromisoIngreso c = cargar(compromisoId);
        if (c.isEstadoFinal()) {
            throw new BusinessException("BUSINESS_ERROR",
                "No se puede facturar contra un compromiso en estado final.");
        }

        GcFactura factura = facturaRepository.findById(req.getFacturaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Factura no encontrada con ID: " + req.getFacturaId()));

        if (Boolean.TRUE.equals(factura.getAnulada())) {
            throw new BusinessException("BUSINESS_ERROR",
                "No se puede aplicar una factura anulada.");
        }

        // Validar que la suma de aplicaciones vigentes + la nueva no exceda el valor total de la factura
        BigDecimal yaAplicado = aplicacionRepository.sumAplicadoVigenteByFacturaId(factura.getId());
        BigDecimal nuevaSumaFactura = yaAplicado.add(req.getMontoAplicado());
        if (nuevaSumaFactura.compareTo(factura.getValorTotal()) > 0) {
            throw new BusinessException("BUSINESS_ERROR",
                "La suma de aplicaciones (" + nuevaSumaFactura +
                ") excedería el valor total de la factura (" + factura.getValorTotal() + ").");
        }

        // Crear aplicación
        GcCompromisoFactura aplicacion = new GcCompromisoFactura();
        aplicacion.setCompromiso(c);
        aplicacion.setFactura(factura);
        aplicacion.setMontoAplicado(req.getMontoAplicado());
        aplicacion.setRevertida(false);
        aplicacion.setCreadoPor(securityUtils.getCurrentUserId());
        aplicacion = aplicacionRepository.save(aplicacion);

        // Recalcular acumulado y estado
        EstadoCompromiso estadoAnterior = c.getEstado();
        BigDecimal nuevoAcumulado = aplicacionRepository.sumAplicadoVigenteByCompromisoId(c.getId());
        c.setMontoFacturadoAcumulado(nuevoAcumulado);
        EstadoCompromiso nuevoEstado = calcularEstadoPorAcumulado(c);
        c.setEstado(nuevoEstado);
        c.setModificadoPor(securityUtils.getCurrentUserId());
        c = compromisoRepository.save(c);

        registrarEvento(c, TipoEvento.FACTURA_REGISTRADA, estadoAnterior, c.getEstado(),
                null, req.getMontoAplicado(), null, null, aplicacion.getId(), null);

        tocarActividad(c);
        return CompromisoIngresoResponse.fromEntity(c);
    }

    /**
     * Revierte una aplicación. Spec §3.8: NO se elimina — se marca.
     */
    @Transactional
    public CompromisoIngresoResponse revertirFactura(Long compromisoId, RevertirFacturaRequest req) {
        if (req.getCompromisoFacturaId() == null) {
            throw new BusinessException("VALIDATION_ERROR",
                "compromisoFacturaId es obligatorio.");
        }
        if (!StringUtils.hasText(req.getMotivo())) {
            throw new BusinessException("VALIDATION_ERROR",
                "El motivo de reversión es obligatorio.");
        }

        GcCompromisoFactura aplicacion = aplicacionRepository.findById(req.getCompromisoFacturaId())
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Aplicación de factura no encontrada con ID: " + req.getCompromisoFacturaId()));

        if (!aplicacion.getCompromiso().getId().equals(compromisoId)) {
            throw new BusinessException("BUSINESS_ERROR",
                "La aplicación no pertenece al compromiso indicado.");
        }

        if (Boolean.TRUE.equals(aplicacion.getRevertida())) {
            throw new BusinessException("BUSINESS_ERROR",
                "La aplicación ya fue revertida previamente.");
        }

        // Marcar como revertida — NO se elimina.
        aplicacion.setRevertida(true);
        aplicacion.setFechaReversion(LocalDateTime.now());
        aplicacion.setMotivoReversion(req.getMotivo());
        aplicacionRepository.save(aplicacion);

        GcCompromisoIngreso c = cargar(compromisoId);
        EstadoCompromiso estadoAnterior = c.getEstado();

        BigDecimal nuevoAcumulado = aplicacionRepository.sumAplicadoVigenteByCompromisoId(c.getId());
        c.setMontoFacturadoAcumulado(nuevoAcumulado);

        // Tras reversión, si era CUMPLIDO/PARCIALMENTE_CUMPLIDO, recalcular;
        // si era final (CUMPLIDO), pasa a PARCIALMENTE_CUMPLIDO o EN_GESTION según acumulado.
        EstadoCompromiso nuevoEstado = calcularEstadoPorAcumulado(c);
        c.setEstado(nuevoEstado);
        c.setModificadoPor(securityUtils.getCurrentUserId());
        c = compromisoRepository.save(c);

        registrarEvento(c, TipoEvento.FACTURA_REVERTIDA, estadoAnterior, c.getEstado(),
                aplicacion.getMontoAplicado(), null, null, null, aplicacion.getId(), req.getMotivo());

        tocarActividad(c);
        return CompromisoIngresoResponse.fromEntity(c);
    }

    // ==================== HELPERS PRIVADOS ====================

    private GcCompromisoIngreso cargar(Long id) {
        return compromisoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("NOT_FOUND",
                    "Compromiso de ingreso no encontrado con ID: " + id));
    }

    /**
     * Transición simple: valida contra la máquina, actualiza estado, emite evento.
     * Para transiciones que requieren payload (reprogramar, ajustar, perder) usar
     * el método específico.
     */
    private CompromisoIngresoResponse transicionar(
            GcCompromisoIngreso c, EstadoCompromiso nuevoEstado,
            TipoEvento tipoEvento, String motivo) {

        CompromisoEstadoMachine.validarTransicion(c.getEstado(), nuevoEstado);

        EstadoCompromiso estadoAnterior = c.getEstado();
        c.setEstado(nuevoEstado);
        c.setModificadoPor(securityUtils.getCurrentUserId());
        c = compromisoRepository.save(c);

        registrarEvento(c, tipoEvento, estadoAnterior, c.getEstado(),
                null, null, null, null, null, motivo);

        tocarActividad(c);
        return CompromisoIngresoResponse.fromEntity(c);
    }

    /**
     * Calcula el estado automático tras un cambio de acumulado.
     *
     * Reglas spec §5.1 / §5.2 / §3.2 (sobre-ejecución):
     *  - acumulado == 0 y estado anterior era final → EN_GESTION
     *  - 0 < acumulado < presupuestado → PARCIALMENTE_CUMPLIDO
     *  - acumulado >= presupuestado → CUMPLIDO (permite sobre-ejecución)
     */
    private EstadoCompromiso calcularEstadoPorAcumulado(GcCompromisoIngreso c) {
        BigDecimal acum = c.getMontoFacturadoAcumulado() != null
                ? c.getMontoFacturadoAcumulado() : BigDecimal.ZERO;
        BigDecimal pres = c.getMontoPresupuestado();

        if (acum.compareTo(BigDecimal.ZERO) == 0) {
            // Sin facturación: si estaba en estado post-facturación, regresa a EN_GESTION.
            if (c.getEstado() == EstadoCompromiso.PARCIALMENTE_CUMPLIDO
                    || c.getEstado() == EstadoCompromiso.CUMPLIDO) {
                return EstadoCompromiso.EN_GESTION;
            }
            return c.getEstado();
        }

        if (acum.compareTo(pres) >= 0) {
            return EstadoCompromiso.CUMPLIDO;
        }

        return EstadoCompromiso.PARCIALMENTE_CUMPLIDO;
    }

    /**
     * Marca la última actividad del compromiso — base para detección de
     * inactividad (spec §5.5). También limpia el flag enRiesgo.
     */
    private void tocarActividad(GcCompromisoIngreso c) {
        c.setFechaUltimaActividad(LocalDateTime.now());
        if (Boolean.TRUE.equals(c.getEnRiesgo())) {
            c.setEnRiesgo(false);
        }
        compromisoRepository.save(c);
    }

    /**
     * Constructor unificado de evento. Todos los parámetros opcionales
     * según el tipo (ver GcCompromisoEvento).
     */
    private void registrarEvento(
            GcCompromisoIngreso compromiso,
            TipoEvento tipo,
            EstadoCompromiso estadoAnterior,
            EstadoCompromiso estadoNuevo,
            BigDecimal montoAnterior,
            BigDecimal montoNuevo,
            LocalDate fechaAnterior,
            LocalDate fechaNueva,
            Long compromisoFacturaId,
            String motivo) {

        GcCompromisoEvento evento = new GcCompromisoEvento();
        evento.setCompromiso(compromiso);
        evento.setTipoEvento(tipo);
        evento.setEstadoAnterior(estadoAnterior);
        evento.setEstadoNuevo(estadoNuevo);
        evento.setMontoAnterior(montoAnterior);
        evento.setMontoNuevo(montoNuevo);
        evento.setFechaAnterior(fechaAnterior);
        evento.setFechaNueva(fechaNueva);
        evento.setCompromisoFacturaId(compromisoFacturaId);
        evento.setMotivo(motivo);
        evento.setUsuarioId(securityUtils.getCurrentUserId());
        eventoRepository.save(evento);
    }
}
