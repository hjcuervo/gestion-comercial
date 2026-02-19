package com.arquitecsoft.gestion.domain.pipeline.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class EtapaCreateRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    private Integer orden;

    @Min(value = 0, message = "La probabilidad debe ser entre 0 y 100")
    @Max(value = 100, message = "La probabilidad debe ser entre 0 y 100")
    private Integer probabilidadSugerida;

    @Size(max = 7, message = "El color debe tener maximo 7 caracteres (ej: #FFFFFF)")
    private String color;

    private Integer modoBloqueo = 0;

    public EtapaCreateRequest() {
    }

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getProbabilidadSugerida() {
        return probabilidadSugerida;
    }

    public void setProbabilidadSugerida(Integer probabilidadSugerida) {
        this.probabilidadSugerida = probabilidadSugerida;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getModoBloqueo() {
        return modoBloqueo;
    }

    public void setModoBloqueo(Integer modoBloqueo) {
        this.modoBloqueo = modoBloqueo;
    }
}
