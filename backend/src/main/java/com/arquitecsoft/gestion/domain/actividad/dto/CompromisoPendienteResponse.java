package com.arquitecsoft.gestion.domain.actividad.dto;

import java.time.LocalDate;

public class CompromisoPendienteResponse {

    private Long id;
    private String descripcion;
    private LocalDate fechaCompromiso;
    private String estado;
    private Long actividadId;
    private Long oportunidadId;
    private String oportunidadNombre;

    public CompromisoPendienteResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaCompromiso() { return fechaCompromiso; }
    public void setFechaCompromiso(LocalDate fechaCompromiso) { this.fechaCompromiso = fechaCompromiso; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Long getActividadId() { return actividadId; }
    public void setActividadId(Long actividadId) { this.actividadId = actividadId; }

    public Long getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Long oportunidadId) { this.oportunidadId = oportunidadId; }

    public String getOportunidadNombre() { return oportunidadNombre; }
    public void setOportunidadNombre(String oportunidadNombre) { this.oportunidadNombre = oportunidadNombre; }
}
