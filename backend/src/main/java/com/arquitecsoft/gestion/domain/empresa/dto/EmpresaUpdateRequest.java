package com.arquitecsoft.gestion.domain.empresa.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class EmpresaUpdateRequest {

    @Size(max = 200, message = "La razón social no puede exceder 200 caracteres")
    private String razonSocial;

    @Size(max = 20, message = "El tipo de documento no puede exceder 20 caracteres")
    private String tipoDoc;

    @Pattern(regexp = "^[0-9]*$", message = "La identificación solo puede contener números")
    @Size(max = 50, message = "La identificación no puede exceder 50 caracteres")
    private String identificacionTributaria;

    @Min(value = 0, message = "El DV debe ser entre 0 y 9")
    @Max(value = 9, message = "El DV debe ser entre 0 y 9")
    private Integer dv;

    @Size(max = 3, message = "El código de país no puede exceder 3 caracteres")
    private String pais;

    @Size(max = 10, message = "El código de departamento no puede exceder 10 caracteres")
    private String departamento;

    @Size(max = 10, message = "El código de ciudad no puede exceder 10 caracteres")
    private String ciudad;

    @Size(max = 500, message = "La dirección no puede exceder 500 caracteres")
    private String direccionFisica;

    @Size(max = 200, message = "El sitio web no puede exceder 200 caracteres")
    @Pattern(regexp = "^(https?://.*)?$", message = "El sitio web debe ser una URL válida (http:// o https://)")
    private String sitioWeb;

    private String estado;

    // --- Enriquecimiento CRM (F-RP4) ---
    private String clasificacion;
    private Long propietarioId;
    @Size(max = 20)
    private String sectorCodigo;
    private String tamano;
    @Size(max = 20)
    private String origenCodigo;
    @PositiveOrZero(message = "Los ingresos anuales no pueden ser negativos")
    private BigDecimal ingresosAnuales;
    @Size(max = 1000)
    private String descripcion;

    public EmpresaUpdateRequest() {}

    // Getters and Setters
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }

    public String getTipoDoc() { return tipoDoc; }
    public void setTipoDoc(String tipoDoc) { this.tipoDoc = tipoDoc; }

    public String getIdentificacionTributaria() { return identificacionTributaria; }
    public void setIdentificacionTributaria(String identificacionTributaria) { this.identificacionTributaria = identificacionTributaria; }

    public Integer getDv() { return dv; }
    public void setDv(Integer dv) { this.dv = dv; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccionFisica() { return direccionFisica; }
    public void setDireccionFisica(String direccionFisica) { this.direccionFisica = direccionFisica; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }

    public Long getPropietarioId() { return propietarioId; }
    public void setPropietarioId(Long propietarioId) { this.propietarioId = propietarioId; }

    public String getSectorCodigo() { return sectorCodigo; }
    public void setSectorCodigo(String sectorCodigo) { this.sectorCodigo = sectorCodigo; }

    public String getTamano() { return tamano; }
    public void setTamano(String tamano) { this.tamano = tamano; }

    public String getOrigenCodigo() { return origenCodigo; }
    public void setOrigenCodigo(String origenCodigo) { this.origenCodigo = origenCodigo; }

    public BigDecimal getIngresosAnuales() { return ingresosAnuales; }
    public void setIngresosAnuales(BigDecimal ingresosAnuales) { this.ingresosAnuales = ingresosAnuales; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
