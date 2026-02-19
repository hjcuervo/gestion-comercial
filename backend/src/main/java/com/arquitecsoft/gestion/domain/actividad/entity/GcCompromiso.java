package com.arquitecsoft.gestion.domain.actividad.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_COMPROMISO")
public class GcCompromiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id", nullable = false)
    private GcActividad actividad;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "fecha_compromiso", nullable = false)
    private LocalDate fechaCompromiso;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoCompromiso estado = EstadoCompromiso.PENDIENTE;

    @Column(name = "fecha_completado")
    private LocalDateTime fechaCompletado;

    @Column(name = "notas_cierre", length = 500)
    private String notasCierre;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum EstadoCompromiso {
        PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO
    }

    // Constructors
    public GcCompromiso() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoCompromiso.PENDIENTE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // Helper methods
    public boolean isPendiente() {
        return this.estado == EstadoCompromiso.PENDIENTE || this.estado == EstadoCompromiso.EN_PROGRESO;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GcActividad getActividad() {
        return actividad;
    }

    public void setActividad(GcActividad actividad) {
        this.actividad = actividad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(LocalDate fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    public EstadoCompromiso getEstado() {
        return estado;
    }

    public void setEstado(EstadoCompromiso estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }

    public String getNotasCierre() {
        return notasCierre;
    }

    public void setNotasCierre(String notasCierre) {
        this.notasCierre = notasCierre;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
}
