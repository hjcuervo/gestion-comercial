package com.arquitecsoft.gestion.domain.oportunidad.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OportunidadStatsResponse {

    // KPIs principales
    private long totalAbiertas;
    private long totalGanadas;
    private long totalPerdidas;
    private long totalNoConcretadas;
    private double tasaConversion;

    // KPIs por moneda: { "COP": 50000000, "USD": 10000 }
    private Map<String, BigDecimal> valorPipelinePorMoneda;
    private Map<String, BigDecimal> valorGanadoPorMoneda;
    private Map<String, BigDecimal> valorPerdidoPorMoneda;

    // Por etapa (funnel)
    private List<EtapaStat> porEtapa;

    // Por mes (tendencia)
    private List<MesStat> porMes;

    // Top oportunidades por valor
    private List<TopOportunidad> topOportunidades;

    // Por empresa
    private List<EmpresaStat> porEmpresa;

    public OportunidadStatsResponse() {}

    // === Inner classes ===

    public static class EtapaStat {
        private Long etapaId;
        private String etapaNombre;
        private String etapaColor;
        private Integer etapaOrden;
        private long cantidad;
        private BigDecimal valorTotal;

        public EtapaStat() {}
        public EtapaStat(Long etapaId, String etapaNombre, String etapaColor, Integer etapaOrden, long cantidad, BigDecimal valorTotal) {
            this.etapaId = etapaId; this.etapaNombre = etapaNombre; this.etapaColor = etapaColor;
            this.etapaOrden = etapaOrden; this.cantidad = cantidad; this.valorTotal = valorTotal;
        }

        public Long getEtapaId() { return etapaId; }
        public void setEtapaId(Long etapaId) { this.etapaId = etapaId; }
        public String getEtapaNombre() { return etapaNombre; }
        public void setEtapaNombre(String etapaNombre) { this.etapaNombre = etapaNombre; }
        public String getEtapaColor() { return etapaColor; }
        public void setEtapaColor(String etapaColor) { this.etapaColor = etapaColor; }
        public Integer getEtapaOrden() { return etapaOrden; }
        public void setEtapaOrden(Integer etapaOrden) { this.etapaOrden = etapaOrden; }
        public long getCantidad() { return cantidad; }
        public void setCantidad(long cantidad) { this.cantidad = cantidad; }
        public BigDecimal getValorTotal() { return valorTotal; }
        public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    }

    public static class MesStat {
        private String mes;
        private String mesLabel;
        private long nuevas;
        private long ganadas;
        private long perdidas;
        private BigDecimal valorNuevas;
        private BigDecimal valorGanado;

        public MesStat() {}
        public String getMes() { return mes; }
        public void setMes(String mes) { this.mes = mes; }
        public String getMesLabel() { return mesLabel; }
        public void setMesLabel(String mesLabel) { this.mesLabel = mesLabel; }
        public long getNuevas() { return nuevas; }
        public void setNuevas(long nuevas) { this.nuevas = nuevas; }
        public long getGanadas() { return ganadas; }
        public void setGanadas(long ganadas) { this.ganadas = ganadas; }
        public long getPerdidas() { return perdidas; }
        public void setPerdidas(long perdidas) { this.perdidas = perdidas; }
        public BigDecimal getValorNuevas() { return valorNuevas; }
        public void setValorNuevas(BigDecimal valorNuevas) { this.valorNuevas = valorNuevas; }
        public BigDecimal getValorGanado() { return valorGanado; }
        public void setValorGanado(BigDecimal valorGanado) { this.valorGanado = valorGanado; }
    }

    public static class TopOportunidad {
        private Long id;
        private String nombre;
        private String empresaNombre;
        private String etapaNombre;
        private String estadoMacro;
        private BigDecimal valorEstimado;
        private String moneda;
        private Integer probabilidad;

        public TopOportunidad() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getEmpresaNombre() { return empresaNombre; }
        public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
        public String getEtapaNombre() { return etapaNombre; }
        public void setEtapaNombre(String etapaNombre) { this.etapaNombre = etapaNombre; }
        public String getEstadoMacro() { return estadoMacro; }
        public void setEstadoMacro(String estadoMacro) { this.estadoMacro = estadoMacro; }
        public BigDecimal getValorEstimado() { return valorEstimado; }
        public void setValorEstimado(BigDecimal valorEstimado) { this.valorEstimado = valorEstimado; }
        public String getMoneda() { return moneda; }
        public void setMoneda(String moneda) { this.moneda = moneda; }
        public Integer getProbabilidad() { return probabilidad; }
        public void setProbabilidad(Integer probabilidad) { this.probabilidad = probabilidad; }
    }

    public static class EmpresaStat {
        private Long empresaId;
        private String empresaNombre;
        private long totalOportunidades;
        private long abiertas;
        private long ganadas;
        private BigDecimal valorTotal;

        public EmpresaStat() {}
        public Long getEmpresaId() { return empresaId; }
        public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
        public String getEmpresaNombre() { return empresaNombre; }
        public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
        public long getTotalOportunidades() { return totalOportunidades; }
        public void setTotalOportunidades(long totalOportunidades) { this.totalOportunidades = totalOportunidades; }
        public long getAbiertas() { return abiertas; }
        public void setAbiertas(long abiertas) { this.abiertas = abiertas; }
        public long getGanadas() { return ganadas; }
        public void setGanadas(long ganadas) { this.ganadas = ganadas; }
        public BigDecimal getValorTotal() { return valorTotal; }
        public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    }

    // === Getters and Setters ===
    public long getTotalAbiertas() { return totalAbiertas; }
    public void setTotalAbiertas(long totalAbiertas) { this.totalAbiertas = totalAbiertas; }
    public long getTotalGanadas() { return totalGanadas; }
    public void setTotalGanadas(long totalGanadas) { this.totalGanadas = totalGanadas; }
    public long getTotalPerdidas() { return totalPerdidas; }
    public void setTotalPerdidas(long totalPerdidas) { this.totalPerdidas = totalPerdidas; }
    public long getTotalNoConcretadas() { return totalNoConcretadas; }
    public void setTotalNoConcretadas(long totalNoConcretadas) { this.totalNoConcretadas = totalNoConcretadas; }
    public double getTasaConversion() { return tasaConversion; }
    public void setTasaConversion(double tasaConversion) { this.tasaConversion = tasaConversion; }
    public Map<String, BigDecimal> getValorPipelinePorMoneda() { return valorPipelinePorMoneda; }
    public void setValorPipelinePorMoneda(Map<String, BigDecimal> valorPipelinePorMoneda) { this.valorPipelinePorMoneda = valorPipelinePorMoneda; }
    public Map<String, BigDecimal> getValorGanadoPorMoneda() { return valorGanadoPorMoneda; }
    public void setValorGanadoPorMoneda(Map<String, BigDecimal> valorGanadoPorMoneda) { this.valorGanadoPorMoneda = valorGanadoPorMoneda; }
    public Map<String, BigDecimal> getValorPerdidoPorMoneda() { return valorPerdidoPorMoneda; }
    public void setValorPerdidoPorMoneda(Map<String, BigDecimal> valorPerdidoPorMoneda) { this.valorPerdidoPorMoneda = valorPerdidoPorMoneda; }
    public List<EtapaStat> getPorEtapa() { return porEtapa; }
    public void setPorEtapa(List<EtapaStat> porEtapa) { this.porEtapa = porEtapa; }
    public List<MesStat> getPorMes() { return porMes; }
    public void setPorMes(List<MesStat> porMes) { this.porMes = porMes; }
    public List<TopOportunidad> getTopOportunidades() { return topOportunidades; }
    public void setTopOportunidades(List<TopOportunidad> topOportunidades) { this.topOportunidades = topOportunidades; }
    public List<EmpresaStat> getPorEmpresa() { return porEmpresa; }
    public void setPorEmpresa(List<EmpresaStat> porEmpresa) { this.porEmpresa = porEmpresa; }
}
