package com.arquitecsoft.gestion.domain.accion.dto;

import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;

import java.time.LocalDateTime;

public class AccionResponse {

    private Long id;
    private Long responsableId;
    private String estado;
    private String tipoAccion;
    private String prioridad;
    private String origen;
    private Long origenRefId;
    private String referenciaTipo;
    private Long referenciaId;
    private LocalDateTime disponibleDesde;
    private LocalDateTime vencimiento;
    private Long disposicionId;
    private String disposicionCodigo;
    private String disposicionEfecto;
    private String motivo;
    private LocalDateTime fechaCreacion;

    public AccionResponse() {}

    public static AccionResponse fromEntity(GcAccion a) {
        AccionResponse r = new AccionResponse();
        r.setId(a.getId());
        r.setResponsableId(a.getResponsableId());
        r.setEstado(a.getEstado() != null ? a.getEstado().name() : null);
        r.setTipoAccion(a.getTipoAccion() != null ? a.getTipoAccion().name() : null);
        r.setPrioridad(a.getPrioridad() != null ? a.getPrioridad().name() : null);
        r.setOrigen(a.getOrigen() != null ? a.getOrigen().name() : null);
        r.setOrigenRefId(a.getOrigenRefId());
        r.setReferenciaTipo(a.getReferenciaTipo() != null ? a.getReferenciaTipo().name() : null);
        r.setReferenciaId(a.getReferenciaId());
        r.setDisponibleDesde(a.getDisponibleDesde());
        r.setVencimiento(a.getVencimiento());
        if (a.getDisposicion() != null) {
            r.setDisposicionId(a.getDisposicion().getId());
            r.setDisposicionCodigo(a.getDisposicion().getCodigo());
            r.setDisposicionEfecto(a.getDisposicion().getEfecto() != null
                    ? a.getDisposicion().getEfecto().name() : null);
        }
        r.setMotivo(a.getMotivo());
        r.setFechaCreacion(a.getFechaCreacion());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getTipoAccion() { return tipoAccion; }
    public void setTipoAccion(String tipoAccion) { this.tipoAccion = tipoAccion; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }

    public Long getOrigenRefId() { return origenRefId; }
    public void setOrigenRefId(Long origenRefId) { this.origenRefId = origenRefId; }

    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public LocalDateTime getDisponibleDesde() { return disponibleDesde; }
    public void setDisponibleDesde(LocalDateTime disponibleDesde) { this.disponibleDesde = disponibleDesde; }

    public LocalDateTime getVencimiento() { return vencimiento; }
    public void setVencimiento(LocalDateTime vencimiento) { this.vencimiento = vencimiento; }

    public Long getDisposicionId() { return disposicionId; }
    public void setDisposicionId(Long disposicionId) { this.disposicionId = disposicionId; }

    public String getDisposicionCodigo() { return disposicionCodigo; }
    public void setDisposicionCodigo(String disposicionCodigo) { this.disposicionCodigo = disposicionCodigo; }

    public String getDisposicionEfecto() { return disposicionEfecto; }
    public void setDisposicionEfecto(String disposicionEfecto) { this.disposicionEfecto = disposicionEfecto; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
