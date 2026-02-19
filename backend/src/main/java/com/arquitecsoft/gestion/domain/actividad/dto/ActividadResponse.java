package com.arquitecsoft.gestion.domain.actividad.dto;

import com.arquitecsoft.gestion.domain.actividad.entity.GcActividad;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ActividadResponse {

    private Long id;
    private Long oportunidadId;
    private String oportunidadNombre;
    private Long tipoActividadId;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private String notas;
    private LocalDateTime fechaCreacion;
    private List<CompromisoResponse> compromisos;

    public ActividadResponse() {
    }

    public static ActividadResponse fromEntity(GcActividad actividad) {
        ActividadResponse response = new ActividadResponse();
        response.setId(actividad.getId());
        response.setOportunidadId(actividad.getOportunidad().getId());
        response.setOportunidadNombre(actividad.getOportunidad().getNombre());
        response.setTipoActividadId(actividad.getTipoActividadId());
        response.setFechaHora(actividad.getFechaHora());
        response.setDuracionMinutos(actividad.getDuracionMinutos());
        response.setNotas(actividad.getNotas());
        response.setFechaCreacion(actividad.getFechaCreacion());

        if (actividad.getCompromisos() != null && !actividad.getCompromisos().isEmpty()) {
            response.setCompromisos(actividad.getCompromisos().stream()
                    .map(CompromisoResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public static ActividadResponse fromEntitySimple(GcActividad actividad) {
        ActividadResponse response = new ActividadResponse();
        response.setId(actividad.getId());
        response.setOportunidadId(actividad.getOportunidad().getId());
        response.setOportunidadNombre(actividad.getOportunidad().getNombre());
        response.setTipoActividadId(actividad.getTipoActividadId());
        response.setFechaHora(actividad.getFechaHora());
        response.setDuracionMinutos(actividad.getDuracionMinutos());
        response.setNotas(actividad.getNotas());
        response.setFechaCreacion(actividad.getFechaCreacion());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOportunidadId() {
        return oportunidadId;
    }

    public void setOportunidadId(Long oportunidadId) {
        this.oportunidadId = oportunidadId;
    }

    public String getOportunidadNombre() {
        return oportunidadNombre;
    }

    public void setOportunidadNombre(String oportunidadNombre) {
        this.oportunidadNombre = oportunidadNombre;
    }

    public Long getTipoActividadId() {
        return tipoActividadId;
    }

    public void setTipoActividadId(Long tipoActividadId) {
        this.tipoActividadId = tipoActividadId;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<CompromisoResponse> getCompromisos() {
        return compromisos;
    }

    public void setCompromisos(List<CompromisoResponse> compromisos) {
        this.compromisos = compromisos;
    }
}
