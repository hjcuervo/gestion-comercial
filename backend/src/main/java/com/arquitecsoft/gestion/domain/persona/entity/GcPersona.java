package com.arquitecsoft.gestion.domain.persona.entity;

import com.arquitecsoft.gestion.domain.catalogo.entity.GcOrigen;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GC_PERSONA")
public class GcPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    // --- Enriquecimiento CRM (F-RP5) ---
    @Column(name = "tipo_documento", length = 20)
    private String tipoDocumento;

    @Column(name = "numero_documento", length = 50)
    private String numeroDocumento;

    @Column(name = "propietario_id")
    private Long propietarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origen_codigo")
    private GcOrigen origen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporta_a_id")
    private GcPersona reportaA;

    @Column(name = "idioma", length = 20)
    private String idioma;

    @Column(name = "notas", length = 1000)
    private String notas;

    @Column(name = "activo", nullable = false)
    private Integer activo = 1;

    @Column(name = "creado_por")
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GcPersonaEmpresa> empresas = new ArrayList<>();

    // Constructors
    public GcPersona() {
    }

    public GcPersona(String nombres, String apellidos) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.activo = 1;
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // Helper methods
    public String getNombreCompleto() {
        return this.nombres + " " + this.apellidos;
    }

    public boolean isActivo() {
        return this.activo != null && this.activo == 1;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public Long getPropietarioId() {
        return propietarioId;
    }

    public void setPropietarioId(Long propietarioId) {
        this.propietarioId = propietarioId;
    }

    public GcOrigen getOrigen() {
        return origen;
    }

    public void setOrigen(GcOrigen origen) {
        this.origen = origen;
    }

    public GcPersona getReportaA() {
        return reportaA;
    }

    public void setReportaA(GcPersona reportaA) {
        this.reportaA = reportaA;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    public Long getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(Long creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Long getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(Long modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public List<GcPersonaEmpresa> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<GcPersonaEmpresa> empresas) {
        this.empresas = empresas;
    }
}
