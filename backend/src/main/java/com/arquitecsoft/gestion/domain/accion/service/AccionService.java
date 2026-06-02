package com.arquitecsoft.gestion.domain.accion.service;

import com.arquitecsoft.gestion.domain.accion.dto.AccionResponse;
import com.arquitecsoft.gestion.domain.accion.dto.EventoResponse;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion.EstadoAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion.Origen;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion.Prioridad;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion.ReferenciaTipo;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccionEvento;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccionEvento.TipoEvento;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion.OrigenAplicable;
import com.arquitecsoft.gestion.domain.accion.repository.GcAccionEventoRepository;
import com.arquitecsoft.gestion.domain.accion.repository.GcAccionRepository;
import com.arquitecsoft.gestion.domain.accion.repository.GcDisposicionRepository;
import com.arquitecsoft.gestion.domain.accion.service.ManejadorEfectoAccion.SolicitudAccion;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Motor de Acción (spec §1-§9). Maquinaria genérica de trabajo: ciclo de vida de la
 * acción, motor de efectos (genéricos + delegados a subsistemas), la cola dosificada
 * y la reconciliación. No conoce semántica de demanda ni de pipeline.
 */
@Service
public class AccionService {

    /** Tope máximo de la cola "Hoy" (dosificación, RB-MA-07). */
    private static final int MAX_TOPE_VISIBLE = 200;

    /** Estados no terminales (para reconciliación). La posposición se modela como
     *  PENDIENTE con disponible_desde futuro (ver nota de implementación), por lo que
     *  no aparece un estado POSPUESTA almacenado. */
    private static final List<EstadoAccion> ESTADOS_NO_TERMINALES =
            List.of(EstadoAccion.PENDIENTE, EstadoAccion.EN_CURSO);

    private final GcAccionRepository accionRepository;
    private final GcAccionEventoRepository eventoRepository;
    private final GcDisposicionRepository disposicionRepository;
    private final SecurityUtils securityUtils;
    private final TrazaAccionPort trazaPort;
    private final Map<Origen, ManejadorEfectoAccion> manejadoresPorOrigen;

    public AccionService(GcAccionRepository accionRepository,
                         GcAccionEventoRepository eventoRepository,
                         GcDisposicionRepository disposicionRepository,
                         SecurityUtils securityUtils,
                         TrazaAccionPort trazaPort,
                         List<ManejadorEfectoAccion> manejadores) {
        this.accionRepository = accionRepository;
        this.eventoRepository = eventoRepository;
        this.disposicionRepository = disposicionRepository;
        this.securityUtils = securityUtils;
        this.trazaPort = trazaPort;
        this.manejadoresPorOrigen = manejadores.stream()
                .collect(Collectors.toMap(ManejadorEfectoAccion::getOrigen, Function.identity()));
    }

    // ---------------------------------------------------------
    // Cola
    // ---------------------------------------------------------

