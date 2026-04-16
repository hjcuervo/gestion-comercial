package com.arquitecsoft.gestion.domain.contrato.dto;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FormaPagoResponse {

    private Long id;
    private Long contratoId;
    private String descripcion;
    private BigDecimal valor;
    private BigDecimal valorFacturado;
    private LocalDate fechaEstimadaPago;
    private String estado;
    private Long facturaId;
    private Long empresaFacturacionId;
    private String empresaFacturacionNombre;
    private boolean vencida;

    public FormaPagoResponse() {}

    public static FormaPagoResponse fromEntity(GcContratoFormaPago fp) {
        FormaPagoResponse r = new FormaPagoResponse();
        r.setId(fp.getId());
        r.setContratoId(fp.getContrato().getId());
        r.setDescripcion(fp.getDescripcion());
        r.setValor(fp.getValor());
        r.setValorFacturado(fp.getValorFacturado());
        r.setFechaEstimadaPago(fp.getFechaEstimadaPago());
        r.setEstado(fp.getEstado().name());
        r.setFacturaId(fp.getFacturaId());
        if (fp.getEmpresaFacturacion() != null) {
            r.setEmpresaFacturacionId(fp.getEmpresaFacturacion().getId());
            r.setEmpresaFacturacionNombre(fp.getEmpresaFacturacion().getRazonSocial());
        }
        r.setVencida(fp.isVencida());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getValorFacturado() { return valorFacturado; }
    public void setValorFacturado(BigDecimal valorFacturado) { this.valorFacturado = valorFacturado; }
    public LocalDate getFechaEstimadaPago() { return fechaEstimadaPago; }
    public void setFechaEstimadaPago(LocalDate fechaEstimadaPago) { this.fechaEstimadaPago = fechaEstimadaPago; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long empresaFacturacionId) { this.empresaFacturacionId = empresaFacturacionId; }
    public String getEmpresaFacturacionNombre() { return empresaFacturacionNombre; }
    public void setEmpresaFacturacionNombre(String empresaFacturacionNombre) { this.empresaFacturacionNombre = empresaFacturacionNombre; }
    public boolean isVencida() { return vencida; }
    public void setVencida(boolean vencida) { this.vencida = vencida; }
}
