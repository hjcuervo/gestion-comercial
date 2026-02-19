package com.arquitecsoft.gestion.domain.documento.entity;

import com.arquitecsoft.gestion.domain.actividad.entity.GcActividad;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_DOCUMENTO")
public class GcDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id")
    private GcOportunidad oportunidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actividad_id")
    private GcActividad actividad;

    @Column(name = "tipo_documento_id", nullable = false)
    private Long tipoDocumentoId;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "nombre_original", nullable = false, length = 200)
    private String nombreOriginal;

    @Column(name = "extension", nullable = false, length = 10)
    private String extension;

    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    @Column(name = "url_storage", nullable = false, length = 500)
    private String urlStorage;

    @Column(name = "bucket_name", nullable = false, length = 100)
    private String bucketName;

    @Column(name = "object_key", nullable = false, length = 200)
    private String objectKey;

    @Column(name = "cargado_por", nullable = false)
    private Long cargadoPor;

    @Column(name = "fecha_carga", nullable = false, updatable = false)
    private LocalDateTime fechaCarga;

    // Constructors
    public GcDocumento() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaCarga = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GcOportunidad getOportunidad() {
        return oportunidad;
    }

    public void setOportunidad(GcOportunidad oportunidad) {
        this.oportunidad = oportunidad;
    }

    public GcActividad getActividad() {
        return actividad;
    }

    public void setActividad(GcActividad actividad) {
        this.actividad = actividad;
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

    public Long getCargadoPor() {
        return cargadoPor;
    }

    public void setCargadoPor(Long cargadoPor) {
        this.cargadoPor = cargadoPor;
    }

    public LocalDateTime getFechaCarga() {
        return fechaCarga;
    }
}
