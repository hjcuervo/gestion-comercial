package com.arquitecsoft.gestion.domain.pipeline.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_ETAPA")
public class GcEtapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    private GcPipeline pipeline;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @Column(name = "probabilidad_sugerida")
    private Integer probabilidadSugerida;

    @Column(name = "color", length = 7)
    private String color;

    @Column(name = "modo_bloqueo", nullable = false)
    private Integer modoBloqueo = 0;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoEtapa estado = EstadoEtapa.ACTIVA;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum EstadoEtapa {
        ACTIVA, INACTIVA
    }

    // Constructors
    public GcEtapa() {
    }

    public GcEtapa(GcPipeline pipeline, String nombre, Integer orden) {
        this.pipeline = pipeline;
        this.nombre = nombre;
        this.orden = orden;
        this.estado = EstadoEtapa.ACTIVA;
        this.modoBloqueo = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // Helper methods
    public boolean isActiva() {
        return this.estado == EstadoEtapa.ACTIVA;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GcPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(GcPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getProbabilidadSugerida() {
        return probabilidadSugerida;
    }

    public void setProbabilidadSugerida(Integer probabilidadSugerida) {
        this.probabilidadSugerida = probabilidadSugerida;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getModoBloqueo() {
        return modoBloqueo;
    }

    public void setModoBloqueo(Integer modoBloqueo) {
        this.modoBloqueo = modoBloqueo;
    }

    public EstadoEtapa getEstado() {
        return estado;
    }

    public void setEstado(EstadoEtapa estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }
}
