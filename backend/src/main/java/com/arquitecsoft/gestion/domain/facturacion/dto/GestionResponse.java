package com.arquitecsoft.gestion.domain.facturacion.dto;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcFormaPagoGestion;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GestionResponse {
    private Long id;
    private Long formaPagoId;
    private String tipoGestion;
    private String descripcion;
    private LocalDate fechaGestion;
    private Long creadoPor;
    private LocalDateTime fechaCreacion;

    public static GestionResponse fromEntity(GcFormaPagoGestion g) {
        GestionResponse r = new GestionResponse();
        r.setId(g.getId());
        r.setFormaPagoId(g.getFormaPago().getId());
        r.setTipoGestion(g.getTipoGestion().name());
        r.setDescripcion(g.getDescripcion());
        r.setFechaGestion(g.getFechaGestion());
        r.setCreadoPor(g.getCreadoPor());
        r.setFechaCreacion(g.getFechaCreacion());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFormaPagoId() { return formaPagoId; }
    public void setFormaPagoId(Long formaPagoId) { this.formaPagoId = formaPagoId; }
    public String getTipoGestion() { return tipoGestion; }
    public void setTipoGestion(String tipoGestion) { this.tipoGestion = tipoGestion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public LocalDate getFechaGestion() { return fechaGestion; }
    public void setFechaGestion(LocalDate fechaGestion) { this.fechaGestion = fechaGestion; }
    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
