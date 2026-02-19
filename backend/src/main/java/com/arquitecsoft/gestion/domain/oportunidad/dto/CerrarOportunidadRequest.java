package com.arquitecsoft.gestion.domain.oportunidad.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CerrarOportunidadRequest {

    @NotNull(message = "El estado de cierre es requerido")
    private String estadoMacro; // GANADA, PERDIDA, NO_CONCRETADA

    private Long motivoCierreId; // Requerido para PERDIDA y NO_CONCRETADA

    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentario;

    public CerrarOportunidadRequest() {
    }

    public String getEstadoMacro() {
        return estadoMacro;
    }

    public void setEstadoMacro(String estadoMacro) {
        this.estadoMacro = estadoMacro;
    }

    public Long getMotivoCierreId() {
        return motivoCierreId;
    }

    public void setMotivoCierreId(Long motivoCierreId) {
        this.motivoCierreId = motivoCierreId;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
