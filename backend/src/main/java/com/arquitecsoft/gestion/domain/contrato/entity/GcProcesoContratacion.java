package com.arquitecsoft.gestion.domain.contrato.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_PROCESO_CONTRATACION")
public class GcProcesoContratacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id", nullable = false)
    private GcOportunidad oportunidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private GcEmpresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    private GcPipeline pipeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_id", nullable = false)
    private GcEtapa etapa;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoProceso estado = EstadoProceso.EN_CURSO;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_completado")
    private LocalDate fechaCompletado;

    @Column(name = "responsable", length = 100)
    private String responsable;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum EstadoProceso {
        EN_CURSO, COMPLETADO, CANCELADO
    }

    public GcProcesoContratacion() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.fechaInicio == null) this.fechaInicio = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public boolean isEnCurso() { return this.estado == EstadoProceso.EN_CURSO; }
    public boolean isCompletado() { return this.estado == EstadoProceso.COMPLETADO; }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GcOportunidad getOportunidad() { return oportunidad; }
    public void setOportunidad(GcOportunidad oportunidad) { this.oportunidad = oportunidad; }

    public GcEmpresa getEmpresa() { return empresa; }
    public void setEmpresa(GcEmpresa empresa) { this.empresa = empresa; }

    public GcPipeline getPipeline() { return pipeline; }
    public void setPipeline(GcPipeline pipeline) { this.pipeline = pipeline; }

    public GcEtapa getEtapa() { return etapa; }
    public void setEtapa(GcEtapa etapa) { this.etapa = etapa; }

    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { this.estado = estado; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaCompletado() { return fechaCompletado; }
    public void setFechaCompletado(LocalDate fechaCompletado) { this.fechaCompletado = fechaCompletado; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
