package com.arquitecsoft.gestion.domain.contacto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Catálogo de redes sociales / canales web.
 * Sigue el patrón real de catálogos transversales (code-as-PK, igual que GC_PAISES):
 * la PK es el {@code codigo} VARCHAR2, no un identity numérico.
 */
@Entity
@Table(name = "GC_RED_SOCIAL")
public class GcRedSocial {

    @Id
    @Column(name = "codigo", length = 20)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "url_base", length = 200)
    private String urlBase;

    @Column(name = "icono", length = 40)
    private String icono;

    @Column(name = "orden", nullable = false)
    private Integer orden = 0;

    @Column(name = "activo", nullable = false)
    private Integer activo = 1;

    public GcRedSocial() {}

    public boolean isActivo() {
        return this.activo != null && this.activo == 1;
    }

    // Getters / Setters
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

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }
}
