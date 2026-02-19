package com.arquitecsoft.gestion.domain.documento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DocumentoCreateRequest {

    private Long oportunidadId;

    private Long actividadId;

    @NotNull(message = "El tipo de documento es requerido")
    private Long tipoDocumentoId;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String nombre;

    @NotBlank(message = "El nombre original es requerido")
    @Size(max = 200, message = "El nombre original no puede exceder 200 caracteres")
    private String nombreOriginal;

    @NotBlank(message = "La extension es requerida")
    @Size(max = 10, message = "La extension no puede exceder 10 caracteres")
    private String extension;

    @NotNull(message = "El tamano es requerido")
    private Long tamanoBytes;

    @NotBlank(message = "La URL de storage es requerida")
    @Size(max = 500, message = "La URL no puede exceder 500 caracteres")
    private String urlStorage;

    @NotBlank(message = "El bucket name es requerido")
    @Size(max = 100, message = "El bucket name no puede exceder 100 caracteres")
    private String bucketName;

    @NotBlank(message = "El object key es requerido")
    @Size(max = 200, message = "El object key no puede exceder 200 caracteres")
    private String objectKey;

    public DocumentoCreateRequest() {
    }

    // Getters and Setters
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

    public String getUrlStorage() {
        return urlStorage;
    }

    public void setUrlStorage(String urlStorage) {
        this.urlStorage = urlStorage;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }
}
