package com.arquitecsoft.gestion.domain.empresa.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_EMPRESA")
public class GcEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", nullable = false, length = 200)
    private String razonSocial;

    @Column(name = "tipo_doc", length = 20)
    private String tipoDoc;

    @Column(name = "identificacion_tributaria", length = 50)
    private String identificacionTributaria;

    @Column(name = "dv", nullable = false)
    private Integer dv;

    @Column(name = "pais", length = 3)
    private String pais;

    @Column(name = "departamento", length = 10)
    private String departamento;

    @Column(name = "ciudad", length = 10)
    private String ciudad;

    @Column(name = "direccion_fisica", length = 500)
    private String direccionFisica;

    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoEmpresa estado = EstadoEmpresa.ACTIVA;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum EstadoEmpresa {
        ACTIVA, INACTIVA
    }

    public GcEmpresa() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
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

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccionFisica() { return direccionFisica; }
    public void setDireccionFisica(String direccionFisica) { this.direccionFisica = direccionFisica; }

    public String getSitioWeb() { return sitioWeb; }
    public void setSitioWeb(String sitioWeb) { this.sitioWeb = sitioWeb; }

    public EstadoEmpresa getEstado() { return estado; }
    public void setEstado(EstadoEmpresa estado) { this.estado = estado; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
