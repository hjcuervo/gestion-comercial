package com.arquitecsoft.gestion.domain.oportunidad.dto;

import jakarta.validation.constraints.NotNull;

public class MoverEtapaRequest {

    @NotNull(message = "La etapa destino es requerida")
    private Long etapaId;

    private String comentario;

    public MoverEtapaRequest() {
    }

    public Long getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(Long etapaId) {
        this.etapaId = etapaId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
