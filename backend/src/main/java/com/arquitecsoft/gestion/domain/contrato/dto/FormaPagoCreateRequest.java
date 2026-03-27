package com.arquitecsoft.gestion.domain.contrato.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FormaPagoCreateRequest {

    @NotBlank(message = "La descripción es requerida")
    @Size(max = 500)
    private String descripcion;

    @NotNull(message = "El valor es requerido")
    private BigDecimal valor;

    private LocalDate fechaEstimadaPago;

    private Long empresaFacturacionId;

    public FormaPagoCreateRequest() {}

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getFechaEstimadaPago() { return fechaEstimadaPago; }
    public void setFechaEstimadaPago(LocalDate fechaEstimadaPago) { this.fechaEstimadaPago = fechaEstimadaPago; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long empresaFacturacionId) { this.empresaFacturacionId = empresaFacturacionId; }
}
