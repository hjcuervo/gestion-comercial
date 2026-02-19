package com.arquitecsoft.gestion.domain.oportunidad.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OportunidadCreateRequest {

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @NotNull(message = "La empresa es requerida")
    private Long empresaId;

    @NotNull(message = "El pipeline es requerido")
    private Long pipelineId;

    private Long etapaId; // Opcional, si no viene se usa la primera etapa del pipeline

    @DecimalMin(value = "0.0", message = "El valor estimado debe ser positivo")
    private BigDecimal valorEstimado;

    @Pattern(regexp = "^(COP|USD|EUR)$", message = "Moneda debe ser COP, USD o EUR")
    private String moneda = "COP";

    @Min(value = 0, message = "La probabilidad debe ser entre 0 y 100")
    @Max(value = 100, message = "La probabilidad debe ser entre 0 y 100")
    private Integer probabilidad;

    private LocalDate fechaEstimadaCierre;

    @Size(max = 100, message = "La fuente no puede exceder 100 caracteres")
    private String fuente;

    @Size(max = 100, message = "El tipo de servicio no puede exceder 100 caracteres")
    private String tipoServicio;

    public OportunidadCreateRequest() {
    }

    // Getters and Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public Long getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(Long etapaId) {
        this.etapaId = etapaId;
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
