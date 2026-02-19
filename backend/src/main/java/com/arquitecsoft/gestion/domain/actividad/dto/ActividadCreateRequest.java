package com.arquitecsoft.gestion.domain.actividad.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ActividadCreateRequest {

    @NotNull(message = "La oportunidad es requerida")
    private Long oportunidadId;

    @NotNull(message = "El tipo de actividad es requerido")
    private Long tipoActividadId;

    @NotNull(message = "La fecha y hora son requeridas")
    private LocalDateTime fechaHora;

    private Integer duracionMinutos;

    @Size(max = 2000, message = "Las notas no pueden exceder 2000 caracteres")
    private String notas;

    public ActividadCreateRequest() {
    }

    // Getters and Setters
    public Long getOportunidadId() {
        return oportunidadId;
    }

    public void setOportunidadId(Long oportunidadId) {
        this.oportunidadId = oportunidadId;
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
}
