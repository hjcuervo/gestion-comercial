package com.arquitecsoft.gestion.domain.catalogo.dto;

import com.arquitecsoft.gestion.domain.catalogo.entity.GcSector;

public class SectorResponse {
    private String codigo;
    private String nombre;

    public SectorResponse() {}

    public static SectorResponse fromEntity(GcSector s) {
        SectorResponse r = new SectorResponse();
        r.setCodigo(s.getCodigo());
        r.setNombre(s.getNombre());
        return r;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
