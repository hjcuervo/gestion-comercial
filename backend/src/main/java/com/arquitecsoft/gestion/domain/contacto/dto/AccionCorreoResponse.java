package com.arquitecsoft.gestion.domain.contacto.dto;

import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto;

/**
 * Payload para la plataforma externa de correo. El cableado concreto es F-CT6.
 */
public class AccionCorreoResponse {

    private Long contactoId;
    private String email;
    private boolean enviable;

    public AccionCorreoResponse() {}

    public static AccionCorreoResponse fromEntity(GcContacto c) {
        AccionCorreoResponse dto = new AccionCorreoResponse();
        dto.setContactoId(c.getId());
        dto.setEmail(c.getValor());
        dto.setEnviable(c.isEnviable());
        return dto;
    }

    public Long getContactoId() { return contactoId; }
    public void setContactoId(Long contactoId) { this.contactoId = contactoId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isEnviable() { return enviable; }
    public void setEnviable(boolean enviable) { this.enviable = enviable; }
}
