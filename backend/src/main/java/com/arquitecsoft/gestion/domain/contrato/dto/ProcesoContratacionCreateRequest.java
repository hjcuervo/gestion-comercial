package com.arquitecsoft.gestion.domain.contrato.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProcesoContratacionCreateRequest {

    @NotNull(message = "La oportunidad es requerida")
    private Long oportunidadId;

    @NotNull(message = "El pipeline de contratación es requerido")
    private Long pipelineId;

    @Size(max = 100, message = "El responsable no puede exceder 100 caracteres")
    private String responsable;

    @Size(max = 2000, message = "Las observaciones no pueden exceder 2000 caracteres")
    private String observaciones;

    public ProcesoContratacionCreateRequest() {}

    public Long getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Long oportunidadId) { this.oportunidadId = oportunidadId; }
    public Long getPipelineId() { return pipelineId; }
    public void setPipelineId(Long pipelineId) { this.pipelineId = pipelineId; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
