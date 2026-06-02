package com.arquitecsoft.gestion.domain.accion.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Catálogo configurable de resultados de una acción. El {@code efecto} decide el
 * encadenamiento al completar (ver spec §5). {@code origenAplicable} valida a qué
 * tipo de acción se puede aplicar la disposición (RB-MA-06); el valor TODOS marca
 * las disposiciones genéricas.
 */
@Entity
@Table(name = "GC_DISPOSICION")
public class GcDisposicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", nullable = false, length = 40)
    private String codigo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "efecto", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Efecto efecto;

    @Column(name = "origen_aplicable", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OrigenAplicable origenAplicable;

    // Minutos de espera para el efecto REINTENTAR (null para los demás efectos).
    @Column(name = "espera_reintento_min")
    private Integer esperaReintentoMin;

    @Column(name = "activo", nullable = false)
    private Boolean activo = Boolean.TRUE;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum Efecto { REINTENTAR, AVANZAR_PASO, CALIFICAR, DESCARTAR, CUMPLE_REQUISITO, NO_APLICA }
    public enum OrigenAplicable { ENROLAMIENTO, FLUJO_ETAPA, MANUAL, TODOS }

    public GcDisposicion() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Efecto getEfecto() { return efecto; }
    public void setEfecto(Efecto efecto) { this.efecto = efecto; }

    public OrigenAplicable getOrigenAplicable() { return origenAplicable; }
    public void setOrigenAplicable(OrigenAplicable origenAplicable) { this.origenAplicable = origenAplicable; }

    public Integer getEsperaReintentoMin() { return esperaReintentoMin; }
    public void setEsperaReintentoMin(Integer esperaReintentoMin) { this.esperaReintentoMin = esperaReintentoMin; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
