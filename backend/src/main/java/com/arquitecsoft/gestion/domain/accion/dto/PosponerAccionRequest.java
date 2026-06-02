package com.arquitecsoft.gestion.domain.accion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class PosponerAccionRequest {

    @NotBlank(message = "El motivo es requerido")
    @Size(max = 500)
    private String motivo;

    // Opcional: si es null o pasada, el service la normaliza a "ahora".
    private LocalDateTime nuevaFecha;

    public PosponerAccionRequest() {}

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getNuevaFecha() { return nuevaFecha; }
    public void setNuevaFecha(LocalDateTime nuevaFecha) { this.nuevaFecha = nuevaFecha; }
}
