package com.arquitecsoft.gestion.domain.facturacion.dto;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoIngreso;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompromisoIngresoResponse {

    private Long id;
    private Long contratoId;
    private String contratoNumeroInterno;
    private String contratoCliente;

    private String descripcion;

    private BigDecimal montoPresupuestado;
    private BigDecimal montoEsperadoActual;
    private BigDecimal montoFacturadoAcumulado;
    private BigDecimal saldoPendiente;
    private BigDecimal porcentajeCumplimiento;

    private LocalDate fechaEsperadaOriginal;
    private LocalDate fechaEsperadaActual;

    private String estado;
    private String tipo;

    private Boolean enRiesgo;
    private LocalDateTime fechaUltimaActividad;

    private String motivoPerdida;
    private Long reemplazaAId;

    private Long empresaFacturacionId;
    private String empresaFacturacionNombre;

    private String moneda;

    private boolean estadoFinal;
    private boolean vencido;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    public CompromisoIngresoResponse() {}

    public static CompromisoIngresoResponse fromEntity(GcCompromisoIngreso c) {
        CompromisoIngresoResponse r = new CompromisoIngresoResponse();
        r.setId(c.getId());

        if (c.getContrato() != null) {
            r.setContratoId(c.getContrato().getId());
            r.setContratoNumeroInterno(c.getContrato().getNumeroContratoInterno());
            r.setContratoCliente(c.getContrato().getNumeroContratoCliente());
        }

        r.setDescripcion(c.getDescripcion());
        r.setMontoPresupuestado(c.getMontoPresupuestado());
        r.setMontoEsperadoActual(c.getMontoEsperadoActual());
        r.setMontoFacturadoAcumulado(c.getMontoFacturadoAcumulado());
        r.setSaldoPendiente(c.getSaldoPendiente());
        r.setPorcentajeCumplimiento(c.getPorcentajeCumplimiento());

        r.setFechaEsperadaOriginal(c.getFechaEsperadaOriginal());
        r.setFechaEsperadaActual(c.getFechaEsperadaActual());

        r.setEstado(c.getEstado() != null ? c.getEstado().name() : null);
        r.setTipo(c.getTipo() != null ? c.getTipo().name() : null);

        r.setEnRiesgo(c.getEnRiesgo());
        r.setFechaUltimaActividad(c.getFechaUltimaActividad());
        r.setMotivoPerdida(c.getMotivoPerdida());

        if (c.getReemplazaA() != null) {
            r.setReemplazaAId(c.getReemplazaA().getId());
        }

        if (c.getEmpresaFacturacion() != null) {
            r.setEmpresaFacturacionId(c.getEmpresaFacturacion().getId());
            r.setEmpresaFacturacionNombre(c.getEmpresaFacturacion().getRazonSocial());
        }

        r.setMoneda(c.getMoneda());
        r.setEstadoFinal(c.isEstadoFinal());
        r.setVencido(c.isVencido());
        r.setFechaCreacion(c.getFechaCreacion());
        r.setFechaModificacion(c.getFechaModificacion());

        return r;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }
    public String getContratoNumeroInterno() { return contratoNumeroInterno; }
    public void setContratoNumeroInterno(String contratoNumeroInterno) { this.contratoNumeroInterno = contratoNumeroInterno; }
    public String getContratoCliente() { return contratoCliente; }
    public void setContratoCliente(String contratoCliente) { this.contratoCliente = contratoCliente; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getMontoPresupuestado() { return montoPresupuestado; }
    public void setMontoPresupuestado(BigDecimal montoPresupuestado) { this.montoPresupuestado = montoPresupuestado; }
    public BigDecimal getMontoEsperadoActual() { return montoEsperadoActual; }
    public void setMontoEsperadoActual(BigDecimal montoEsperadoActual) { this.montoEsperadoActual = montoEsperadoActual; }
    public BigDecimal getMontoFacturadoAcumulado() { return montoFacturadoAcumulado; }
    public void setMontoFacturadoAcumulado(BigDecimal montoFacturadoAcumulado) { this.montoFacturadoAcumulado = montoFacturadoAcumulado; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    public BigDecimal getPorcentajeCumplimiento() { return porcentajeCumplimiento; }
    public void setPorcentajeCumplimiento(BigDecimal porcentajeCumplimiento) { this.porcentajeCumplimiento = porcentajeCumplimiento; }
    public LocalDate getFechaEsperadaOriginal() { return fechaEsperadaOriginal; }
    public void setFechaEsperadaOriginal(LocalDate fechaEsperadaOriginal) { this.fechaEsperadaOriginal = fechaEsperadaOriginal; }
    public LocalDate getFechaEsperadaActual() { return fechaEsperadaActual; }
    public void setFechaEsperadaActual(LocalDate fechaEsperadaActual) { this.fechaEsperadaActual = fechaEsperadaActual; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Boolean getEnRiesgo() { return enRiesgo; }
    public void setEnRiesgo(Boolean enRiesgo) { this.enRiesgo = enRiesgo; }
    public LocalDateTime getFechaUltimaActividad() { return fechaUltimaActividad; }
    public void setFechaUltimaActividad(LocalDateTime fechaUltimaActividad) { this.fechaUltimaActividad = fechaUltimaActividad; }
    public String getMotivoPerdida() { return motivoPerdida; }
    public void setMotivoPerdida(String motivoPerdida) { this.motivoPerdida = motivoPerdida; }
    public Long getReemplazaAId() { return reemplazaAId; }
    public void setReemplazaAId(Long reemplazaAId) { this.reemplazaAId = reemplazaAId; }
    public Long getEmpresaFacturacionId() { return empresaFacturacionId; }
    public void setEmpresaFacturacionId(Long empresaFacturacionId) { this.empresaFacturacionId = empresaFacturacionId; }
    public String getEmpresaFacturacionNombre() { return empresaFacturacionNombre; }
    public void setEmpresaFacturacionNombre(String empresaFacturacionNombre) { this.empresaFacturacionNombre = empresaFacturacionNombre; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public boolean isEstadoFinal() { return estadoFinal; }
    public void setEstadoFinal(boolean estadoFinal) { this.estadoFinal = estadoFinal; }
    public boolean isVencido() { return vencido; }
    public void setVencido(boolean vencido) { this.vencido = vencido; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
