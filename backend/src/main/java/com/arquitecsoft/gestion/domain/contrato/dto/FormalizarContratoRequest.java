package com.arquitecsoft.gestion.domain.contrato.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FormalizarContratoRequest {

    @NotNull(message = "El ID de la oportunidad es requerido")
    private Long oportunidadId;

    @NotNull(message = "El tipo de contrato es requerido")
    private Long tipoContratoId;

    @Size(max = 50) private String numeroContratoInterno;
    @Size(max = 100) private String numeroContratoCliente;
    @Size(max = 1000) private String objeto;
    @Size(max = 3) private String moneda;
    private BigDecimal valorContrato;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    @Size(max = 100) private String responsableGestion;
    @Size(max = 200) private String interventor;
    private Long empresaFacturacionId;
    @Size(max = 2000) private String observaciones;

    public FormalizarContratoRequest() {}

    public Long getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Long oportunidadId) { this.oportunidadId = oportunidadId; }
    public Long getTipoContratoId() { return tipoContratoId; }
    public void setTipoContratoId(Long tipoContratoId) { this.tipoContratoId = tipoContratoId; }
    public String getNumeroContratoInterno() { return numeroContratoInterno; }
    public void setNumeroContratoInterno(String v) { this.numeroContratoInterno = v; }
    public String getNumeroContratoCliente() { return numeroContratoCliente; }
    public void setNumeroContratoCliente(String v) { this.numeroContratoCliente = v; }
    public String getObjeto() { return objeto; }
    public void setObjeto(String objeto) { this.objeto = objeto; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public BigDecimal getValorContrato() { return valorContrato; }
    public void setValorContrato(BigDecimal v) { this.valorContrato = v; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate v) { this.fechaInicio = v; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate v) { this.fechaFin = v; }
    public String getResponsableGestion() { return responsableGestion; }
    public void setResponsableGestion(String v) { this.responsableGestion = v; }
    public String getInterventor() { return interventor; }
    public void setInterventor(String v) { this.interventor = v; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long v) { this.empresaFacturacionId = v; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String v) { this.observaciones = v; }
}
