package com.arquitecsoft.gestion.domain.accion.dto;

import jakarta.validation.constraints.NotNull;

public class CompletarAccionRequest {

    @NotNull(message = "La disposición es requerida")
    private Long disposicionId;

    public CompletarAccionRequest() {}

    public Long getDisposicionId() { return disposicionId; }
    public void setDisposicionId(Long disposicionId) { this.disposicionId = disposicionId; }
}
