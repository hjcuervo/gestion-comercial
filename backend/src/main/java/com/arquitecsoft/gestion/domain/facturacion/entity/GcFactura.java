package com.arquitecsoft.gestion.domain.facturacion.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GC_FACTURA", uniqueConstraints = @UniqueConstraint(columnNames = {"numero_factura", "prefijo"}))
public class GcFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private GcEmpresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_facturacion_id")
    private GcEmpresa empresaFacturacion;

    @Column(name = "numero_factura", nullable = false, length = 50)
    private String numeroFactura;

    @Column(name = "prefijo", length = 10)
    private String prefijo;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "valor_base", precision = 16, scale = 2)
    private BigDecimal valorBase;

    @Column(name = "valor_iva", precision = 16, scale = 2)
    private BigDecimal valorIva;

    @Column(name = "valor_total", nullable = false, precision = 16, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "COP";

    @Column(name = "factro_id", length = 50)
    private String factroId;

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public GcFactura() {}

    @PrePersist
    protected void onCreate() { this.fechaCreacion = LocalDateTime.now(); }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GcEmpresa getEmpresa() { return empresa; }
    public void setEmpresa(GcEmpresa empresa) { this.empresa = empresa; }
    public GcEmpresa getEmpresaFacturacion() { return empresaFacturacion; }
    public void setEmpresaFacturacion(GcEmpresa empresaFacturacion) { this.empresaFacturacion = empresaFacturacion; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }
    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public BigDecimal getValorBase() { return valorBase; }
    public void setValorBase(BigDecimal valorBase) { this.valorBase = valorBase; }
    public BigDecimal getValorIva() { return valorIva; }
    public void setValorIva(BigDecimal valorIva) { this.valorIva = valorIva; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getFactroId() { return factroId; }
    public void setFactroId(String factroId) { this.factroId = factroId; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
