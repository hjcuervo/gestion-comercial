package com.arquitecsoft.gestion.domain.facturacion.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Aplicación de una factura contra un compromiso de ingreso.
 *
 * Permite relación N:N:
 *  - Una factura puede aplicar contra varios compromisos (factura consolidada).
 *  - Un compromiso puede recibir varias facturas parciales (caso 3.1 de la spec:
 *    presupuesto 10.000 cubierto con facturas de 3.000 + 2.000 + 5.000).
 *
 * El campo `montoAplicado` es independiente del valor total de la factura:
 * refleja cuánto de esa factura se está imputando a este compromiso específico.
 *
 * El flag `revertida` soporta el caso 3.8 (Cancelación/reversión de factura):
 * la aplicación NO se elimina — se marca como revertida y se descuenta del
 * acumulado del compromiso en un evento FacturaRevertida.
 */
@Entity
@Table(
    name = "GC_COMPROMISO_FACTURA",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_COMPR_FACT",
        columnNames = {"compromiso_id", "factura_id"}
    )
)
public class GcCompromisoFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compromiso_id", nullable = false)
    private GcCompromisoIngreso compromiso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    private GcFactura factura;

    /**
     * Monto de la factura imputado a este compromiso.
     * Debe ser > 0 y <= valorTotal de la factura.
     * La validación cross-factura (suma de aplicaciones <= valorTotal) es
     * responsabilidad del servicio.
     */
    @Column(name = "monto_aplicado", nullable = false, precision = 16, scale = 2)
    private BigDecimal montoAplicado;

    /**
     * Si la factura fue revertida (anulada), esta aplicación queda marcada
     * y se descuenta del acumulado. NO se elimina para preservar trazabilidad.
     */
    @Column(name = "revertida", nullable = false)
    private Boolean revertida = false;

    @Column(name = "fecha_reversion")
    private LocalDateTime fechaReversion;

    @Column(name = "motivo_reversion", length = 500)
    private String motivoReversion;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public GcCompromisoFactura() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    // ========== Getters y Setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GcCompromisoIngreso getCompromiso() { return compromiso; }
    public void setCompromiso(GcCompromisoIngreso compromiso) { this.compromiso = compromiso; }

    public GcFactura getFactura() { return factura; }
    public void setFactura(GcFactura factura) { this.factura = factura; }

    public BigDecimal getMontoAplicado() { return montoAplicado; }
    public void setMontoAplicado(BigDecimal montoAplicado) { this.montoAplicado = montoAplicado; }

    public Boolean getRevertida() { return revertida; }
    public void setRevertida(Boolean revertida) { this.revertida = revertida; }

    public LocalDateTime getFechaReversion() { return fechaReversion; }
    public void setFechaReversion(LocalDateTime fechaReversion) { this.fechaReversion = fechaReversion; }

    public String getMotivoReversion() { return motivoReversion; }
    public void setMotivoReversion(String motivoReversion) { this.motivoReversion = motivoReversion; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
