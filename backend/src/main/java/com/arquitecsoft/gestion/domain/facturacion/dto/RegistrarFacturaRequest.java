package com.arquitecsoft.gestion.domain.facturacion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class RegistrarFacturaRequest {

    @NotNull(message = "facturaId es obligatorio")
    private Long facturaId;

    @NotNull(message = "El monto aplicado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto aplicado debe ser mayor a cero")
    private BigDecimal montoAplicado;

    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }

    public BigDecimal getMontoAplicado() { return montoAplicado; }
    public void setMontoAplicado(BigDecimal montoAplicado) { this.montoAplicado = montoAplicado; }
}
