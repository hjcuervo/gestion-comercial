package com.arquitecsoft.gestion.domain.persona.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PersonaEmpresaCreateRequest {

    @NotNull(message = "El ID de empresa es requerido")
    private Long empresaId;

    @Size(max = 100, message = "El cargo no puede exceder 100 caracteres")
    private String cargo;

    @Size(max = 100, message = "El puesto no puede exceder 100 caracteres")
    private String puesto;

    private String rolContacto;

    @Email(message = "El email empresarial debe ser válido")
    @Size(max = 100, message = "El email empresarial no puede exceder 100 caracteres")
    private String emailEmpresarial;

    @Size(max = 30, message = "El teléfono empresarial no puede exceder 30 caracteres")
    private String telefonoEmpresarial;

    private Boolean esContactoPrincipal = false;

    public PersonaEmpresaCreateRequest() {
    }

    // Getters and Setters
    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
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
