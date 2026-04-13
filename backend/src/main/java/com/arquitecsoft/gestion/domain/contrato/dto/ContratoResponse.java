package com.arquitecsoft.gestion.domain.contrato.dto;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ContratoResponse {

    private Long id;
    private Long procesoContratacionId;
    private Long oportunidadId;
    private String oportunidadNombre;
    private Long empresaId;
    private String empresaNombre;
    private Long empresaFacturacionId;
    private String empresaFacturacionNombre;
    private Long tipoContratoId;
    private String tipoContratoNombre;
    private String numeroContratoInterno;
    private String numeroContratoCliente;
    private String objeto;
    private String moneda;
    private BigDecimal valorContrato;
    private BigDecimal valorAjuste;
    private BigDecimal valorTotal;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
    private String responsableGestion;
    private String interventor;
    private String observaciones;
    private LocalDateTime fechaCreacion;
    private List<FormaPagoResponse> formasPago;
    private List<ModificacionResponse> modificaciones;

    public ContratoResponse() {}

    public static ContratoResponse fromEntity(GcContrato c) {
        ContratoResponse r = new ContratoResponse();
        r.setId(c.getId());
        if (c.getProcesoContratacion() != null) {
            r.setProcesoContratacionId(c.getProcesoContratacion().getId());
        }
        r.setOportunidadId(c.getOportunidad().getId());
        r.setOportunidadNombre(c.getOportunidad().getNombre());
        r.setEmpresaId(c.getEmpresa().getId());
        r.setEmpresaNombre(c.getEmpresa().getRazonSocial());
        if (c.getEmpresaFacturacion() != null) {
            r.setEmpresaFacturacionId(c.getEmpresaFacturacion().getId());
            r.setEmpresaFacturacionNombre(c.getEmpresaFacturacion().getRazonSocial());
        }
        r.setTipoContratoId(c.getTipoContrato().getId());
        r.setTipoContratoNombre(c.getTipoContrato().getNombre());
        r.setNumeroContratoInterno(c.getNumeroContratoInterno());
        r.setNumeroContratoCliente(c.getNumeroContratoCliente());
        r.setObjeto(c.getObjeto());
        r.setMoneda(c.getMoneda());
        r.setValorContrato(c.getValorContrato());
        r.setValorAjuste(c.getValorAjuste());
        r.setValorTotal(c.getValorTotal());
        r.setFechaInicio(c.getFechaInicio());
        r.setFechaFin(c.getFechaFin());
        r.setEstado(c.getEstado().name());
        r.setResponsableGestion(c.getResponsableGestion());
        r.setInterventor(c.getInterventor());
        r.setObservaciones(c.getObservaciones());
        r.setFechaCreacion(c.getFechaCreacion());

        if (c.getFormasPago() != null && !c.getFormasPago().isEmpty()) {
            r.setFormasPago(c.getFormasPago().stream()
                    .map(FormaPagoResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        if (c.getModificaciones() != null && !c.getModificaciones().isEmpty()) {
            r.setModificaciones(c.getModificaciones().stream()
                    .map(ModificacionResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return r;
    }

    public static ContratoResponse fromEntitySimple(GcContrato c) {
        ContratoResponse r = new ContratoResponse();
        r.setId(c.getId());
        r.setOportunidadId(c.getOportunidad().getId());
        r.setOportunidadNombre(c.getOportunidad().getNombre());
        r.setEmpresaId(c.getEmpresa().getId());
        r.setEmpresaNombre(c.getEmpresa().getRazonSocial());
        r.setTipoContratoId(c.getTipoContrato().getId());
        r.setTipoContratoNombre(c.getTipoContrato().getNombre());
        r.setNumeroContratoInterno(c.getNumeroContratoInterno());
        r.setMoneda(c.getMoneda());
        r.setValorContrato(c.getValorContrato());
        r.setValorAjuste(c.getValorAjuste());
        r.setValorTotal(c.getValorTotal());
        r.setFechaInicio(c.getFechaInicio());
        r.setFechaFin(c.getFechaFin());
        r.setEstado(c.getEstado().name());
        r.setFechaCreacion(c.getFechaCreacion());
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProcesoContratacionId() { return procesoContratacionId; }
    public void setProcesoContratacionId(Long procesoContratacionId) { this.procesoContratacionId = procesoContratacionId; }
    public Long getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Long oportunidadId) { this.oportunidadId = oportunidadId; }
    public String getOportunidadNombre() { return oportunidadNombre; }
    public void setOportunidadNombre(String oportunidadNombre) { this.oportunidadNombre = oportunidadNombre; }
    public Long getEmpresaId() { return empresaId; }
    public void setEmpresaId(Long empresaId) { this.empresaId = empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public void setEmpresaNombre(String empresaNombre) { this.empresaNombre = empresaNombre; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long empresaFacturacionId) { this.empresaFacturacionId = empresaFacturacionId; }
    public String getEmpresaFacturacionNombre() { return empresaFacturacionNombre; }
    public void setEmpresaFacturacionNombre(String empresaFacturacionNombre) { this.empresaFacturacionNombre = empresaFacturacionNombre; }
    public Long getTipoContratoId() { return tipoContratoId; }
    public void setTipoContratoId(Long tipoContratoId) { this.tipoContratoId = tipoContratoId; }
    public String getTipoContratoNombre() { return tipoContratoNombre; }
    public void setTipoContratoNombre(String tipoContratoNombre) { this.tipoContratoNombre = tipoContratoNombre; }
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
    public BigDecimal getValorAjuste() { return valorAjuste; }
    public void setValorAjuste(BigDecimal valorAjuste) { this.valorAjuste = valorAjuste; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getResponsableGestion() { return responsableGestion; }
    public void setResponsableGestion(String responsableGestion) { this.responsableGestion = responsableGestion; }
    public String getInterventor() { return interventor; }
    public void setInterventor(String interventor) { this.interventor = interventor; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public List<FormaPagoResponse> getFormasPago() { return formasPago; }
    public void setFormasPago(List<FormaPagoResponse> formasPago) { this.formasPago = formasPago; }
    public List<ModificacionResponse> getModificaciones() { return modificaciones; }
    public void setModificaciones(List<ModificacionResponse> modificaciones) { this.modificaciones = modificaciones; }
}
