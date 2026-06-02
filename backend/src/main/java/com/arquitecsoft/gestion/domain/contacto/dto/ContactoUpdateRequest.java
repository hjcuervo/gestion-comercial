package com.arquitecsoft.gestion.domain.contacto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Actualización de un contacto existente. No permite cambiar el propietario
 * ni el {@code tipoCanal} (para cambiar de canal, crear uno nuevo y desactivar
 * el anterior). La categoría sí es editable.
 */
public class ContactoUpdateRequest {

    private String categoria;

    @Size(max = 60)
    private String etiqueta;

    @NotBlank(message = "El valor del contacto es requerido")
    @Size(max = 255)
    private String valor;

    @Size(max = 5)
    private String indicativoPais;
    @Size(max = 10)
    private String extension;
    private Boolean esWhatsapp;

    private Boolean enviable;

    @Size(max = 20)
    private String redSocialCodigo;
    @Size(max = 500)
    private String url;

    private Boolean esPrincipal;
    @Size(max = 500)
    private String observaciones;

    public ContactoUpdateRequest() {}

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
