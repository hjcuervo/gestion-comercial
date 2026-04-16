package com.arquitecsoft.gestion.domain.facturacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaCreateRequest {
    @NotNull(message = "La empresa es requerida")
    private Long empresaId;
    private Long empresaFacturacionId;
    @NotBlank(message = "El número de factura es requerido") @Size(max = 50)
    private String numeroFactura;
    @Size(max = 10) private String prefijo;
    @NotNull(message = "La fecha de emisión es requerida")
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private BigDecimal valorBase;
    private BigDecimal valorIva;
    @NotNull(message = "El valor total es requerido")
    private BigDecimal valorTotal;
    @Size(max = 3) private String moneda;
    @Size(max = 50) private String factroId;
    @Size(max = 500) private String observaciones;

    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long v) { this.empresaFacturacionId = v; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String v) { this.numeroFactura = v; }
    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String v) { this.prefijo = v; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate v) { this.fechaEmision = v; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate v) { this.fechaVencimiento = v; }
    public BigDecimal getValorBase() { return valorBase; }
    public void setValorBase(BigDecimal v) { this.valorBase = v; }
    public BigDecimal getValorIva() { return valorIva; }
    public void setValorIva(BigDecimal v) { this.valorIva = v; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal v) { this.valorTotal = v; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String v) { this.moneda = v; }
    public String getFactroId() { return factroId; }
    public void setFactroId(String v) { this.factroId = v; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String v) { this.observaciones = v; }
}
