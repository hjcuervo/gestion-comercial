package com.arquitecsoft.gestion.domain.facturacion.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TrmCreateRequest {
    @NotNull private Integer anio;
    @NotNull private Integer mes;
    @NotNull private BigDecimal valor;

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
