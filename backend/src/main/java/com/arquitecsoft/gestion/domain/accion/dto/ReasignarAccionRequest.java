package com.arquitecsoft.gestion.domain.accion.dto;

import jakarta.validation.constraints.NotNull;

public class ReasignarAccionRequest {

    @NotNull(message = "El nuevo responsable es requerido")
    private Long nuevoResponsableId;

    public ReasignarAccionRequest() {}

    public Long getNuevoResponsableId() { return nuevoResponsableId; }
    public void setNuevoResponsableId(Long nuevoResponsableId) { this.nuevoResponsableId = nuevoResponsableId; }
}
