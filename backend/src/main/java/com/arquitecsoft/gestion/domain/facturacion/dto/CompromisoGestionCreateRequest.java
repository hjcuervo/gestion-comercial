package com.arquitecsoft.gestion.domain.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class CompromisoGestionCreateRequest {

    @NotBlank(message = "El tipo de gestión es obligatorio")
    private String tipoGestion;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de gestión es obligatoria")
    private LocalDate fechaGestion;

    public String getTipoGestion() { return tipoGestion; }
    public void setTipoGestion(String tipoGestion) { this.tipoGestion = tipoGestion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFechaGestion() { return fechaGestion; }
    public void setFechaGestion(LocalDate fechaGestion) { this.fechaGestion = fechaGestion; }
}
