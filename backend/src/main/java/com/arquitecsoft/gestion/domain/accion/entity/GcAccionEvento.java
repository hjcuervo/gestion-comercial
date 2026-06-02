package com.arquitecsoft.gestion.domain.accion.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Bitácora append-only del ciclo de vida de una acción (RB-MA-15). No se modifica
 * tras su creación; la auditoría se reduce a {@code actorId} + {@code fechaEvento}.
 * Sigue el patrón del GcCompromisoEvento del Mundo 3.
 */
@Entity
@Table(name = "GC_ACCION_EVENTO")
public class GcAccionEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accion_id", nullable = false)
    private GcAccion accion;

    @Column(name = "tipo_evento", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Column(name = "motivo", length = 500)
    private String motivo;

    // JSON libre: disposición aplicada, nuevo responsable en reasignación, etc.
    @Column(name = "datos", length = 1000)
    private String datos;

    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    public enum TipoEvento { CREADA, TOMADA, COMPLETADA, POSPUESTA, OMITIDA, INVALIDADA, REASIGNADA }

    public GcAccionEvento() {}

    @PrePersist
    protected void onCreate() {
        if (this.fechaEvento == null) {
            this.fechaEvento = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GcAccion getAccion() { return accion; }
    public void setAccion(GcAccion accion) { this.accion = accion; }

    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDatos() { return datos; }
    public void setDatos(String datos) { this.datos = datos; }

    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }
}
