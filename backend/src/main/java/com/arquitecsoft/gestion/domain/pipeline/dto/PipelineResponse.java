package com.arquitecsoft.gestion.domain.pipeline.dto;

import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineResponse {

    private Long id;
    private String nombre;
    private String ambito;
    private Integer version;
    private String estado;
    private Boolean esDefault;
    private LocalDateTime fechaCreacion;
    private List<EtapaResponse> etapas;

    public PipelineResponse() {
    }

    public static PipelineResponse fromEntity(GcPipeline pipeline) {
        PipelineResponse response = new PipelineResponse();
        response.setId(pipeline.getId());
        response.setNombre(pipeline.getNombre());
        response.setAmbito(pipeline.getAmbito());
        response.setVersion(pipeline.getVersion());
        response.setEstado(pipeline.getEstado().name());
        response.setEsDefault(pipeline.isDefault());
        response.setFechaCreacion(pipeline.getFechaCreacion());

        if (pipeline.getEtapas() != null && !pipeline.getEtapas().isEmpty()) {
            response.setEtapas(pipeline.getEtapas().stream()
                    .map(EtapaResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    public static PipelineResponse fromEntitySimple(GcPipeline pipeline) {
        PipelineResponse response = new PipelineResponse();
        response.setId(pipeline.getId());
        response.setNombre(pipeline.getNombre());
        response.setAmbito(pipeline.getAmbito());
        response.setVersion(pipeline.getVersion());
        response.setEstado(pipeline.getEstado().name());
        response.setEsDefault(pipeline.isDefault());
        response.setFechaCreacion(pipeline.getFechaCreacion());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getEsDefault() {
        return esDefault;
    }

    public void setEsDefault(Boolean esDefault) {
        this.esDefault = esDefault;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<EtapaResponse> getEtapas() {
        return etapas;
    }

    public void setEtapas(List<EtapaResponse> etapas) {
        this.etapas = etapas;
    }
}
