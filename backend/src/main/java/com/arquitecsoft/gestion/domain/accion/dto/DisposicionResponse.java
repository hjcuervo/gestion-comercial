package com.arquitecsoft.gestion.domain.accion.dto;

import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion;

public class DisposicionResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private String efecto;
    private String origenAplicable;
    private Integer esperaReintentoMin;
    private Boolean activo;

    public DisposicionResponse() {}

    public static DisposicionResponse fromEntity(GcDisposicion d) {
        DisposicionResponse r = new DisposicionResponse();
        r.setId(d.getId());
        r.setCodigo(d.getCodigo());
        r.setNombre(d.getNombre());
        r.setEfecto(d.getEfecto() != null ? d.getEfecto().name() : null);
        r.setOrigenAplicable(d.getOrigenAplicable() != null ? d.getOrigenAplicable().name() : null);
        r.setEsperaReintentoMin(d.getEsperaReintentoMin());
        r.setActivo(d.getActivo());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEfecto() { return efecto; }
    public void setEfecto(String efecto) { this.efecto = efecto; }

    public String getOrigenAplicable() { return origenAplicable; }
    public void setOrigenAplicable(String origenAplicable) { this.origenAplicable = origenAplicable; }

    public Integer getEsperaReintentoMin() { return esperaReintentoMin; }
    public void setEsperaReintentoMin(Integer esperaReintentoMin) { this.esperaReintentoMin = esperaReintentoMin; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
