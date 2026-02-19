package com.arquitecsoft.gestion.domain.oportunidad.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_OPORTUNIDAD")
public class GcOportunidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private GcEmpresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pipeline_id", nullable = false)
    private GcPipeline pipeline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etapa_id", nullable = false)
    private GcEtapa etapa;

    @Column(name = "estado_macro", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoMacro estadoMacro = EstadoMacro.ABIERTA;

    @Column(name = "valor_estimado", precision = 15, scale = 2)
    private BigDecimal valorEstimado;

    @Column(name = "moneda", length = 3)
    private String moneda = "COP";

    @Column(name = "probabilidad")
    private Integer probabilidad;

    @Column(name = "fecha_estimada_cierre")
    private LocalDate fechaEstimadaCierre;

    @Column(name = "fuente", length = 100)
    private String fuente;

    @Column(name = "tipo_servicio", length = 100)
    private String tipoServicio;

    @Column(name = "motivo_cierre_id")
    private Long motivoCierreId;

    @Column(name = "comentario_cierre", length = 500)
    private String comentarioCierre;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum EstadoMacro {
        ABIERTA, SEGUIMIENTO, GANADA, PERDIDA, NO_CONCRETADA
    }

    public enum Moneda {
        COP, USD, EUR
    }

    // Constructors
    public GcOportunidad() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estadoMacro == null) {
            this.estadoMacro = EstadoMacro.ABIERTA;
        }
        if (this.moneda == null) {
            this.moneda = "COP";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // Helper methods
    public boolean isAbierta() {
        return this.estadoMacro == EstadoMacro.ABIERTA || this.estadoMacro == EstadoMacro.SEGUIMIENTO;
    }

    public boolean isCerrada() {
        return this.estadoMacro == EstadoMacro.GANADA || 
               this.estadoMacro == EstadoMacro.PERDIDA || 
               this.estadoMacro == EstadoMacro.NO_CONCRETADA;
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

    public GcEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(GcEmpresa empresa) {
        this.empresa = empresa;
    }

    public GcPipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(GcPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public GcEtapa getEtapa() {
        return etapa;
    }

    public void setEtapa(GcEtapa etapa) {
        this.etapa = etapa;
    }

    public EstadoMacro getEstadoMacro() {
        return estadoMacro;
    }

    public void setEstadoMacro(EstadoMacro estadoMacro) {
        this.estadoMacro = estadoMacro;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Integer getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(Integer probabilidad) {
        this.probabilidad = probabilidad;
    }

    public LocalDate getFechaEstimadaCierre() {
        return fechaEstimadaCierre;
    }

    public void setFechaEstimadaCierre(LocalDate fechaEstimadaCierre) {
        this.fechaEstimadaCierre = fechaEstimadaCierre;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public Long getMotivoCierreId() {
        return motivoCierreId;
    }

    public void setMotivoCierreId(Long motivoCierreId) {
        this.motivoCierreId = motivoCierreId;
    }

    public String getComentarioCierre() {
        return comentarioCierre;
    }

    public void setComentarioCierre(String comentarioCierre) {
        this.comentarioCierre = comentarioCierre;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
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
}
