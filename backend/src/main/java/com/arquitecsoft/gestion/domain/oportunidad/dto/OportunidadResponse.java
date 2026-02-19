package com.arquitecsoft.gestion.domain.oportunidad.dto;

import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OportunidadResponse {

    private Long id;
    private String nombre;
    private Long empresaId;
    private String empresaNombre;
    private Long pipelineId;
    private String pipelineNombre;
    private Long etapaId;
    private String etapaNombre;
    private String estadoMacro;
    private BigDecimal valorEstimado;
    private String moneda;
    private Integer probabilidad;
    private LocalDate fechaEstimadaCierre;
    private String fuente;
    private String tipoServicio;
    private Long motivoCierreId;
    private String comentarioCierre;
    private LocalDateTime fechaCierre;
    private LocalDateTime fechaCreacion;

    public OportunidadResponse() {
    }

    public static OportunidadResponse fromEntity(GcOportunidad oportunidad) {
        OportunidadResponse response = new OportunidadResponse();
        response.setId(oportunidad.getId());
        response.setNombre(oportunidad.getNombre());
        response.setEmpresaId(oportunidad.getEmpresa().getId());
        response.setEmpresaNombre(oportunidad.getEmpresa().getRazonSocial());
        response.setPipelineId(oportunidad.getPipeline().getId());
        response.setPipelineNombre(oportunidad.getPipeline().getNombre());
        response.setEtapaId(oportunidad.getEtapa().getId());
        response.setEtapaNombre(oportunidad.getEtapa().getNombre());
        response.setEstadoMacro(oportunidad.getEstadoMacro().name());
        response.setValorEstimado(oportunidad.getValorEstimado());
        response.setMoneda(oportunidad.getMoneda());
        response.setProbabilidad(oportunidad.getProbabilidad());
        response.setFechaEstimadaCierre(oportunidad.getFechaEstimadaCierre());
        response.setFuente(oportunidad.getFuente());
        response.setTipoServicio(oportunidad.getTipoServicio());
        response.setMotivoCierreId(oportunidad.getMotivoCierreId());
        response.setComentarioCierre(oportunidad.getComentarioCierre());
        response.setFechaCierre(oportunidad.getFechaCierre());
        response.setFechaCreacion(oportunidad.getFechaCreacion());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public void setEmpresaNombre(String empresaNombre) {
        this.empresaNombre = empresaNombre;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineNombre() {
        return pipelineNombre;
    }

    public void setPipelineNombre(String pipelineNombre) {
        this.pipelineNombre = pipelineNombre;
    }

    public Long getEtapaId() {
        return etapaId;
    }

    public void setEtapaId(Long etapaId) {
        this.etapaId = etapaId;
    }

    public String getEtapaNombre() {
        return etapaNombre;
    }

    public void setEtapaNombre(String etapaNombre) {
        this.etapaNombre = etapaNombre;
    }

    public String getEstadoMacro() {
        return estadoMacro;
    }

    public void setEstadoMacro(String estadoMacro) {
        this.estadoMacro = estadoMacro;
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

    public Long getMotivoCierreId() {
        return motivoCierreId;
    }

    public void setMotivoCierreId(Long motivoCierreId) {
        this.motivoCierreId = motivoCierreId;
    }

    public String getComentarioCierre() {
        return comentarioCierre;
    }

    public void setComentarioCierre(String comentarioCierre) {
        this.comentarioCierre = comentarioCierre;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
