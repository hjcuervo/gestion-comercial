package com.arquitecsoft.gestion.domain.empresa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GC_PAISES")
public class GcPais {

    @Id
    @Column(name = "codigo", length = 3)
    private String codigo;

    @Column(name = "nombre", length = 50)
    private String nombre;

    public GcPais() {}

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
