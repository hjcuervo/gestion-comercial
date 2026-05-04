package com.arquitecsoft.gestion.domain.facturacion.dto;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Respuesta del endpoint GET /api/v1/compromisos/vista-periodo.
 *
 * Contiene los KPIs agregados y las 3 listas de compromisos pendientes:
 *  1. arrastreMesesAnteriores: fechaEsperadaActual < primer día del mes objetivo
 *  2. mesActualVencidos: fecha en el mes objetivo y fecha <= hoy (solo cuando
 *     esMesActual=true; para meses futuros siempre 0, para meses pasados
 *     todos estarían vencidos y caerían en arrastre de periodos posteriores)
 *  3. mesActualPorVencer: fecha en el mes objetivo y fecha > hoy (solo cuando
 *     esMesActual=true)
 *
 * Cuando se consulta un mes distinto al actual (parámetro anioMes especificado),
 * todos los del mes quedan en arrastre si ya pasaron, o en mesActualPorVencer
 * si el mes es futuro. La distinción "vencidos vs por vencer" solo aplica al
 * mes en curso.
 */
public class VistaPeriodoResponse {

    private String anioMes;                // "YYYY-MM"
    private LocalDate fechaReferencia;     // hoy según sistema
    private boolean esMesActual;
    private String moneda = "COP";
    private String notaTrm;                // advertencia si algún TRM faltaba

    private KpisPeriodoData kpis;

    private List<CompromisoPeriodoItem> arrastreMesesAnteriores;
    private List<CompromisoPeriodoItem> mesActualVencidos;
    private List<CompromisoPeriodoItem> mesActualPorVencer;

    public static String toAnioMes(YearMonth ym) {
        return String.format("%04d-%02d", ym.getYear(), ym.getMonthValue());
    }

    public String getAnioMes() { return anioMes; }
    public void setAnioMes(String anioMes) { this.anioMes = anioMes; }
    public LocalDate getFechaReferencia() { return fechaReferencia; }
    public void setFechaReferencia(LocalDate fechaReferencia) { this.fechaReferencia = fechaReferencia; }
    public boolean isEsMesActual() { return esMesActual; }
    public void setEsMesActual(boolean esMesActual) { this.esMesActual = esMesActual; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getNotaTrm() { return notaTrm; }
    public void setNotaTrm(String notaTrm) { this.notaTrm = notaTrm; }
    public KpisPeriodoData getKpis() { return kpis; }
    public void setKpis(KpisPeriodoData kpis) { this.kpis = kpis; }
    public List<CompromisoPeriodoItem> getArrastreMesesAnteriores() { return arrastreMesesAnteriores; }
    public void setArrastreMesesAnteriores(List<CompromisoPeriodoItem> arrastreMesesAnteriores) { this.arrastreMesesAnteriores = arrastreMesesAnteriores; }
    public List<CompromisoPeriodoItem> getMesActualVencidos() { return mesActualVencidos; }
    public void setMesActualVencidos(List<CompromisoPeriodoItem> mesActualVencidos) { this.mesActualVencidos = mesActualVencidos; }
    public List<CompromisoPeriodoItem> getMesActualPorVencer() { return mesActualPorVencer; }
    public void setMesActualPorVencer(List<CompromisoPeriodoItem> mesActualPorVencer) { this.mesActualPorVencer = mesActualPorVencer; }
}
