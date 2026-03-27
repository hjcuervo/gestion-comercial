package com.arquitecsoft.gestion.domain.oportunidad.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CerrarOportunidadRequest {

    @NotBlank(message = "El estado de cierre es requerido")
    private String estadoMacro;

    private Long motivoCierreId;

    @Size(max = 500, message = "El comentario no puede exceder 500 caracteres")
    private String comentario;

    /** Pipeline de contratación al que se moverá la oportunidad cuando se marca como GANADA. */
    private Long pipelineContratacionId;

    public CerrarOportunidadRequest() {}

    public String getEstadoMacro() { return estadoMacro; }
    public void setEstadoMacro(String estadoMacro) { this.estadoMacro = estadoMacro; }
    public Long getMotivoCierreId() { return motivoCierreId; }
    public void setMotivoCierreId(Long motivoCierreId) { this.motivoCierreId = motivoCierreId; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Long getPipelineContratacionId() { return pipelineContratacionId; }
    public void setPipelineContratacionId(Long pipelineContratacionId) { this.pipelineContratacionId = pipelineContratacionId; }
}
