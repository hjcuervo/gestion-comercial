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
    private Boolean anulada;
    private LocalDateTime fechaAnulacion;
    private String motivoAnulacion;
    private LocalDateTime fechaCreacion;

    /** Monto total aplicado (suma de aplicaciones N:M vigentes contra compromisos). */
    private BigDecimal montoAplicadoAcumulado;

    /** Saldo disponible para aplicar (valorTotal - montoAplicadoAcumulado). */
    private BigDecimal saldoDisponible;

    public static FacturaResponse fromEntity(GcFactura f) {
        return fromEntity(f, null);
    }

    /**
     * Variante que incluye el monto aplicado acumulado y calcula el saldo disponible.
     * Si {@code montoAplicado} es null, los campos quedan en null (el frontend puede
     * pedirlos por separado o asumir que no aplica).
     */
    public static FacturaResponse fromEntity(GcFactura f, BigDecimal montoAplicado) {
        FacturaResponse r = new FacturaResponse();
        r.setId(f.getId());
        if (f.getEmpresa() != null) {
            r.setEmpresaId(f.getEmpresa().getId());
            r.setEmpresaNombre(f.getEmpresa().getRazonSocial());
        }
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
        r.setAnulada(f.getAnulada());
        r.setFechaAnulacion(f.getFechaAnulacion());
        r.setMotivoAnulacion(f.getMotivoAnulacion());
        r.setFechaCreacion(f.getFechaCreacion());
        if (montoAplicado != null) {
            r.setMontoAplicadoAcumulado(montoAplicado);
            BigDecimal total = f.getValorTotal() != null ? f.getValorTotal() : BigDecimal.ZERO;
            r.setSaldoDisponible(total.subtract(montoAplicado));
        }
        return r;
    }

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
    public Boolean getAnulada() { return anulada; }
    public void setAnulada(Boolean anulada) { this.anulada = anulada; }
    public LocalDateTime getFechaAnulacion() { return fechaAnulacion; }
    public void setFechaAnulacion(LocalDateTime fechaAnulacion) { this.fechaAnulacion = fechaAnulacion; }
    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public BigDecimal getMontoAplicadoAcumulado() { return montoAplicadoAcumulado; }
    public void setMontoAplicadoAcumulado(BigDecimal montoAplicadoAcumulado) { this.montoAplicadoAcumulado = montoAplicadoAcumulado; }
    public BigDecimal getSaldoDisponible() { return saldoDisponible; }
    public void setSaldoDisponible(BigDecimal saldoDisponible) { this.saldoDisponible = saldoDisponible; }
}
