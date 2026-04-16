package com.arquitecsoft.gestion.domain.documento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DocumentoCreateRequest {

    private Long oportunidadId;
    private Long actividadId;
    private Long contratoId;
    private Long modificacionId;

    @NotNull(message = "El tipo de documento es requerido")
    private Long tipoDocumentoId;

    @NotBlank(message = "El nombre es requerido")
    @Size(max = 200)
    private String nombre;

    @Size(max = 200)
    private String nombreOriginal;

    @Size(max = 10)
    private String extension;

    private Long tamanoBytes;

    @Size(max = 500)
    private String urlStorage;

    @Size(max = 100)
    private String bucketName;

    @Size(max = 200)
    private String objectKey;

    @Size(max = 500)
    private String url;

    @Size(max = 500)
    private String descripcion;

    public DocumentoCreateRequest() {}

    public Long getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Long oportunidadId) { this.oportunidadId = oportunidadId; }
    public Long getActividadId() { return actividadId; }
    public void setActividadId(Long actividadId) { this.actividadId = actividadId; }
    public Long getContratoId() { return contratoId; }
    public void setContratoId(Long contratoId) { this.contratoId = contratoId; }
    public Long getModificacionId() { return modificacionId; }
    public void setModificacionId(Long modificacionId) { this.modificacionId = modificacionId; }
    public Long getTipoDocumentoId() { return tipoDocumentoId; }
    public void setTipoDocumentoId(Long tipoDocumentoId) { this.tipoDocumentoId = tipoDocumentoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNombreOriginal() { return nombreOriginal; }
    public void setNombreOriginal(String nombreOriginal) { this.nombreOriginal = nombreOriginal; }
    public String getExtension() { return extension; }
    public void setExtension(String extension) { this.extension = extension; }
    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }
    public String getUrlStorage() { return urlStorage; }
    public void setUrlStorage(String urlStorage) { this.urlStorage = urlStorage; }
    public String getBucketName() { return bucketName; }
    public void setBucketName(String bucketName) { this.bucketName = bucketName; }
    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
