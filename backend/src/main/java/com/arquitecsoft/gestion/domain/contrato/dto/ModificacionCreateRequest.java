package com.arquitecsoft.gestion.domain.contrato.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ModificacionCreateRequest {

    @NotNull(message = "El tipo de modificación es requerido")
    @Size(max = 20)
    private String tipoModificacion;

    private Boolean modificaTiempo;
    private Boolean modificaValor;
    private BigDecimal valorModificacion;
    private LocalDate nuevaFechaFin;

    @NotNull(message = "La fecha de modificación es requerida")
    private LocalDate fechaModificacion;

    @Size(max = 1000)
    private String observaciones;

    public ModificacionCreateRequest() {}

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
}
