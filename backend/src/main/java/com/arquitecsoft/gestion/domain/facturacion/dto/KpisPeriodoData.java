package com.arquitecsoft.gestion.domain.facturacion.dto;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Agregados del periodo (en COP).
 *
 *  - Los totales suman todos los compromisos pendientes (los 3 grupos).
 *  - porcentajeCumplimiento = totalFacturadoCOP / totalPresupuestadoCOP.
 *    Si totalPresupuestadoCOP=0, se devuelve 0 (evita división por cero).
 *  - conteosPorGrupo / montosPorGrupoCOP: útiles para gráficos y cards
 *    sin que el frontend tenga que sumar las listas.
 *  - distribucionEstado: conteo de cada estado en el universo pendiente.
 *    Usa LinkedHashMap para preservar orden consistente en el JSON.
 */
public class KpisPeriodoData {

    private BigDecimal totalPresupuestadoCOP = BigDecimal.ZERO;
    private BigDecimal totalFacturadoCOP = BigDecimal.ZERO;
    private BigDecimal totalSaldoPendienteCOP = BigDecimal.ZERO;
    private BigDecimal porcentajeCumplimiento = BigDecimal.ZERO;

    private Map<String, Integer> conteosPorGrupo = new LinkedHashMap<>();
    private Map<String, BigDecimal> montosPorGrupoCOP = new LinkedHashMap<>();
    private Map<String, Integer> distribucionEstado = new LinkedHashMap<>();

    public BigDecimal getTotalPresupuestadoCOP() { return totalPresupuestadoCOP; }
    public void setTotalPresupuestadoCOP(BigDecimal totalPresupuestadoCOP) { this.totalPresupuestadoCOP = totalPresupuestadoCOP; }
    public BigDecimal getTotalFacturadoCOP() { return totalFacturadoCOP; }
    public void setTotalFacturadoCOP(BigDecimal totalFacturadoCOP) { this.totalFacturadoCOP = totalFacturadoCOP; }
    public BigDecimal getTotalSaldoPendienteCOP() { return totalSaldoPendienteCOP; }
    public void setTotalSaldoPendienteCOP(BigDecimal totalSaldoPendienteCOP) { this.totalSaldoPendienteCOP = totalSaldoPendienteCOP; }
    public BigDecimal getPorcentajeCumplimiento() { return porcentajeCumplimiento; }
    public void setPorcentajeCumplimiento(BigDecimal porcentajeCumplimiento) { this.porcentajeCumplimiento = porcentajeCumplimiento; }
    public Map<String, Integer> getConteosPorGrupo() { return conteosPorGrupo; }
    public void setConteosPorGrupo(Map<String, Integer> conteosPorGrupo) { this.conteosPorGrupo = conteosPorGrupo; }
    public Map<String, BigDecimal> getMontosPorGrupoCOP() { return montosPorGrupoCOP; }
    public void setMontosPorGrupoCOP(Map<String, BigDecimal> montosPorGrupoCOP) { this.montosPorGrupoCOP = montosPorGrupoCOP; }
    public Map<String, Integer> getDistribucionEstado() { return distribucionEstado; }
    public void setDistribucionEstado(Map<String, Integer> distribucionEstado) { this.distribucionEstado = distribucionEstado; }
}
