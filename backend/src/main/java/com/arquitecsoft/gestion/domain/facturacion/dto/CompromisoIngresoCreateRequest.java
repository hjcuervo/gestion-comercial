package com.arquitecsoft.gestion.domain.facturacion.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request para crear un compromiso de ingreso.
 *
 * NOTA: contratoId NO lleva @NotNull porque se inyecta desde el path variable
 * del controller (POST /api/v1/contratos/{contratoId}/compromisos). La
 * validación @Valid corre antes que el controller pueda setearlo, por eso
 * no puede ser @NotNull.
 */
public class CompromisoIngresoCreateRequest {

    private Long contratoId;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El monto presupuestado es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto presupuestado debe ser mayor a cero")
    private BigDecimal montoPresupuestado;

    @NotNull(message = "La fecha esperada es obligatoria")
    private LocalDate fechaEsperada;

    private String tipo;                    // NUEVO, RECURRENTE, RENOVACION, REEMPLAZO
    private String moneda;
    private Long reemplazaAId;

    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getMontoPresupuestado() { return montoPresupuestado; }
    public void setMontoPresupuestado(BigDecimal montoPresupuestado) { this.montoPresupuestado = montoPresupuestado; }
    public LocalDate getFechaEsperada() { return fechaEsperada; }
    public void setFechaEsperada(LocalDate fechaEsperada) { this.fechaEsperada = fechaEsperada; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public Long getReemplazaAId() { return reemplazaAId; }
    public void setReemplazaAId(Long reemplazaAId) { this.reemplazaAId = reemplazaAId; }
}
