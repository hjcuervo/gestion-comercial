package com.arquitecsoft.gestion.domain.facturacion.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Evento del sistema sobre un compromiso de ingreso.
 *
 * Bitácora INMUTABLE que construye la historia del compromiso. Según la spec:
 *   "El sistema no cambia por edición, cambia por eventos.
 *    Los eventos construyen la historia del compromiso."
 *
 * Toda transición de estado, cambio de monto o de fecha debe pasar por un
 * evento. No se permite modificación directa del compromiso sin que se
 * registre el evento correspondiente.
 *
 * IMPORTANTE: Esta tabla NO debe confundirse con GcCompromisoGestion, que
 * captura notas libres del usuario (llamadas, correos, propuestas
 * comerciales). Los eventos aquí son los disparadores de la máquina de
 * estados definidos en spec §4.
 *
 * El campo `usuarioId` registra quién ejecutó la acción que originó el evento
 * (auditoría), NO un responsable asignado al compromiso — en esta versión
 * no se modela responsable dedicado. Sigue la convención del proyecto de
 * usar Long crudo en vez de @ManyToOne a entidad Usuario.
 *
 * Una vez creados, los eventos NO se actualizan ni se eliminan.
 */
@Entity
@Table(name = "GC_COMPROMISO_EVENTO")
public class GcCompromisoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compromiso_id", nullable = false, updatable = false)
    private GcCompromisoIngreso compromiso;

    @Column(name = "tipo_evento", nullable = false, length = 30, updatable = false)
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    /**
     * Estado del compromiso ANTES de este evento (null para CompromisoCreado).
     */
    @Column(name = "estado_anterior", length = 25, updatable = false)
    @Enumerated(EnumType.STRING)
    private GcCompromisoIngreso.EstadoCompromiso estadoAnterior;

    /**
     * Estado del compromiso DESPUÉS de este evento.
     * Puede ser null para eventos que no cambian estado (ej. GestionActualizada,
     * InactividadDetectada).
     */
    @Column(name = "estado_nuevo", length = 25, updatable = false)
    @Enumerated(EnumType.STRING)
    private GcCompromisoIngreso.EstadoCompromiso estadoNuevo;

    // ===== Payload estructurado — nullable según tipo de evento =====

    @Column(name = "monto_anterior", precision = 16, scale = 2, updatable = false)
    private BigDecimal montoAnterior;

    @Column(name = "monto_nuevo", precision = 16, scale = 2, updatable = false)
    private BigDecimal montoNuevo;

    @Column(name = "fecha_anterior", updatable = false)
    private LocalDate fechaAnterior;

    @Column(name = "fecha_nueva", updatable = false)
    private LocalDate fechaNueva;

    /**
     * Referencia a la aplicación de factura (para FacturaRegistrada / FacturaRevertida).
     * No es FK con constraint para permitir que la aplicación también sea auditable
     * sin dependencia circular; el servicio valida la integridad.
     */
    @Column(name = "compromiso_factura_id", updatable = false)
    private Long compromisoFacturaId;

    /**
     * Motivo / justificación. OBLIGATORIO para:
     *  - FechaReprogramada
     *  - MontoAjustado
     *  - CompromisoPerdido
     *  - FacturaRevertida
     * Validación a nivel de servicio.
     */
    @Column(name = "motivo", length = 1000, updatable = false)
    private String motivo;

    /**
     * Payload adicional en JSON para casos extremos (ej. datos del compromiso
     * original en un evento de reemplazo). No estructurado a propósito.
     */
    @Column(name = "payload_json", length = 2000, updatable = false)
    private String payloadJson;

    /**
     * Usuario que ejecutó la acción que disparó este evento (auditoría).
     * Puede ser null para eventos automáticos del sistema (InactividadDetectada,
     * DesviacionDetectada generados por schedulers).
     */
    @Column(name = "usuario_id", updatable = false)
    private Long usuarioId;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    /**
     * Tipos de eventos según spec §4.
     * Agrupados funcionalmente:
     *  - Creación:          COMPROMISO_CREADO
     *  - Gestión comercial: GESTION_INICIADA, GESTION_ACTUALIZADA, COMPROMISO_CONFIRMADO
     *  - Ejecución:         FACTURA_REGISTRADA, FACTURA_REVERTIDA
     *  - Desviación:        FECHA_REPROGRAMADA, MONTO_AJUSTADO
     *  - Cierre:            COMPROMISO_PERDIDO, COMPROMISO_REACTIVADO
     *  - Automáticos:       INACTIVIDAD_DETECTADA, DESVIACION_DETECTADA
     */
    public enum TipoEvento {
        COMPROMISO_CREADO,
        GESTION_INICIADA,
        GESTION_ACTUALIZADA,
        COMPROMISO_CONFIRMADO,
        FACTURA_REGISTRADA,
        FACTURA_REVERTIDA,
        FECHA_REPROGRAMADA,
        MONTO_AJUSTADO,
        COMPROMISO_PERDIDO,
        COMPROMISO_REACTIVADO,
        INACTIVIDAD_DETECTADA,
        DESVIACION_DETECTADA
    }

    public GcCompromisoEvento() {}

    @PrePersist
    protected void onCreate() {
        if (this.fechaEvento == null) {
            this.fechaEvento = LocalDateTime.now();
        }
    }

    // ========== Getters y Setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GcCompromisoIngreso getCompromiso() { return compromiso; }
    public void setCompromiso(GcCompromisoIngreso compromiso) { this.compromiso = compromiso; }

    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }

    public GcCompromisoIngreso.EstadoCompromiso getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(GcCompromisoIngreso.EstadoCompromiso estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public GcCompromisoIngreso.EstadoCompromiso getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(GcCompromisoIngreso.EstadoCompromiso estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public BigDecimal getMontoAnterior() { return montoAnterior; }
    public void setMontoAnterior(BigDecimal montoAnterior) { this.montoAnterior = montoAnterior; }

    public BigDecimal getMontoNuevo() { return montoNuevo; }
    public void setMontoNuevo(BigDecimal montoNuevo) { this.montoNuevo = montoNuevo; }

    public LocalDate getFechaAnterior() { return fechaAnterior; }
    public void setFechaAnterior(LocalDate fechaAnterior) { this.fechaAnterior = fechaAnterior; }

    public LocalDate getFechaNueva() { return fechaNueva; }
    public void setFechaNueva(LocalDate fechaNueva) { this.fechaNueva = fechaNueva; }

    public Long getCompromisoFacturaId() { return compromisoFacturaId; }
    public void setCompromisoFacturaId(Long compromisoFacturaId) { this.compromisoFacturaId = compromisoFacturaId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }
}
