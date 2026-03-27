package com.arquitecsoft.gestion.domain.contrato.dto;

import com.arquitecsoft.gestion.domain.contrato.entity.GcProcesoContratacion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProcesoContratacionResponse {

    private Long id;
    private Long oportunidadId;
    private String oportunidadNombre;
    private Long empresaId;
    private String empresaNombre;
    private Long pipelineId;
    private String pipelineNombre;
    private Long etapaId;
    private String etapaNombre;
    private String etapaColor;
    private Integer etapaOrden;
    private String estado;
    private LocalDate fechaInicio;
    private LocalDate fechaCompletado;
    private String responsable;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    public ProcesoContratacionResponse() {}

    public static ProcesoContratacionResponse fromEntity(GcProcesoContratacion p) {
        ProcesoContratacionResponse r = new ProcesoContratacionResponse();
        r.setId(p.getId());
        r.setOportunidadId(p.getOportunidad().getId());
        r.setOportunidadNombre(p.getOportunidad().getNombre());
        r.setEmpresaId(p.getEmpresa().getId());
        r.setEmpresaNombre(p.getEmpresa().getRazonSocial());
        r.setPipelineId(p.getPipeline().getId());
        r.setPipelineNombre(p.getPipeline().getNombre());
        r.setEtapaId(p.getEtapa().getId());
        r.setEtapaNombre(p.getEtapa().getNombre());
        r.setEtapaColor(p.getEtapa().getColor());
        r.setEtapaOrden(p.getEtapa().getOrden());
        r.setEstado(p.getEstado().name());
        r.setFechaInicio(p.getFechaInicio());
        r.setFechaCompletado(p.getFechaCompletado());
        r.setResponsable(p.getResponsable());
        r.setObservaciones(p.getObservaciones());
        r.setFechaCreacion(p.getFechaCreacion());
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Long oportunidadId) { this.oportunidadId = oportunidadId; }
    public String getOportunidadNombre() { return oportunidadNombre; }
    public void setOportunidadNombre(String oportunidadNombre) { this.oportunidadNombre = oportunidadNombre; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public Long getPipelineId() { return pipelineId; }
    public void setPipelineId(Long pipelineId) { this.pipelineId = pipelineId; }
    public String getPipelineNombre() { return pipelineNombre; }
    public void setPipelineNombre(String pipelineNombre) { this.pipelineNombre = pipelineNombre; }
    public Long getEtapaId() { return etapaId; }
    public void setEtapaId(Long etapaId) { this.etapaId = etapaId; }
    public String getEtapaNombre() { return etapaNombre; }
    public void setEtapaNombre(String etapaNombre) { this.etapaNombre = etapaNombre; }
    public String getEtapaColor() { return etapaColor; }
    public void setEtapaColor(String etapaColor) { this.etapaColor = etapaColor; }
    public Integer getEtapaOrden() { return etapaOrden; }
    public void setEtapaOrden(Integer etapaOrden) { this.etapaOrden = etapaOrden; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaCompletado() { return fechaCompletado; }
    public void setFechaCompletado(LocalDate fechaCompletado) { this.fechaCompletado = fechaCompletado; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
