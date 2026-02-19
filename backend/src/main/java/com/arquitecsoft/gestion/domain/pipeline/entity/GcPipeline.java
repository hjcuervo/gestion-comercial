package com.arquitecsoft.gestion.domain.pipeline.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GC_PIPELINE")
public class GcPipeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "ambito", nullable = false, length = 50)
    private String ambito = "GESTION_COMERCIAL";

    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoPipeline estado = EstadoPipeline.ACTIVO;

    @Column(name = "es_default", nullable = false)
    private Integer esDefault = 0;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "pipeline", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<GcEtapa> etapas = new ArrayList<>();

    public enum EstadoPipeline {
        ACTIVO, INACTIVO
    }

    // Constructors
    public GcPipeline() {
    }

    public GcPipeline(String nombre) {
        this.nombre = nombre;
        this.ambito = "GESTION_COMERCIAL";
        this.version = 1;
        this.estado = EstadoPipeline.ACTIVO;
        this.esDefault = 0;
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
    public boolean isActivo() {
        return this.estado == EstadoPipeline.ACTIVO;
    }

    public boolean isDefault() {
        return this.esDefault != null && this.esDefault == 1;
    }

    public void addEtapa(GcEtapa etapa) {
        etapas.add(etapa);
        etapa.setPipeline(this);
    }

    public void removeEtapa(GcEtapa etapa) {
        etapas.remove(etapa);
        etapa.setPipeline(null);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public EstadoPipeline getEstado() {
        return estado;
    }

    public void setEstado(EstadoPipeline estado) {
        this.estado = estado;
    }

    public Integer getEsDefault() {
        return esDefault;
    }

    public void setEsDefault(Integer esDefault) {
        this.esDefault = esDefault;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Long getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(Long modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public List<GcEtapa> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<GcEtapa> etapas) {
        this.etapas = etapas;
    }
}
