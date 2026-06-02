package com.arquitecsoft.gestion.domain.contacto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Alta de un contacto. El propietario (empresa o persona) viene en el path,
 * no en el cuerpo. Las reglas por tipo de canal (E.164, email, red social,
 * categoría obligatoria en persona) se validan en el service (RB-20..RB-31).
 */
public class ContactoCreateRequest {

    @NotNull(message = "El tipo de canal es requerido")
    private String tipoCanal;          // TELEFONO | CELULAR | EMAIL | RED_SOCIAL

    private String categoria;          // PERSONAL | EMPRESARIAL | OTRO (obligatoria en persona)

    @Size(max = 60, message = "La etiqueta admite máximo 60 caracteres")
    private String etiqueta;

    @NotBlank(message = "El valor del contacto es requerido")
    @Size(max = 255)
    private String valor;

    // Teléfono / celular
    @Size(max = 5)
    private String indicativoPais;
    @Size(max = 10)
    private String extension;
    private Boolean esWhatsapp;

    // Email
    private Boolean enviable;

    // Red social
    @Size(max = 20)
    private String redSocialCodigo;
    @Size(max = 500)
    private String url;

    // Comunes
    private Boolean esPrincipal;
    @Size(max = 500)
    private String observaciones;

    public ContactoCreateRequest() {}

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

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public Boolean getEsWhatsapp() { return esWhatsapp; }
    public void setEsWhatsapp(Boolean esWhatsapp) { this.esWhatsapp = esWhatsapp; }

    public Boolean getEnviable() { return enviable; }
    public void setEnviable(Boolean enviable) { this.enviable = enviable; }

    public String getRedSocialCodigo() { return redSocialCodigo; }
    public void setRedSocialCodigo(String redSocialCodigo) { this.redSocialCodigo = redSocialCodigo; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
