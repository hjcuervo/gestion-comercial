package com.arquitecsoft.gestion.domain.contacto.dto;

import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto;

import java.time.LocalDateTime;

public class ContactoResponse {

    private Long id;
    private Long empresaId;
    private Long personaId;
    private String tipoCanal;          // enum serializado como string
    private String categoria;
    private String etiqueta;
    private String valor;

    // Teléfono / celular
    private String indicativoPais;
    private String numeroE164;
    private String extension;
    private boolean esWhatsapp;
    private String whatsappLink;       // calculado: https://wa.me/<e164 sin +>

    // Email
    private boolean enviable;

    // Red social (aplanada a codigo + nombre + icono)
    private String redSocialCodigo;
    private String redSocialNombre;
    private String redSocialIcono;
    private String url;

    // Comunes
    private boolean esPrincipal;
    private boolean activo;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    public ContactoResponse() {}

    public static ContactoResponse fromEntity(GcContacto c) {
        ContactoResponse dto = new ContactoResponse();
        dto.setId(c.getId());
        dto.setEmpresaId(c.getEmpresa() != null ? c.getEmpresa().getId() : null);
        dto.setPersonaId(c.getPersona() != null ? c.getPersona().getId() : null);
        dto.setTipoCanal(c.getTipoCanal() != null ? c.getTipoCanal().name() : null);
        dto.setCategoria(c.getCategoria() != null ? c.getCategoria().name() : null);
        dto.setEtiqueta(c.getEtiqueta());
        dto.setValor(c.getValor());
        dto.setIndicativoPais(c.getIndicativoPais());
        dto.setNumeroE164(c.getNumeroE164());
        dto.setExtension(c.getExtension());
        dto.setEsWhatsapp(c.isWhatsapp());
        dto.setEnviable(c.isEnviable());
        if (c.isWhatsapp() && c.getNumeroE164() != null) {
            dto.setWhatsappLink("https://wa.me/" + c.getNumeroE164().replace("+", ""));
        }
        if (c.getRedSocial() != null) {
            dto.setRedSocialCodigo(c.getRedSocial().getCodigo());
            dto.setRedSocialNombre(c.getRedSocial().getNombre());
            dto.setRedSocialIcono(c.getRedSocial().getIcono());
        }
        dto.setUrl(c.getUrl());
        dto.setEsPrincipal(c.isPrincipal());
        dto.setActivo(c.isActivo());
        dto.setObservaciones(c.getObservaciones());
        dto.setFechaCreacion(c.getFechaCreacion());
        return dto;
    }

    // Getters / Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }

    public Long getPersonaId() { return personaId; }
    public void setPersonaId(Long personaId) { this.personaId = personaId; }

    public String getTipoCanal() { return tipoCanal; }
    public void setTipoCanal(String tipoCanal) { this.tipoCanal = tipoCanal; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

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

    public boolean isEsWhatsapp() { return esWhatsapp; }
    public void setEsWhatsapp(boolean esWhatsapp) { this.esWhatsapp = esWhatsapp; }

    public String getWhatsappLink() { return whatsappLink; }
    public void setWhatsappLink(String whatsappLink) { this.whatsappLink = whatsappLink; }

    public boolean isEnviable() { return enviable; }
    public void setEnviable(boolean enviable) { this.enviable = enviable; }

    public String getRedSocialCodigo() { return redSocialCodigo; }
    public void setRedSocialCodigo(String redSocialCodigo) { this.redSocialCodigo = redSocialCodigo; }

    public String getRedSocialNombre() { return redSocialNombre; }
    public void setRedSocialNombre(String redSocialNombre) { this.redSocialNombre = redSocialNombre; }

    public String getRedSocialIcono() { return redSocialIcono; }
    public void setRedSocialIcono(String redSocialIcono) { this.redSocialIcono = redSocialIcono; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public boolean isEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
