package com.arquitecsoft.gestion.domain.contacto.dto;

import com.arquitecsoft.gestion.domain.contacto.entity.GcRedSocial;

public class RedSocialResponse {

    private String codigo;
    private String nombre;
    private String urlBase;
    private String icono;
    private Integer orden;

    public RedSocialResponse() {}

    public static RedSocialResponse fromEntity(GcRedSocial r) {
        RedSocialResponse dto = new RedSocialResponse();
        dto.setCodigo(r.getCodigo());
        dto.setNombre(r.getNombre());
        dto.setUrlBase(r.getUrlBase());
        dto.setIcono(r.getIcono());
        dto.setOrden(r.getOrden());
        return dto;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUrlBase() { return urlBase; }
    public void setUrlBase(String urlBase) { this.urlBase = urlBase; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
}
