package com.arquitecsoft.gestion.domain.accion.dto;

import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion.Efecto;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion.OrigenAplicable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CrearDisposicionRequest {

    @NotBlank(message = "El código es requerido")
    @Size(max = 40)
    private String codigo;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100)
    private String nombre;

    @NotNull(message = "El efecto es requerido")
    private Efecto efecto;

    @NotNull(message = "El origen aplicable es requerido")
    private OrigenAplicable origenAplicable;

    // Solo relevante para el efecto REINTENTAR.
    private Integer esperaReintentoMin;

    public CrearDisposicionRequest() {}

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Efecto getEfecto() { return efecto; }
    public void setEfecto(Efecto efecto) { this.efecto = efecto; }

    public OrigenAplicable getOrigenAplicable() { return origenAplicable; }
    public void setOrigenAplicable(OrigenAplicable origenAplicable) { this.origenAplicable = origenAplicable; }

    public Integer getEsperaReintentoMin() { return esperaReintentoMin; }
    public void setEsperaReintentoMin(Integer esperaReintentoMin) { this.esperaReintentoMin = esperaReintentoMin; }
}
