package com.arquitecsoft.gestion.domain.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RevertirFacturaRequest {

    @NotNull(message = "compromisoFacturaId es obligatorio")
    private Long compromisoFacturaId;

    @NotBlank(message = "El motivo de reversión es obligatorio")
    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivo;

    public Long getCompromisoFacturaId() { return compromisoFacturaId; }
    public void setCompromisoFacturaId(Long compromisoFacturaId) { this.compromisoFacturaId = compromisoFacturaId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
