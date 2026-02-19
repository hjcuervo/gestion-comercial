package com.arquitecsoft.gestion.domain.documento.dto;

import com.arquitecsoft.gestion.domain.documento.entity.GcDocumento;

import java.time.LocalDateTime;

public class DocumentoResponse {

    private Long id;
    private Long oportunidadId;
    private Long actividadId;
    private Long tipoDocumentoId;
    private String nombre;
    private String nombreOriginal;
    private String extension;
    private Long tamanoBytes;
    private String tamanoFormateado;
    private String urlStorage;
    private LocalDateTime fechaCarga;

    public DocumentoResponse() {
    }

    public static DocumentoResponse fromEntity(GcDocumento documento) {
        DocumentoResponse response = new DocumentoResponse();
        response.setId(documento.getId());
        response.setOportunidadId(documento.getOportunidad() != null ? documento.getOportunidad().getId() : null);
        response.setActividadId(documento.getActividad() != null ? documento.getActividad().getId() : null);
        response.setTipoDocumentoId(documento.getTipoDocumentoId());
        response.setNombre(documento.getNombre());
        response.setNombreOriginal(documento.getNombreOriginal());
        response.setExtension(documento.getExtension());
        response.setTamanoBytes(documento.getTamanoBytes());
        response.setTamanoFormateado(formatearTamano(documento.getTamanoBytes()));
        response.setUrlStorage(documento.getUrlStorage());
        response.setFechaCarga(documento.getFechaCarga());
        return response;
    }

    private static String formatearTamano(Long bytes) {
        if (bytes == null) return "0 B";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOportunidadId() {
        return oportunidadId;
    }

    public void setOportunidadId(Long oportunidadId) {
        this.oportunidadId = oportunidadId;
    }

    public Long getActividadId() {
        return actividadId;
    }

    public void setActividadId(Long actividadId) {
        this.actividadId = actividadId;
    }

    public Long getTipoDocumentoId() {
        return tipoDocumentoId;
    }

    public void setTipoDocumentoId(Long tipoDocumentoId) {
        this.tipoDocumentoId = tipoDocumentoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreOriginal() {
        return nombreOriginal;
    }

    public void setNombreOriginal(String nombreOriginal) {
        this.nombreOriginal = nombreOriginal;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public Long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(Long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public String getTamanoFormateado() {
        return tamanoFormateado;
    }

    public void setTamanoFormateado(String tamanoFormateado) {
        this.tamanoFormateado = tamanoFormateado;
    }

    public String getUrlStorage() {
        return urlStorage;
    }

    public void setUrlStorage(String urlStorage) {
        this.urlStorage = urlStorage;
    }

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(LocalDateTime fechaCarga) {
        this.fechaCarga = fechaCarga;
    }
}
