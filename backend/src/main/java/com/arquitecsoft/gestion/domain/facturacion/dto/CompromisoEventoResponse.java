package com.arquitecsoft.gestion.domain.facturacion.dto;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoEvento;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompromisoEventoResponse {

    private Long id;
    private Long compromisoId;
    private String tipoEvento;
    private String estadoAnterior;
    private String estadoNuevo;
    private BigDecimal montoAnterior;
    private BigDecimal montoNuevo;
    private LocalDate fechaAnterior;
    private LocalDate fechaNueva;
    private Long compromisoFacturaId;
    private String motivo;
    private Long usuarioId;
    private LocalDateTime fechaEvento;

    public static CompromisoEventoResponse fromEntity(GcCompromisoEvento e) {
        CompromisoEventoResponse r = new CompromisoEventoResponse();
        r.setId(e.getId());
        r.setCompromisoId(e.getCompromiso() != null ? e.getCompromiso().getId() : null);
        r.setTipoEvento(e.getTipoEvento() != null ? e.getTipoEvento().name() : null);
        r.setEstadoAnterior(e.getEstadoAnterior() != null ? e.getEstadoAnterior().name() : null);
        r.setEstadoNuevo(e.getEstadoNuevo() != null ? e.getEstadoNuevo().name() : null);
        r.setMontoAnterior(e.getMontoAnterior());
        r.setMontoNuevo(e.getMontoNuevo());
        r.setFechaAnterior(e.getFechaAnterior());
        r.setFechaNueva(e.getFechaNueva());
        r.setCompromisoFacturaId(e.getCompromisoFacturaId());
        r.setMotivo(e.getMotivo());
        r.setUsuarioId(e.getUsuarioId());
        r.setFechaEvento(e.getFechaEvento());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompromisoId() { return compromisoId; }
    public void setCompromisoId(Long compromisoId) { this.compromisoId = compromisoId; }
    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public BigDecimal getMontoAnterior() { return montoAnterior; }
    public void setMontoAnterior(BigDecimal montoAnterior) { this.montoAnterior = montoAnterior; }
    public BigDecimal getMontoNuevo() { return montoNuevo; }
    public void setMontoNuevo(BigDecimal montoNuevo) { this.montoNuevo = montoNuevo; }
    public LocalDate getFechaAnterior() { return fechaAnterior; }
    public void setFechaAnterior(LocalDate fechaAnterior) { this.fechaAnterior = fechaAnterior; }
    public LocalDate getFechaNueva() { return fechaNueva; }
    public void setFechaNueva(LocalDate fechaNueva) { this.fechaNueva = fechaNueva; }
    public Long getCompromisoFacturaId() { return compromisoFacturaId; }
    public void setCompromisoFacturaId(Long compromisoFacturaId) { this.compromisoFacturaId = compromisoFacturaId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }
}
