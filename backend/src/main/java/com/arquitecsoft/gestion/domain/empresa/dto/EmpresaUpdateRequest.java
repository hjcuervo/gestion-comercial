package com.arquitecsoft.gestion.domain.empresa.dto;

import jakarta.validation.constraints.Size;

public class EmpresaUpdateRequest {

    private String tipo;

    @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
    private String razonSocial;

    @Size(max = 50, message = "La identificación tributaria no puede exceder 50 caracteres")
    private String identificacionTributaria;

    @Size(max = 200, message = "El sitio web no puede exceder 200 caracteres")
    private String sitioWeb;

    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String pais;

    private String estado;

    public EmpresaUpdateRequest() {
    }

    // Getters and Setters
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
}
