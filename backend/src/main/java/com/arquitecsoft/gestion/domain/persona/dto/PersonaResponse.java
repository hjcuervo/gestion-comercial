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
    private String email;
    private String telefono;
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
        response.setEmail(persona.getEmail());
        response.setTelefono(persona.getTelefono());
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
        response.setEmail(persona.getEmail());
        response.setTelefono(persona.getTelefono());
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

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
