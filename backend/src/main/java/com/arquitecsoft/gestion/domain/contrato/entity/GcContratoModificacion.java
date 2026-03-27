package com.arquitecsoft.gestion.domain.contrato.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_CONTRATO_MODIFICACION")
public class GcContratoModificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private GcContrato contrato;

    @Column(name = "tipo_modificacion", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoModificacion tipoModificacion;

    @Column(name = "modifica_tiempo")
    private Boolean modificaTiempo = false;

    @Column(name = "modifica_valor")
    private Boolean modificaValor = false;

    @Column(name = "valor_modificacion", precision = 16, scale = 2)
    private BigDecimal valorModificacion;

    @Column(name = "nueva_fecha_fin")
    private LocalDate nuevaFechaFin;

    @Column(name = "fecha_modificacion", nullable = false)
    private LocalDate fechaModificacionContrato;

    @Column(name = "observaciones", length = 1000)
    private String observaciones;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public enum TipoModificacion {
        ADICION, PRORROGA, SUSPENSION, REINICIO, OTRO
    }

    public GcContratoModificacion() {}

    @PrePersist
    protected void onCreate() { this.fechaCreacion = LocalDateTime.now(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GcContrato getContrato() { return contrato; }
    public void setContrato(GcContrato contrato) { this.contrato = contrato; }
    public TipoModificacion getTipoModificacion() { return tipoModificacion; }
    public void setTipoModificacion(TipoModificacion tipoModificacion) { this.tipoModificacion = tipoModificacion; }
    public Boolean getModificaTiempo() { return modificaTiempo; }
    public void setModificaTiempo(Boolean modificaTiempo) { this.modificaTiempo = modificaTiempo; }
    public Boolean getModificaValor() { return modificaValor; }
    public void setModificaValor(Boolean modificaValor) { this.modificaValor = modificaValor; }
    public BigDecimal getValorModificacion() { return valorModificacion; }
    public void setValorModificacion(BigDecimal valorModificacion) { this.valorModificacion = valorModificacion; }
    public LocalDate getNuevaFechaFin() { return nuevaFechaFin; }
    public void setNuevaFechaFin(LocalDate nuevaFechaFin) { this.nuevaFechaFin = nuevaFechaFin; }
    public LocalDate getFechaModificacionContrato() { return fechaModificacionContrato; }
    public void setFechaModificacionContrato(LocalDate fechaModificacionContrato) { this.fechaModificacionContrato = fechaModificacionContrato; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
