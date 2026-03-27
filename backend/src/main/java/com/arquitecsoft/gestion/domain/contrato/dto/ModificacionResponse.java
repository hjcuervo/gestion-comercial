package com.arquitecsoft.gestion.domain.contrato.dto;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoModificacion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ModificacionResponse {

    private Long id;
    private Long contratoId;
    private String tipoModificacion;
    private Boolean modificaTiempo;
    private Boolean modificaValor;
    private BigDecimal valorModificacion;
    private LocalDate nuevaFechaFin;
    private LocalDate fechaModificacion;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    public ModificacionResponse() {}

    public static ModificacionResponse fromEntity(GcContratoModificacion m) {
        ModificacionResponse r = new ModificacionResponse();
        r.setId(m.getId());
        r.setContratoId(m.getContrato().getId());
        r.setTipoModificacion(m.getTipoModificacion().name());
        r.setModificaTiempo(m.getModificaTiempo());
        r.setModificaValor(m.getModificaValor());
        r.setValorModificacion(m.getValorModificacion());
        r.setNuevaFechaFin(m.getNuevaFechaFin());
        r.setFechaModificacion(m.getFechaModificacionContrato());
        r.setObservaciones(m.getObservaciones());
        r.setFechaCreacion(m.getFechaCreacion());
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }
    public String getTipoModificacion() { return tipoModificacion; }
    public void setTipoModificacion(String tipoModificacion) { this.tipoModificacion = tipoModificacion; }
    public Boolean getModificaTiempo() { return modificaTiempo; }
    public void setModificaTiempo(Boolean modificaTiempo) { this.modificaTiempo = modificaTiempo; }
    public Boolean getModificaValor() { return modificaValor; }
    public void setModificaValor(Boolean modificaValor) { this.modificaValor = modificaValor; }
    public BigDecimal getValorModificacion() { return valorModificacion; }
    public void setValorModificacion(BigDecimal valorModificacion) { this.valorModificacion = valorModificacion; }
    public LocalDate getNuevaFechaFin() { return nuevaFechaFin; }
    public void setNuevaFechaFin(LocalDate nuevaFechaFin) { this.nuevaFechaFin = nuevaFechaFin; }
    public LocalDate getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDate fechaModificacion) { this.fechaModificacion = fechaModificacion; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
