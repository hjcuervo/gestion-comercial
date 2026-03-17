package com.arquitecsoft.gestion.domain.empresa.dto;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import java.time.LocalDateTime;

public class EmpresaResponse {

    private Long id;
    private String razonSocial;
    private String tipoDoc;
    private String identificacionTributaria;
    private Integer dv;
    private String pais;
    private String paisNombre;
    private String departamento;
    private String departamentoNombre;
    private String ciudad;
    private String ciudadNombre;
    private String direccionFisica;
    private String sitioWeb;
    private String estado;
    private LocalDateTime fechaCreacion;

    public EmpresaResponse() {}

    public static EmpresaResponse fromEntity(GcEmpresa empresa) {
        EmpresaResponse r = new EmpresaResponse();
        r.setId(empresa.getId());
        r.setRazonSocial(empresa.getRazonSocial());
        r.setTipoDoc(empresa.getTipoDoc());
        r.setIdentificacionTributaria(empresa.getIdentificacionTributaria());
        r.setDv(empresa.getDv());
        r.setPais(empresa.getPais());
        r.setDepartamento(empresa.getDepartamento());
        r.setCiudad(empresa.getCiudad());
        r.setDireccionFisica(empresa.getDireccionFisica());
        r.setSitioWeb(empresa.getSitioWeb());
        r.setEstado(empresa.getEstado().name());
        r.setFechaCreacion(empresa.getFechaCreacion());
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getPaisNombre() { return paisNombre; }
    public void setPaisNombre(String paisNombre) { this.paisNombre = paisNombre; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String departamentoNombre) { this.departamentoNombre = departamentoNombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCiudadNombre() { return ciudadNombre; }
    public void setCiudadNombre(String ciudadNombre) { this.ciudadNombre = ciudadNombre; }

    public String getDireccionFisica() { return direccionFisica; }
    public void setDireccionFisica(String direccionFisica) { this.direccionFisica = direccionFisica; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
