package com.arquitecsoft.gestion.domain.facturacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representa una forma de pago pendiente por facturar con datos del contrato y empresa.
 */
public class EmisionPendienteResponse {
    private Long formaPagoId;
    private Long contratoId;
    private String contratoNumeroInterno;
    private String contratoCliente;
    private String tipoContrato;
    private Long empresaId;
    private String empresaNombre;
    private String empresaIdentificacion;
    private String formaPagoDescripcion;
    private BigDecimal valor;              // presupuestado
    private BigDecimal valorFacturado;     // real (null si no cruzada)
    private String moneda;
    private LocalDate fechaEstimadaPago;
    private Integer anio;
    private Integer mes;
    private String estado;
    private boolean vencida;
    private BigDecimal valorCop;           // valor convertido a COP (si USD)
    private String ultimaGestionTipo;      // tipo de la última gestión
    private LocalDate ultimaGestionFecha;  // fecha de la última gestión

    // Getters and Setters
    public Long getFormaPagoId() { return formaPagoId; }
    public void setFormaPagoId(Long v) { this.formaPagoId = v; }
    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long v) { this.contratoId = v; }
    public String getContratoNumeroInterno() { return contratoNumeroInterno; }
    public void setContratoNumeroInterno(String v) { this.contratoNumeroInterno = v; }
    public String getContratoCliente() { return contratoCliente; }
    public void setContratoCliente(String v) { this.contratoCliente = v; }
    public String getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(String v) { this.tipoContrato = v; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long v) { this.empresaId = v; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String v) { this.empresaNombre = v; }
    public String getEmpresaIdentificacion() { return empresaIdentificacion; }
    public void setEmpresaIdentificacion(String v) { this.empresaIdentificacion = v; }
    public String getFormaPagoDescripcion() { return formaPagoDescripcion; }
    public void setFormaPagoDescripcion(String v) { this.formaPagoDescripcion = v; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal v) { this.valor = v; }
    public BigDecimal getValorFacturado() { return valorFacturado; }
    public void setValorFacturado(BigDecimal v) { this.valorFacturado = v; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String v) { this.moneda = v; }
    public LocalDate getFechaEstimadaPago() { return fechaEstimadaPago; }
    public void setFechaEstimadaPago(LocalDate v) { this.fechaEstimadaPago = v; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer v) { this.anio = v; }
    public Integer getMes() { return mes; }
    public void setMes(Integer v) { this.mes = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { this.estado = v; }
    public boolean isVencida() { return vencida; }
    public void setVencida(boolean v) { this.vencida = v; }
    public BigDecimal getValorCop() { return valorCop; }
    public void setValorCop(BigDecimal v) { this.valorCop = v; }
    public String getUltimaGestionTipo() { return ultimaGestionTipo; }
    public void setUltimaGestionTipo(String v) { this.ultimaGestionTipo = v; }
    public LocalDate getUltimaGestionFecha() { return ultimaGestionFecha; }
    public void setUltimaGestionFecha(LocalDate v) { this.ultimaGestionFecha = v; }
}