    /**
     * Cola "Hoy" del usuario actual: acciones PENDIENTE disponibles, ordenadas y
     * dosificadas a {@code topeVisible} (RB-MA-07/08). El total de la página refleja
     * todas las disponibles; "En espera" = total - mostradas.
     */
    @Transactional(readOnly = true)
    public PageResponse<AccionResponse> obtenerCola(int topeVisible) {
        Long usuario = securityUtils.getCurrentUserId();
        int tope = (topeVisible <= 0 || topeVisible > MAX_TOPE_VISIBLE) ? MAX_TOPE_VISIBLE : topeVisible;
        Pageable pageable = PageRequest.of(0, tope);
        Page<GcAccion> page = accionRepository.findColaPendiente(usuario, LocalDateTime.now(), pageable);
        return PageResponse.from(page, AccionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<EventoResponse> obtenerHistorial(Long accionId) {
        return eventoRepository.findByAccionIdOrderByFechaEventoAsc(accionId).stream()
                .map(EventoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------
    // Transiciones
    // ---------------------------------------------------------

    @Transactional
    public AccionResponse tomar(Long id) {
        GcAccion a = cargarOperable(id);
        if (a.getEstado() != EstadoAccion.PENDIENTE) {
            throw new BusinessException("ESTADO_INVALIDO", "Solo se puede tomar una acción PENDIENTE");
        }
        a.setEstado(EstadoAccion.EN_CURSO);
        a.setModificadoPor(securityUtils.getCurrentUserId());
        accionRepository.save(a);
        registrarEvento(a, TipoEvento.TOMADA, null, null);
        return AccionResponse.fromEntity(a);
    }

    /**
     * Completa una acción con una disposición y aplica su efecto (spec §5).
     * Todo ocurre en una sola transacción: cierre + traza + efecto + materialización.
     */
    @Transactional
    public AccionResponse completar(Long id, Long disposicionId) {
        GcAccion a = cargarOperable(id);
        GcDisposicion disp = disposicionRepository.findById(disposicionId)
                .orElseThrow(() -> new BusinessException("DISPOSICION_NOT_FOUND",
                        "Disposición no encontrada: " + disposicionId));
        if (!Boolean.TRUE.equals(disp.getActivo())) {
            throw new BusinessException("DISPOSICION_INACTIVA",
                    "La disposición " + disp.getCodigo() + " está inactiva");
        }
        validarAplicable(disp, a.getOrigen());

        // 1) Cerrar la acción.
        a.setEstado(EstadoAccion.COMPLETADA);
        a.setDisposicion(disp);
        a.setModificadoPor(securityUtils.getCurrentUserId());
        accionRepository.save(a);

        // 2) Traza inmutable (RB-MA-09).
        trazaPort.registrar(a, disp);

        // 3) Evento de ciclo de vida.
        registrarEvento(a, TipoEvento.COMPLETADA, null, "disposicion=" + disp.getCodigo());

        // 4) Efecto -> 5) Materializar acciones siguientes.
        materializar(dispatchEfecto(a, disp));

        return AccionResponse.fromEntity(a);
    }

    /**
     * Posposición (spec §7). Implementada como PENDIENTE con {@code disponibleDesde}
     * futuro: la acción sale de "Hoy" y reaparece automáticamente al llegar la fecha
     * (sin scheduler), consistente con "Próximas = PENDIENTE + futuro" (§6). El hecho
     * de posponer queda en la bitácora.
     */
    @Transactional
    public AccionResponse posponer(Long id, String motivo, LocalDateTime nuevaFecha) {
        exigirMotivo(motivo, "posponer");
        GcAccion a = cargarOperable(id);
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fecha = (nuevaFecha == null || nuevaFecha.isBefore(ahora)) ? ahora : nuevaFecha;
        a.setEstado(EstadoAccion.PENDIENTE);
        a.setDisponibleDesde(fecha);
        a.setMotivo(motivo);
        a.setModificadoPor(securityUtils.getCurrentUserId());
        accionRepository.save(a);
        registrarEvento(a, TipoEvento.POSPUESTA, motivo, "nuevaFecha=" + fecha);
        return AccionResponse.fromEntity(a);
    }

    @Transactional
    public AccionResponse omitir(Long id, String motivo) {
        exigirMotivo(motivo, "omitir");
        GcAccion a = cargarOperable(id);
        a.setEstado(EstadoAccion.OMITIDA);
        a.setMotivo(motivo);
        a.setModificadoPor(securityUtils.getCurrentUserId());
        accionRepository.save(a);
        registrarEvento(a, TipoEvento.OMITIDA, motivo, null);
        return AccionResponse.fromEntity(a); // OMITIDA no encadena (RB-MA-03)
    }

    /**
     * Reasignación (RB-MA-12). NOTA: la restricción a rol Admin debe cablearse con la
     * API de roles del proyecto (SecurityUtils / @PreAuthorize); aquí no se asume.
     */
    @Transactional
    public AccionResponse reasignar(Long id, Long nuevoResponsableId) {
        GcAccion a = accionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ACCION_NOT_FOUND", "Acción no encontrada: " + id));
        if (esTerminal(a.getEstado())) {
            throw new BusinessException("ESTADO_INVALIDO", "No se puede reasignar una acción terminal");
        }
        Long anterior = a.getResponsableId();
        a.setResponsableId(nuevoResponsableId);
        a.setModificadoPor(securityUtils.getCurrentUserId());
        accionRepository.save(a);
        registrarEvento(a, TipoEvento.REASIGNADA, null, "de=" + anterior + ";a=" + nuevoResponsableId);
        return AccionResponse.fromEntity(a);
    }

    // ---------------------------------------------------------
    // Reconciliación (spec §9)
    // ---------------------------------------------------------

    /**
     * Invalida las acciones no terminales que referencian una entidad que cambió de
     * estado y dejó de admitir trabajo. Lo invoca el dominio dueño de esa entidad
     * (oportunidad cerrada, enrolamiento finalizado, etc.). Devuelve cuántas invalidó.
     */
    @Transactional
    public int invalidarPorReferencia(ReferenciaTipo tipo, Long referenciaId, String motivo) {
        List<GcAccion> acciones = accionRepository
                .findByReferenciaTipoAndReferenciaIdAndEstadoIn(tipo, referenciaId, ESTADOS_NO_TERMINALES);
        Long actor = securityUtils.getCurrentUserId();
        for (GcAccion a : acciones) {
            a.setEstado(EstadoAccion.INVALIDADA);
            a.setMotivo(motivo);
            a.setModificadoPor(actor);
            accionRepository.save(a);
            registrarEvento(a, TipoEvento.INVALIDADA, motivo, null);
        }
        return acciones.size();
    }

    // ---------------------------------------------------------
    // Motor de efectos
    // ---------------------------------------------------------

    private List<SolicitudAccion> dispatchEfecto(GcAccion a, GcDisposicion disp) {
        switch (disp.getEfecto()) {
            case NO_APLICA:
                return List.of();
            case REINTENTAR:
                return List.of(solicitudReintento(a, disp));
            case AVANZAR_PASO:
            case CALIFICAR:
            case DESCARTAR:
            case CUMPLE_REQUISITO:
                ManejadorEfectoAccion manejador = manejadoresPorOrigen.get(a.getOrigen());
                if (manejador == null) {
                    throw new BusinessException("SIN_MANEJADOR",
                            "No hay manejador registrado para el origen " + a.getOrigen()
                                    + " (efecto " + disp.getEfecto() + ")");
                }
                return manejador.alCompletar(a, disp);
            default:
                return List.of();
        }
    }

    private SolicitudAccion solicitudReintento(GcAccion a, GcDisposicion disp) {
        int esperaMin = disp.getEsperaReintentoMin() != null ? disp.getEsperaReintentoMin() : 0;
        return new SolicitudAccion(
                a.getResponsableId(), a.getTipoAccion(), a.getPrioridad(),
                a.getOrigen(), a.getOrigenRefId(),
                a.getReferenciaTipo(), a.getReferenciaId(),
                LocalDateTime.now().plusMinutes(esperaMin),
                a.getVencimiento());
    }

    private void materializar(List<SolicitudAccion> solicitudes) {
        if (solicitudes == null || solicitudes.isEmpty()) {
            return;
        }
        Long actor = securityUtils.getCurrentUserId();
        for (SolicitudAccion s : solicitudes) {
            GcAccion nueva = new GcAccion();
            nueva.setResponsableId(s.responsableId());
            nueva.setTipoAccion(s.tipoAccion());
            nueva.setPrioridad(s.prioridad() != null ? s.prioridad() : Prioridad.MEDIA);
            nueva.setOrigen(s.origen());
            nueva.setOrigenRefId(s.origenRefId());
            nueva.setReferenciaTipo(s.referenciaTipo());
            nueva.setReferenciaId(s.referenciaId());
            nueva.setDisponibleDesde(s.disponibleDesde() != null ? s.disponibleDesde() : LocalDateTime.now());
            nueva.setVencimiento(s.vencimiento());
            nueva.setEstado(EstadoAccion.PENDIENTE);
            nueva.setCreadoPor(actor);
            accionRepository.save(nueva);
            registrarEvento(nueva, TipoEvento.CREADA, null, "origen=" + nueva.getOrigen());
        }
    }

    // ---------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------

    /** Carga una acción operable por el usuario actual (RB-MA-04/13). El override de
     *  Admin sobre acciones de otros se añade al cablear la API de roles. */
    private GcAccion cargarOperable(Long id) {
        GcAccion a = accionRepository.findById(id)
                .orElseThrow(() -> new BusinessException("ACCION_NOT_FOUND", "Acción no encontrada: " + id));
        if (esTerminal(a.getEstado())) {
            throw new BusinessException("ESTADO_INVALIDO",
                    "La acción está en estado terminal: " + a.getEstado());
        }
        Long actual = securityUtils.getCurrentUserId();
        if (!actual.equals(a.getResponsableId())) {
            throw new BusinessException("NO_AUTORIZADO", "La acción no pertenece al usuario actual");
        }
        return a;
    }

    private void validarAplicable(GcDisposicion disp, Origen origen) {
        OrigenAplicable oa = disp.getOrigenAplicable();
        boolean aplica = oa == OrigenAplicable.TODOS || oa.name().equals(origen.name());
        if (!aplica) {
            throw new BusinessException("DISPOSICION_NO_APLICABLE",
                    "La disposición " + disp.getCodigo() + " no aplica al origen " + origen);
        }
    }

    private void registrarEvento(GcAccion a, TipoEvento tipo, String motivo, String datos) {
        GcAccionEvento ev = new GcAccionEvento();
        ev.setAccion(a);
        ev.setTipoEvento(tipo);
        ev.setActorId(securityUtils.getCurrentUserId());
        ev.setMotivo(motivo);
        ev.setDatos(datos);
        eventoRepository.save(ev);
    }

    private void exigirMotivo(String motivo, String accion) {
        if (motivo == null || motivo.isBlank()) {
            throw new BusinessException("MOTIVO_REQUERIDO", "El motivo es obligatorio para " + accion);
        }
    }

    private boolean esTerminal(EstadoAccion e) {
        return e == EstadoAccion.COMPLETADA || e == EstadoAccion.OMITIDA || e == EstadoAccion.INVALIDADA;
    }
}
