package com.arquitecsoft.gestion.domain.accion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OmitirAccionRequest {

    @NotBlank(message = "El motivo es requerido")
    @Size(max = 500)
    private String motivo;

    public OmitirAccionRequest() {}

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
