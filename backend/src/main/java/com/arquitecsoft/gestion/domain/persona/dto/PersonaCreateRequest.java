package com.arquitecsoft.gestion.domain.persona.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PersonaCreateRequest {

    @NotBlank(message = "Los nombres son requeridos")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son requeridos")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    // --- Enriquecimiento CRM (F-RP5) ---
    @Size(max = 20)
    private String tipoDocumento;
    @Size(max = 50)
    private String numeroDocumento;
    private Long propietarioId;
    @Size(max = 20)
    private String origenCodigo;
    private Long reportaAId;
    @Size(max = 20)
    private String idioma;
    @Size(max = 1000)
    private String notas;

    public PersonaCreateRequest() {
    }

    // Getters and Setters
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

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public Long getPropietarioId() { return propietarioId; }
    public void setPropietarioId(Long propietarioId) { this.propietarioId = propietarioId; }

    public String getOrigenCodigo() { return origenCodigo; }
    public void setOrigenCodigo(String origenCodigo) { this.origenCodigo = origenCodigo; }

    public Long getReportaAId() { return reportaAId; }
    public void setReportaAId(Long reportaAId) { this.reportaAId = reportaAId; }

    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
