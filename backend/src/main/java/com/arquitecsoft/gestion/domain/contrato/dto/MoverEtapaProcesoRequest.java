package com.arquitecsoft.gestion.domain.contrato.dto;

import jakarta.validation.constraints.NotNull;

public class MoverEtapaProcesoRequest {

    @NotNull(message = "La etapa destino es requerida")
    private Long etapaId;

    public MoverEtapaProcesoRequest() {}

    public Long getEtapaId() { return etapaId; }
    public void setEtapaId(Long etapaId) { this.etapaId = etapaId; }
}
