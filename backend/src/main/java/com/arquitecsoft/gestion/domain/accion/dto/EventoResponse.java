package com.arquitecsoft.gestion.domain.accion.dto;

import com.arquitecsoft.gestion.domain.accion.entity.GcAccionEvento;

import java.time.LocalDateTime;

public class EventoResponse {

    private Long id;
    private Long accionId;
    private String tipoEvento;
    private Long actorId;
    private String motivo;
    private String datos;
    private LocalDateTime fechaEvento;

    public EventoResponse() {}

    public static EventoResponse fromEntity(GcAccionEvento e) {
        EventoResponse r = new EventoResponse();
        r.setId(e.getId());
        r.setAccionId(e.getAccion() != null ? e.getAccion().getId() : null);
        r.setTipoEvento(e.getTipoEvento() != null ? e.getTipoEvento().name() : null);
        r.setActorId(e.getActorId());
        r.setMotivo(e.getMotivo());
        r.setDatos(e.getDatos());
        r.setFechaEvento(e.getFechaEvento());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAccionId() { return accionId; }
    public void setAccionId(Long accionId) { this.accionId = accionId; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getDatos() { return datos; }
    public void setDatos(String datos) { this.datos = datos; }

    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }
}
