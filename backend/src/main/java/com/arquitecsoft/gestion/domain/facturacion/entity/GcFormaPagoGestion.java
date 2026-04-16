package com.arquitecsoft.gestion.domain.facturacion.entity;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_FORMA_PAGO_GESTION")
public class GcFormaPagoGestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pago_id", nullable = false)
    private GcContratoFormaPago formaPago;

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

    public GcFormaPagoGestion() {}

    @PrePersist
    protected void onCreate() { this.fechaCreacion = LocalDateTime.now(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GcContratoFormaPago getFormaPago() { return formaPago; }
    public void setFormaPago(GcContratoFormaPago formaPago) { this.formaPago = formaPago; }
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
