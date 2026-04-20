package com.arquitecsoft.gestion.domain.facturacion.dto;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoGestion;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CompromisoGestionResponse {

    private Long id;
    private Long compromisoId;
    private String tipoGestion;
    private String descripcion;
    private LocalDate fechaGestion;
    private Long creadoPor;
    private LocalDateTime fechaCreacion;

    public static CompromisoGestionResponse fromEntity(GcCompromisoGestion g) {
        CompromisoGestionResponse r = new CompromisoGestionResponse();
        r.setId(g.getId());
        r.setCompromisoId(g.getCompromiso() != null ? g.getCompromiso().getId() : null);
        r.setTipoGestion(g.getTipoGestion() != null ? g.getTipoGestion().name() : null);
        r.setDescripcion(g.getDescripcion());
        r.setFechaGestion(g.getFechaGestion());
        r.setCreadoPor(g.getCreadoPor());
        r.setFechaCreacion(g.getFechaCreacion());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompromisoId() { return compromisoId; }
    public void setCompromisoId(Long compromisoId) { this.compromisoId = compromisoId; }
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
