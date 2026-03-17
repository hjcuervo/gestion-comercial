package com.arquitecsoft.gestion.domain.empresa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GC_DOCUMENTTYPE")
public class GcDocumentType {

    @Id
    @Column(name = "nombre", length = 20)
    private String nombre;

    @Column(name = "descripcion", nullable = false, length = 200)
    private String descripcion;

    public GcDocumentType() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
