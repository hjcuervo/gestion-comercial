package com.arquitecsoft.gestion.domain.facturacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload unificado para comandos del CompromisoIngresoService.
 *
 * No se anotan @NotNull/@NotBlank porque los campos requeridos dependen del
 * comando invocado:
 *
 *  - reprogramar():    nuevaFecha + motivo OBLIGATORIOS
 *  - ajustarMonto():   nuevoMonto + motivo OBLIGATORIOS
 *  - marcarPerdido():  motivo OBLIGATORIO
 *
 * La validación por comando se hace en el servicio.
 */
public class CompromisoComandoRequest {

    private LocalDate nuevaFecha;
    private BigDecimal nuevoMonto;
    private String motivo;

    public LocalDate getNuevaFecha() { return nuevaFecha; }
    public void setNuevaFecha(LocalDate nuevaFecha) { this.nuevaFecha = nuevaFecha; }

    public BigDecimal getNuevoMonto() { return nuevoMonto; }
    public void setNuevoMonto(BigDecimal nuevoMonto) { this.nuevoMonto = nuevoMonto; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
