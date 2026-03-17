package com.arquitecsoft.gestion.domain.empresa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GC_DEPARTAMENTO")
public class GcDepartamento {

    @Id
    @Column(name = "codigo", length = 10)
    private String codigo;

    @Column(name = "descripcion", length = 30)
    private String descripcion;

    @Column(name = "codigo_iso", length = 3)
    private String codigoIso;

    public GcDepartamento() {}

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigoIso() { return codigoIso; }
    public void setCodigoIso(String codigoIso) { this.codigoIso = codigoIso; }
}
