package com.arquitecsoft.gestion.domain.persona.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_PERSONA_EMPRESA", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"persona_id", "empresa_id"})
})
public class GcPersonaEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private GcPersona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private GcEmpresa empresa;

    @Column(name = "cargo", length = 100)
    private String cargo;

    @Column(name = "puesto", length = 100)
    private String puesto;

    @Column(name = "rol_contacto", length = 20)
    @Enumerated(EnumType.STRING)
    private RolContacto rolContacto;

    @Column(name = "email_empresarial", length = 100)
    private String emailEmpresarial;

    @Column(name = "telefono_empresarial", length = 30)
    private String telefonoEmpresarial;

    @Column(name = "es_contacto_principal", nullable = false)
    private Integer esContactoPrincipal = 0;

    // --- Vigencia y empresa principal (F-RP6) ---
    @Column(name = "activo", nullable = false)
    private Integer activo = 1;

    @Column(name = "fecha_inicio")
    private java.time.LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private java.time.LocalDate fechaFin;

    @Column(name = "es_empresa_principal", nullable = false)
    private Integer esEmpresaPrincipal = 0;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public enum RolContacto {
        DECISOR, INFLUENCIADOR, TECNICO, USUARIO, ADMINISTRATIVO
    }

    // Constructors
    public GcPersonaEmpresa() {
    }

    public GcPersonaEmpresa(GcPersona persona, GcEmpresa empresa) {
        this.persona = persona;
        this.empresa = empresa;
        this.esContactoPrincipal = 0;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // Helper methods
    public boolean isContactoPrincipal() {
        return this.esContactoPrincipal != null && this.esContactoPrincipal == 1;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GcPersona getPersona() {
        return persona;
    }

    public void setPersona(GcPersona persona) {
        this.persona = persona;
    }

    public GcEmpresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(GcEmpresa empresa) {
        this.empresa = empresa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public RolContacto getRolContacto() {
        return rolContacto;
    }

    public void setRolContacto(RolContacto rolContacto) {
        this.rolContacto = rolContacto;
    }

    public String getEmailEmpresarial() {
        return emailEmpresarial;
    }

    public void setEmailEmpresarial(String emailEmpresarial) {
        this.emailEmpresarial = emailEmpresarial;
    }

    public String getTelefonoEmpresarial() {
        return telefonoEmpresarial;
    }

    public void setTelefonoEmpresarial(String telefonoEmpresarial) {
        this.telefonoEmpresarial = telefonoEmpresarial;
    }

    public Integer getEsContactoPrincipal() {
        return esContactoPrincipal;
    }

    public void setEsContactoPrincipal(Integer esContactoPrincipal) {
        this.esContactoPrincipal = esContactoPrincipal;
    }

    public boolean isActivo() {
        return this.activo != null && this.activo == 1;
    }

    public boolean isEmpresaPrincipal() {
        return this.esEmpresaPrincipal != null && this.esEmpresaPrincipal == 1;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    public java.time.LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(java.time.LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public java.time.LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(java.time.LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getEsEmpresaPrincipal() {
        return esEmpresaPrincipal;
    }

    public void setEsEmpresaPrincipal(Integer esEmpresaPrincipal) {
        this.esEmpresaPrincipal = esEmpresaPrincipal;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
}
