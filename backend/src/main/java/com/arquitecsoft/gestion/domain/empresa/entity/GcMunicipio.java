package com.arquitecsoft.gestion.domain.empresa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "GC_MUNICIPIOS")
public class GcMunicipio {

    @Id
    @Column(name = "codigo", length = 10)
    private String codigo;

    @Column(name = "descripcion", length = 40)
    private String descripcion;

    @Column(name = "codigo_departamento", nullable = false, length = 10)
    private String codigoDepartamento;

    @Column(name = "alternativo", length = 40)
    private String alternativo;

    public GcMunicipio() {}

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigoDepartamento() { return codigoDepartamento; }
    public void setCodigoDepartamento(String codigoDepartamento) { this.codigoDepartamento = codigoDepartamento; }

    public String getAlternativo() { return alternativo; }
    public void setAlternativo(String alternativo) { this.alternativo = alternativo; }
}
