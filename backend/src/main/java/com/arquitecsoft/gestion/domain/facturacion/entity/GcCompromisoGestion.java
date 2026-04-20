package com.arquitecsoft.gestion.domain.facturacion.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Notas / bitácora libre del usuario sobre un compromiso de ingreso.
 *
 * IMPORTANTE — distinción con GcCompromisoEvento:
 *  - GcCompromisoEvento: eventos de sistema que mueven la máquina de estados
 *    (FacturaRegistrada, FechaReprogramada, CompromisoPerdido...). Estructurado,
 *    inmutable, disparado por transiciones.
 *  - GcCompromisoGestion (esta entidad): interacción comercial registrada
 *    manualmente por el responsable (llamadas, correos, validaciones, soportes,
 *    novedades informativas). Texto libre.
 *
 * Antes: GcFormaPagoGestion apuntaba a GcContratoFormaPago.
 * Ahora:  GcCompromisoGestion apunta a GcCompromisoIngreso.
 */
@Entity
@Table(name = "GC_COMPROMISO_GESTION")
public class GcCompromisoGestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compromiso_id", nullable = false)
    private GcCompromisoIngreso compromiso;

    @Column(name = "tipo_gestion", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private TipoGestion tipoGestion;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "fecha_gestion", nullable = false)
    private LocalDate fechaGestion;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Tipos de gestión comercial preservados del enum original (Mundo 3 previo).
     * Son notas que el usuario registra — NO disparan transiciones de estado.
     * Para eventos con efecto en la máquina de estados, ver GcCompromisoEvento.
     */
    public enum TipoGestion {
        VALIDACION_SERVICIO,
        SOPORTE_OBTENIDO,
        SOLICITUD_EMISION,
        FACTURA_CRUZADA,
        NOVEDAD_APLAZAMIENTO,
        NOVEDAD_CAMBIO_VALOR,
        NOVEDAD_SERVICIO_INCOMPLETO,
        NOVEDAD_SUSPENSION,
        NOVEDAD_OTRA,
        OBSERVACION
    }

    public GcCompromisoGestion() {}

    @PrePersist
    protected void onCreate() { this.fechaCreacion = LocalDateTime.now(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GcCompromisoIngreso getCompromiso() { return compromiso; }
    public void setCompromiso(GcCompromisoIngreso compromiso) { this.compromiso = compromiso; }

    public TipoGestion getTipoGestion() { return tipoGestion; }
    public void setTipoGestion(TipoGestion tipoGestion) { this.tipoGestion = tipoGestion; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDate getFechaGestion() { return fechaGestion; }
    public void setFechaGestion(LocalDate fechaGestion) { this.fechaGestion = fechaGestion; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
