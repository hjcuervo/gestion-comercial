package com.arquitecsoft.gestion.domain.persona.dto;

import com.arquitecsoft.gestion.domain.persona.entity.GcPersonaEmpresa;

public class PersonaEmpresaResponse {

    private Long id;
    private Long empresaId;
    private String empresaRazonSocial;
    private String cargo;
    private String puesto;
    private String rolContacto;
    private String emailEmpresarial;
    private String telefonoEmpresarial;
    private Boolean esContactoPrincipal;

    public PersonaEmpresaResponse() {
    }

    public static PersonaEmpresaResponse fromEntity(GcPersonaEmpresa pe) {
        PersonaEmpresaResponse response = new PersonaEmpresaResponse();
        response.setId(pe.getId());
        response.setEmpresaId(pe.getEmpresa().getId());
        response.setEmpresaRazonSocial(pe.getEmpresa().getRazonSocial());
        response.setCargo(pe.getCargo());
        response.setPuesto(pe.getPuesto());
        response.setRolContacto(pe.getRolContacto() != null ? pe.getRolContacto().name() : null);
        response.setEmailEmpresarial(pe.getEmailEmpresarial());
        response.setTelefonoEmpresarial(pe.getTelefonoEmpresarial());
        response.setEsContactoPrincipal(pe.isContactoPrincipal());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaRazonSocial() {
        return empresaRazonSocial;
    }

    public void setEmpresaRazonSocial(String empresaRazonSocial) {
        this.empresaRazonSocial = empresaRazonSocial;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getRolContacto() {
        return rolContacto;
    }

    public void setRolContacto(String rolContacto) {
        this.rolContacto = rolContacto;
    }

    public String getEmailEmpresarial() {
        return emailEmpresarial;
    }

    public void setEmailEmpresarial(String emailEmpresarial) {
        this.emailEmpresarial = emailEmpresarial;
    }

    public String getTelefonoEmpresarial() {
        return telefonoEmpresarial;
    }

    public void setTelefonoEmpresarial(String telefonoEmpresarial) {
        this.telefonoEmpresarial = telefonoEmpresarial;
    }

    public Boolean getEsContactoPrincipal() {
        return esContactoPrincipal;
    }

    public void setEsContactoPrincipal(Boolean esContactoPrincipal) {
        this.esContactoPrincipal = esContactoPrincipal;
    }
}
