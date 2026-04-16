package com.arquitecsoft.gestion.domain.contrato.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_CONTRATO_FORMA_PAGO")
public class GcContratoFormaPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private GcContrato contrato;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Column(name = "valor", nullable = false, precision = 16, scale = 2)
    private BigDecimal valor;

    @Column(name = "valor_facturado", precision = 16, scale = 2)
    private BigDecimal valorFacturado;

    @Column(name = "fecha_estimada_pago")
    private LocalDate fechaEstimadaPago;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoFormaPago estado = EstadoFormaPago.PENDIENTE;

    @Column(name = "factura_id")
    private Long facturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_facturacion_id")
    private GcEmpresa empresaFacturacion;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum EstadoFormaPago {
        PENDIENTE, FACTURADA, PARCIAL, CANCELADA
    }

    public GcContratoFormaPago() {}

    @PrePersist
    protected void onCreate() { this.fechaCreacion = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.fechaModificacion = LocalDateTime.now(); }

    public boolean isVencida() {
        return this.estado == EstadoFormaPago.PENDIENTE && this.fechaEstimadaPago != null && this.fechaEstimadaPago.isBefore(LocalDate.now());
    }

    /** Diferencia entre valor facturado y presupuestado */
    public BigDecimal getDiferencia() {
        if (valorFacturado == null) return null;
        return valorFacturado.subtract(valor);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GcContrato getContrato() { return contrato; }
    public void setContrato(GcContrato contrato) { this.contrato = contrato; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getValorFacturado() { return valorFacturado; }
    public void setValorFacturado(BigDecimal valorFacturado) { this.valorFacturado = valorFacturado; }
    public LocalDate getFechaEstimadaPago() { return fechaEstimadaPago; }
    public void setFechaEstimadaPago(LocalDate fechaEstimadaPago) { this.fechaEstimadaPago = fechaEstimadaPago; }
    public EstadoFormaPago getEstado() { return estado; }
    public void setEstado(EstadoFormaPago estado) { this.estado = estado; }
    public Long getFacturaId() { return facturaId; }
    public void setFacturaId(Long facturaId) { this.facturaId = facturaId; }
    public GcEmpresa getEmpresaFacturacion() { return empresaFacturacion; }
    public void setEmpresaFacturacion(GcEmpresa empresaFacturacion) { this.empresaFacturacion = empresaFacturacion; }
    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }
    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
