package com.arquitecsoft.gestion.domain.actividad.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class CompromisoUpdateRequest {

    @Size(max = 500, message = "La descripcion no puede exceder 500 caracteres")
    private String descripcion;

    private LocalDate fechaCompromiso;

    private String estado; // PENDIENTE, EN_PROGRESO, COMPLETADO, CANCELADO

    @Size(max = 500, message = "Las notas de cierre no pueden exceder 500 caracteres")
    private String notasCierre;

    public CompromisoUpdateRequest() {
    }

    // Getters and Setters
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotasCierre() {
        return notasCierre;
    }

    public void setNotasCierre(String notasCierre) {
        this.notasCierre = notasCierre;
    }
}
