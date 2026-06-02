package com.arquitecsoft.gestion.domain.persona.dto;

import com.arquitecsoft.gestion.domain.persona.entity.GcPersona;
import com.arquitecsoft.gestion.domain.persona.entity.GcPersonaEmpresa;

import java.util.List;
import java.util.stream.Collectors;

public class PersonaResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private Long propietarioId;
    private String origenCodigo;
    private String origenNombre;
    private Long reportaAId;
    private String reportaANombre;
    private String idioma;
    private String notas;
    private Boolean activo;
    private List<PersonaEmpresaResponse> empresas;

    public PersonaResponse() {
    }

    public static PersonaResponse fromEntity(GcPersona persona) {
        PersonaResponse response = new PersonaResponse();
        response.setId(persona.getId());
        response.setNombres(persona.getNombres());
        response.setApellidos(persona.getApellidos());
        response.setNombreCompleto(persona.getNombreCompleto());
        response.setTipoDocumento(persona.getTipoDocumento());
        response.setNumeroDocumento(persona.getNumeroDocumento());
        response.setPropietarioId(persona.getPropietarioId());
        if (persona.getOrigen() != null) {
            response.setOrigenCodigo(persona.getOrigen().getCodigo());
            response.setOrigenNombre(persona.getOrigen().getNombre());
        }
        if (persona.getReportaA() != null) {
            response.setReportaAId(persona.getReportaA().getId());
            response.setReportaANombre(persona.getReportaA().getNombreCompleto());
        }
        response.setIdioma(persona.getIdioma());
        response.setNotas(persona.getNotas());
        response.setActivo(persona.isActivo());
        
        if (persona.getEmpresas() != null) {
            response.setEmpresas(persona.getEmpresas().stream()
                    .map(PersonaEmpresaResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }

    public static PersonaResponse fromEntitySimple(GcPersona persona) {
        PersonaResponse response = new PersonaResponse();
        response.setId(persona.getId());
        response.setNombres(persona.getNombres());
        response.setApellidos(persona.getApellidos());
        response.setNombreCompleto(persona.getNombreCompleto());
        response.setActivo(persona.isActivo());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public Long getPropietarioId() { return propietarioId; }
    public void setPropietarioId(Long propietarioId) { this.propietarioId = propietarioId; }

    public String getOrigenCodigo() { return origenCodigo; }
    public void setOrigenCodigo(String origenCodigo) { this.origenCodigo = origenCodigo; }

    public String getOrigenNombre() { return origenNombre; }
    public void setOrigenNombre(String origenNombre) { this.origenNombre = origenNombre; }

    public Long getReportaAId() { return reportaAId; }
    public void setReportaAId(Long reportaAId) { this.reportaAId = reportaAId; }

    public String getReportaANombre() { return reportaANombre; }
    public void setReportaANombre(String reportaANombre) { this.reportaANombre = reportaANombre; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<PersonaEmpresaResponse> getEmpresas() {
        return empresas;
    }

    public void setEmpresas(List<PersonaEmpresaResponse> empresas) {
        this.empresas = empresas;
    }
}
