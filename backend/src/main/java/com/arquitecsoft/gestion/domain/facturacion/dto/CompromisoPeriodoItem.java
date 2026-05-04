package com.arquitecsoft.gestion.domain.facturacion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Item ligero de compromiso para la vista por periodo.
 *
 * Contiene SOLO los datos necesarios para el dashboard de "Compromisos
 * Pendientes", con los valores monetarios duplicados: el valor original
 * en la moneda del compromiso + la versión convertida a COP (usando TRM
 * del año-mes de fechaEsperadaActual).
 *
 * La conversión a COP permite que el frontend haga los totales del resumen
 * sin recalcular y además muestre los valores originales en la lista.
 */
public class CompromisoPeriodoItem {

    private Long id;
    private Long contratoId;
    private String contratoNumero;
    private Long empresaId;
    private String empresaNombre;
    private String descripcion;
    private String estado;
    private String moneda;
    private LocalDate fechaEsperadaActual;

    // Valores en moneda original del compromiso
    private BigDecimal montoPresupuestado;
    private BigDecimal montoFacturadoAcumulado;
    private BigDecimal saldoPendiente;

    // Valores convertidos a COP (si moneda = COP, son iguales)
    private BigDecimal montoPresupuestadoCOP;
    private BigDecimal montoFacturadoAcumuladoCOP;
    private BigDecimal saldoPendienteCOP;
    private BigDecimal trmAplicada;             // null si moneda ya era COP

    private Integer diasVencimiento;            // negativo=vencido, 0=hoy, positivo=por vencer

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }
    public String getContratoNumero() { return contratoNumero; }
    public void setContratoNumero(String contratoNumero) { this.contratoNumero = contratoNumero; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public LocalDate getFechaEsperadaActual() { return fechaEsperadaActual; }
    public void setFechaEsperadaActual(LocalDate fechaEsperadaActual) { this.fechaEsperadaActual = fechaEsperadaActual; }
    public BigDecimal getMontoPresupuestado() { return montoPresupuestado; }
    public void setMontoPresupuestado(BigDecimal montoPresupuestado) { this.montoPresupuestado = montoPresupuestado; }
    public BigDecimal getMontoFacturadoAcumulado() { return montoFacturadoAcumulado; }
    public void setMontoFacturadoAcumulado(BigDecimal montoFacturadoAcumulado) { this.montoFacturadoAcumulado = montoFacturadoAcumulado; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    public BigDecimal getMontoPresupuestadoCOP() { return montoPresupuestadoCOP; }
    public void setMontoPresupuestadoCOP(BigDecimal montoPresupuestadoCOP) { this.montoPresupuestadoCOP = montoPresupuestadoCOP; }
    public BigDecimal getMontoFacturadoAcumuladoCOP() { return montoFacturadoAcumuladoCOP; }
    public void setMontoFacturadoAcumuladoCOP(BigDecimal montoFacturadoAcumuladoCOP) { this.montoFacturadoAcumuladoCOP = montoFacturadoAcumuladoCOP; }
    public BigDecimal getSaldoPendienteCOP() { return saldoPendienteCOP; }
    public void setSaldoPendienteCOP(BigDecimal saldoPendienteCOP) { this.saldoPendienteCOP = saldoPendienteCOP; }
    public BigDecimal getTrmAplicada() { return trmAplicada; }
    public void setTrmAplicada(BigDecimal trmAplicada) { this.trmAplicada = trmAplicada; }
    public Integer getDiasVencimiento() { return diasVencimiento; }
    public void setDiasVencimiento(Integer diasVencimiento) { this.diasVencimiento = diasVencimiento; }
}
