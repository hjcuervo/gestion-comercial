package com.arquitecsoft.gestion.domain.actividad.dto;

import com.arquitecsoft.gestion.domain.actividad.entity.GcCompromiso;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompromisoResponse {

    private Long id;
    private Long actividadId;
    private String descripcion;
    private LocalDate fechaCompromiso;
    private String estado;
    private LocalDateTime fechaCompletado;
    private String notasCierre;
    private LocalDateTime fechaCreacion;

    public CompromisoResponse() {
    }

    public static CompromisoResponse fromEntity(GcCompromiso compromiso) {
        CompromisoResponse response = new CompromisoResponse();
        response.setId(compromiso.getId());
        response.setActividadId(compromiso.getActividad().getId());
        response.setDescripcion(compromiso.getDescripcion());
        response.setFechaCompromiso(compromiso.getFechaCompromiso());
        response.setEstado(compromiso.getEstado().name());
        response.setFechaCompletado(compromiso.getFechaCompletado());
        response.setNotasCierre(compromiso.getNotasCierre());
        response.setFechaCreacion(compromiso.getFechaCreacion());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaCompromiso() {
        return fechaCompromiso;
    }

    public void setFechaCompromiso(LocalDate fechaCompromiso) {
        this.fechaCompromiso = fechaCompromiso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }

    public String getNotasCierre() {
        return notasCierre;
    }

    public void setNotasCierre(String notasCierre) {
        this.notasCierre = notasCierre;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
