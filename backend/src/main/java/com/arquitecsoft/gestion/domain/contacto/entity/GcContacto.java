package com.arquitecsoft.gestion.domain.contacto.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.persona.entity.GcPersona;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Medio de contacto multicanal. Pertenece a EXACTAMENTE una empresa o una persona
 * (CHECK CK_GC_CONTACTO_OWNER). Un mismo propietario puede tener varios medios.
 *
 * Convenciones tomadas de las entidades reales del repo:
 *  - Banderas como {@code Integer} 0/1 (no boolean), igual que GcPersona.activo.
 *  - Auditoría con {@code creadoPor}/{@code modificadoPor} como FK (Long) a GC_USUARIO.
 *  - Enums anidados + EnumType.STRING con CHECK constraint en Oracle.
 */
@Entity
@Table(name = "GC_CONTACTO")
public class GcContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Propietario: exactamente uno poblado (FKs nullable a propósito — lección #2)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private GcEmpresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private GcPersona persona;

    @Column(name = "tipo_canal", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoCanal tipoCanal;

    @Column(name = "categoria", length = 20)
    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Column(name = "etiqueta", length = 60)
    private String etiqueta;

    @Column(name = "valor", nullable = false, length = 255)
    private String valor;

    // --- Teléfono / Celular ---
    @Column(name = "indicativo_pais", length = 5)
    private String indicativoPais;

    @Column(name = "numero_e164", length = 20)
    private String numeroE164;

    @Column(name = "extension", length = 10)
    private String extension;

    @Column(name = "es_whatsapp", nullable = false)
    private Integer esWhatsapp = 0;

    // --- Email ---
    @Column(name = "enviable", nullable = false)
    private Integer enviable = 1;

    // --- Red social ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "red_social_codigo")
    private GcRedSocial redSocial;

    @Column(name = "url", length = 500)
    private String url;

    // --- Comunes ---
    @Column(name = "es_principal", nullable = false)
    private Integer esPrincipal = 0;

    @Column(name = "activo", nullable = false)
    private Integer activo = 1;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // --- Auditoría ---
    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum TipoCanal {
        TELEFONO, CELULAR, EMAIL, RED_SOCIAL
    }

    public enum Categoria {
        PERSONAL, EMPRESARIAL, OTRO
    }

    public GcContacto() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // Helpers de banderas (estilo GcPersona.isActivo)
    public boolean isWhatsapp()  { return esWhatsapp != null && esWhatsapp == 1; }
    public boolean isEnviable()  { return enviable != null && enviable == 1; }
    public boolean isPrincipal() { return esPrincipal != null && esPrincipal == 1; }
    public boolean isActivo()    { return activo != null && activo == 1; }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GcEmpresa getEmpresa() { return empresa; }
    public void setEmpresa(GcEmpresa empresa) { this.empresa = empresa; }

    public GcPersona getPersona() { return persona; }
    public void setPersona(GcPersona persona) { this.persona = persona; }

    public TipoCanal getTipoCanal() { return tipoCanal; }
    public void setTipoCanal(TipoCanal tipoCanal) { this.tipoCanal = tipoCanal; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getEtiqueta() { return etiqueta; }
    public void setEtiqueta(String etiqueta) { this.etiqueta = etiqueta; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public String getIndicativoPais() { return indicativoPais; }
    public void setIndicativoPais(String indicativoPais) { this.indicativoPais = indicativoPais; }

    public String getNumeroE164() { return numeroE164; }
    public void setNumeroE164(String numeroE164) { this.numeroE164 = numeroE164; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Integer getEsWhatsapp() { return esWhatsapp; }
    public void setEsWhatsapp(Integer esWhatsapp) { this.esWhatsapp = esWhatsapp; }

    public Integer getEnviable() { return enviable; }
    public void setEnviable(Integer enviable) { this.enviable = enviable; }

    public GcRedSocial getRedSocial() { return redSocial; }
    public void setRedSocial(GcRedSocial redSocial) { this.redSocial = redSocial; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Integer getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Integer esPrincipal) { this.esPrincipal = esPrincipal; }

    public Integer getActivo() { return activo; }
    public void setActivo(Integer activo) { this.activo = activo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
