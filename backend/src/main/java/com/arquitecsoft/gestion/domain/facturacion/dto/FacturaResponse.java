package com.arquitecsoft.gestion.domain.facturacion.dto;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcFactura;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FacturaResponse {
    private Long id;
    private Long empresaId;
    private String empresaNombre;
    private Long empresaFacturacionId;
    private String empresaFacturacionNombre;
    private String numeroFactura;
    private String prefijo;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private BigDecimal valorBase;
    private BigDecimal valorIva;
    private BigDecimal valorTotal;
    private String moneda;
    private String factroId;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    public FacturaResponse() {}

    public static FacturaResponse fromEntity(GcFactura f) {
        FacturaResponse r = new FacturaResponse();
        r.setId(f.getId());
        r.setEmpresaId(f.getEmpresa().getId());
        r.setEmpresaNombre(f.getEmpresa().getRazonSocial());
        if (f.getEmpresaFacturacion() != null) {
            r.setEmpresaFacturacionId(f.getEmpresaFacturacion().getId());
            r.setEmpresaFacturacionNombre(f.getEmpresaFacturacion().getRazonSocial());
        }
        r.setNumeroFactura(f.getNumeroFactura());
        r.setPrefijo(f.getPrefijo());
        r.setFechaEmision(f.getFechaEmision());
        r.setFechaVencimiento(f.getFechaVencimiento());
        r.setValorBase(f.getValorBase());
        r.setValorIva(f.getValorIva());
        r.setValorTotal(f.getValorTotal());
        r.setMoneda(f.getMoneda());
        r.setFactroId(f.getFactroId());
        r.setObservaciones(f.getObservaciones());
        r.setFechaCreacion(f.getFechaCreacion());
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long empresaFacturacionId) { this.empresaFacturacionId = empresaFacturacionId; }
    public String getEmpresaFacturacionNombre() { return empresaFacturacionNombre; }
    public void setEmpresaFacturacionNombre(String empresaFacturacionNombre) { this.empresaFacturacionNombre = empresaFacturacionNombre; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public BigDecimal getValorBase() { return valorBase; }
    public void setValorBase(BigDecimal valorBase) { this.valorBase = valorBase; }
    public BigDecimal getValorIva() { return valorIva; }
    public void setValorIva(BigDecimal valorIva) { this.valorIva = valorIva; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getFactroId() { return factroId; }
    public void setFactroId(String factroId) { this.factroId = factroId; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
