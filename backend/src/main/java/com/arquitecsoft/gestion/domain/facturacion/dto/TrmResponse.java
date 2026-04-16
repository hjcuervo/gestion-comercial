package com.arquitecsoft.gestion.domain.facturacion.dto;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcTrm;
import java.math.BigDecimal;

public class TrmResponse {
    private Long id;
    private Integer anio;
    private Integer mes;
    private String monedaOrigen;
    private String monedaDestino;
    private BigDecimal valor;

    public TrmResponse() {}

    public static TrmResponse fromEntity(GcTrm t) {
        TrmResponse r = new TrmResponse();
        r.setId(t.getId());
        r.setAnio(t.getAnio());
        r.setMes(t.getMes());
        r.setMonedaOrigen(t.getMonedaOrigen());
        r.setMonedaDestino(t.getMonedaDestino());
        r.setValor(t.getValor());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public String getMonedaOrigen() { return monedaOrigen; }
    public void setMonedaOrigen(String monedaOrigen) { this.monedaOrigen = monedaOrigen; }
    public String getMonedaDestino() { return monedaDestino; }
    public void setMonedaDestino(String monedaDestino) { this.monedaDestino = monedaDestino; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
