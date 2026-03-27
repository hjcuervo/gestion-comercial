package com.arquitecsoft.gestion.domain.contrato.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ContratoCreateRequest {

    @NotNull(message = "El proceso de contratación es requerido")
    private Long procesoContratacionId;

    @NotNull(message = "El tipo de contrato es requerido")
    private Long tipoContratoId;

    private Long empresaFacturacionId;

    @Size(max = 50)
    private String numeroContratoInterno;

    @Size(max = 100)
    private String numeroContratoCliente;

    @Size(max = 1000)
    private String objeto;

    @Size(max = 3)
    private String moneda;

    private BigDecimal valorContrato;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Size(max = 100)
    private String responsableGestion;

    @Size(max = 200)
    private String interventor;

    @Size(max = 2000)
    private String observaciones;

    public ContratoCreateRequest() {}

    public Long getProcesoContratacionId() { return procesoContratacionId; }
    public void setProcesoContratacionId(Long procesoContratacionId) { this.procesoContratacionId = procesoContratacionId; }
    public Long getTipoContratoId() { return tipoContratoId; }
    public void setTipoContratoId(Long tipoContratoId) { this.tipoContratoId = tipoContratoId; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long empresaFacturacionId) { this.empresaFacturacionId = empresaFacturacionId; }
    public String getNumeroContratoInterno() { return numeroContratoInterno; }
    public void setNumeroContratoInterno(String numeroContratoInterno) { this.numeroContratoInterno = numeroContratoInterno; }
    public String getNumeroContratoCliente() { return numeroContratoCliente; }
    public void setNumeroContratoCliente(String numeroContratoCliente) { this.numeroContratoCliente = numeroContratoCliente; }
    public String getObjeto() { return objeto; }
    public void setObjeto(String objeto) { this.objeto = objeto; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public BigDecimal getValorContrato() { return valorContrato; }
    public void setValorContrato(BigDecimal valorContrato) { this.valorContrato = valorContrato; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getResponsableGestion() { return responsableGestion; }
    public void setResponsableGestion(String responsableGestion) { this.responsableGestion = responsableGestion; }
    public String getInterventor() { return interventor; }
    public void setInterventor(String interventor) { this.interventor = interventor; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
