package com.arquitecsoft.gestion.domain.catalogo.dto;

import com.arquitecsoft.gestion.domain.catalogo.entity.GcOrigen;

public class OrigenResponse {
    private String codigo;
    private String nombre;

    public OrigenResponse() {}

    public static OrigenResponse fromEntity(GcOrigen o) {
        OrigenResponse r = new OrigenResponse();
        r.setCodigo(o.getCodigo());
        r.setNombre(o.getNombre());
        return r;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
