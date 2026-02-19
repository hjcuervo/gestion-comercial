package com.arquitecsoft.gestion.domain.pipeline.dto;

import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa;

public class EtapaResponse {

    private Long id;
    private Long pipelineId;
    private String nombre;
    private Integer orden;
    private Integer probabilidadSugerida;
    private String color;
    private Integer modoBloqueo;
    private String estado;

    public EtapaResponse() {
    }

    public static EtapaResponse fromEntity(GcEtapa etapa) {
        EtapaResponse response = new EtapaResponse();
        response.setId(etapa.getId());
        response.setPipelineId(etapa.getPipeline().getId());
        response.setNombre(etapa.getNombre());
        response.setOrden(etapa.getOrden());
        response.setProbabilidadSugerida(etapa.getProbabilidadSugerida());
        response.setColor(etapa.getColor());
        response.setModoBloqueo(etapa.getModoBloqueo());
        response.setEstado(etapa.getEstado().name());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }

    public Integer getProbabilidadSugerida() {
        return probabilidadSugerida;
    }

    public void setProbabilidadSugerida(Integer probabilidadSugerida) {
        this.probabilidadSugerida = probabilidadSugerida;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getModoBloqueo() {
        return modoBloqueo;
    }

    public void setModoBloqueo(Integer modoBloqueo) {
        this.modoBloqueo = modoBloqueo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
