package com.arquitecsoft.gestion.domain.empresa.dto;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;

import java.time.LocalDateTime;

public class EmpresaResponse {

    private Long id;
    private String tipo;
    private String razonSocial;
    private String identificacionTributaria;
    private String sitioWeb;
    private String pais;
    private String estado;
    private LocalDateTime fechaCreacion;

    public EmpresaResponse() {
    }

    public static EmpresaResponse fromEntity(GcEmpresa empresa) {
        EmpresaResponse response = new EmpresaResponse();
        response.setId(empresa.getId());
        response.setTipo(empresa.getTipo().name());
        response.setRazonSocial(empresa.getRazonSocial());
        response.setIdentificacionTributaria(empresa.getIdentificacionTributaria());
        response.setSitioWeb(empresa.getSitioWeb());
        response.setPais(empresa.getPais());
        response.setEstado(empresa.getEstado().name());
        response.setFechaCreacion(empresa.getFechaCreacion());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getIdentificacionTributaria() {
        return identificacionTributaria;
    }

    public void setIdentificacionTributaria(String identificacionTributaria) {
        this.identificacionTributaria = identificacionTributaria;
    }

    public String getSitioWeb() {
        return sitioWeb;
    }

    public void setSitioWeb(String sitioWeb) {
        this.sitioWeb = sitioWeb;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
