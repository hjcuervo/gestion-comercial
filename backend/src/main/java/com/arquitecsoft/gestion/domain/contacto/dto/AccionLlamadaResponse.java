package com.arquitecsoft.gestion.domain.contacto.dto;

import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto;

/**
 * Payload para entregar a la plataforma externa de llamadas (click-to-call).
 * El cableado de la plataforma concreta es F-CT6; aquí solo se expone el dato listo.
 */
public class AccionLlamadaResponse {

    private Long contactoId;
    private String numeroE164;
    private String extension;
    private boolean esWhatsapp;
    private String whatsappLink;

    public AccionLlamadaResponse() {}

    public static AccionLlamadaResponse fromEntity(GcContacto c) {
        AccionLlamadaResponse dto = new AccionLlamadaResponse();
        dto.setContactoId(c.getId());
        dto.setNumeroE164(c.getNumeroE164());
        dto.setExtension(c.getExtension());
        dto.setEsWhatsapp(c.isWhatsapp());
        if (c.isWhatsapp() && c.getNumeroE164() != null) {
            dto.setWhatsappLink("https://wa.me/" + c.getNumeroE164().replace("+", ""));
        }
        return dto;
    }

    public Long getContactoId() { return contactoId; }
    public void setContactoId(Long contactoId) { this.contactoId = contactoId; }

    public String getNumeroE164() { return numeroE164; }
    public void setNumeroE164(String numeroE164) { this.numeroE164 = numeroE164; }

    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }

    public boolean isEsWhatsapp() { return esWhatsapp; }
    public void setEsWhatsapp(boolean esWhatsapp) { this.esWhatsapp = esWhatsapp; }

    public String getWhatsappLink() { return whatsappLink; }
    public void setWhatsappLink(String whatsappLink) { this.whatsappLink = whatsappLink; }
}
