package com.arquitecsoft.gestion.domain.oportunidad.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OportunidadUpdateRequest {

    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @DecimalMin(value = "0.0", message = "El valor estimado debe ser positivo")
    private BigDecimal valorEstimado;

    @Pattern(regexp = "^(COP|USD|EUR)$", message = "Moneda debe ser COP, USD o EUR")
    private String moneda;

    @Min(value = 0, message = "La probabilidad debe ser entre 0 y 100")
    @Max(value = 100, message = "La probabilidad debe ser entre 0 y 100")
    private Integer probabilidad;

    private LocalDate fechaEstimadaCierre;

    @Size(max = 100, message = "La fuente no puede exceder 100 caracteres")
    private String fuente;

    @Size(max = 100, message = "El tipo de servicio no puede exceder 100 caracteres")
    private String tipoServicio;

    public OportunidadUpdateRequest() {
    }

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Integer getProbabilidad() {
        return probabilidad;
    }

    public void setProbabilidad(Integer probabilidad) {
        this.probabilidad = probabilidad;
    }

    public LocalDate getFechaEstimadaCierre() {
        return fechaEstimadaCierre;
    }

    public void setFechaEstimadaCierre(LocalDate fechaEstimadaCierre) {
        this.fechaEstimadaCierre = fechaEstimadaCierre;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }
}
