package com.arquitecsoft.gestion.domain.facturacion.entity;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Compromiso de Ingreso.
 *
 * Unidad mínima de control del módulo de Presupuesto vs Ejecución Comercial.
 * Representa un ingreso esperado (presupuestado) que debe ser gestionado hasta
 * su resolución final (CUMPLIDO o NO_LOGRADO).
 *
 * Reglas de persistencia:
 *  - Nunca se elimina.
 *  - El monto presupuestado original es inmutable.
 *  - Los estados finales (CUMPLIDO, NO_LOGRADO) no admiten salida.
 *  - Toda diferencia entre presupuesto y ejecución debe permanecer visible.
 *
 * Relaciones con factura: N:N a través de GcCompromisoFactura.
 *
 * NOTA: En esta versión de la plataforma NO se modela un responsable dedicado;
 * la gestión del compromiso la lleva el equipo de facturación de forma
 * colectiva. Si en el futuro se requiere asignar responsable individual,
 * se agregará como cambio aditivo.
 */
@Entity
@Table(name = "GC_COMPROMISO_INGRESO")
public class GcCompromisoIngreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private GcContrato contrato;

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    /**
     * Valor original comprometido. INMUTABLE tras creación.
     * Cualquier ajuste de expectativa va contra montoEsperadoActual.
     */
    @Column(name = "monto_presupuestado", nullable = false, precision = 16, scale = 2, updatable = false)
    private BigDecimal montoPresupuestado;

    /**
     * Expectativa actual del ingreso. Puede diferir del presupuestado original
     * vía evento MontoAjustado. No cambia el presupuesto original.
     */
    @Column(name = "monto_esperado_actual", nullable = false, precision = 16, scale = 2)
    private BigDecimal montoEsperadoActual;

    /**
     * Suma de montos aplicados desde facturas (vía GcCompromisoFactura).
     * Mantenido por el servicio al procesar eventos FacturaRegistrada / FacturaRevertida.
     */
    @Column(name = "monto_facturado_acumulado", nullable = false, precision = 16, scale = 2)
    private BigDecimal montoFacturadoAcumulado = BigDecimal.ZERO;

    /**
     * Fecha inicial comprometida. INMUTABLE tras creación.
     */
    @Column(name = "fecha_esperada_original", nullable = false, updatable = false)
    private LocalDate fechaEsperadaOriginal;

    /**
     * Fecha actual proyectada. Se actualiza vía evento FechaReprogramada.
     * Es esta fecha la que determina la pertenencia del compromiso a un periodo.
     */
    @Column(name = "fecha_esperada_actual", nullable = false)
    private LocalDate fechaEsperadaActual;

    @Column(name = "estado", nullable = false, length = 25)
    @Enumerated(EnumType.STRING)
    private EstadoCompromiso estado = EstadoCompromiso.PENDIENTE_GESTION;

    @Column(name = "tipo", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private TipoCompromiso tipo = TipoCompromiso.NUEVO;

    /**
     * Flag de riesgo — independiente del estado.
     * Marcado por evento InactividadDetectada. No afecta la máquina de estados.
     */
    @Column(name = "en_riesgo", nullable = false)
    private Boolean enRiesgo = false;

    /**
     * Fecha de la última actividad relevante (gestión o evento).
     * Base para el cálculo de inactividad.
     */
    @Column(name = "fecha_ultima_actividad")
    private LocalDateTime fechaUltimaActividad;

    /**
     * Causa de pérdida. Obligatorio si estado = NO_LOGRADO.
     * Validación a nivel de servicio (no se puede forzar a nivel de columna
     * porque puede ser null durante el ciclo de vida normal).
     */
    @Column(name = "motivo_perdida", length = 500)
    private String motivoPerdida;

    /**
     * Relación de reemplazo. Si este compromiso reemplaza a otro que se perdió,
     * apunta al original. Permite trazar "derivaciones" de ingreso.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reemplaza_a_id")
    private GcCompromisoIngreso reemplazaA;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "COP";

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /**
     * Estados del compromiso según spec §4.
     *  PENDIENTE_GESTION  → inicial
     *  EN_GESTION         → el equipo de facturación lo está trabajando
     *  COMPROMETIDO       → acuerdo comercial alcanzado
     *  PARCIALMENTE_CUMPLIDO → facturado > 0 y < presupuestado
     *  CUMPLIDO           → final. facturado >= presupuestado
     *  REPROGRAMADO       → no se cumplió en fecha, nueva fecha esperada
     *  NO_LOGRADO         → final. no se ejecutará
     */
    public enum EstadoCompromiso {
        PENDIENTE_GESTION,
        EN_GESTION,
        COMPROMETIDO,
        PARCIALMENTE_CUMPLIDO,
        CUMPLIDO,
        REPROGRAMADO,
        NO_LOGRADO
    }

    public enum TipoCompromiso {
        NUEVO,
        RECURRENTE,
        RENOVACION,
        REEMPLAZO
    }

    public GcCompromisoIngreso() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.montoEsperadoActual == null) {
            this.montoEsperadoActual = this.montoPresupuestado;
        }
        if (this.fechaEsperadaActual == null) {
            this.fechaEsperadaActual = this.fechaEsperadaOriginal;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // ========== Atributos derivados ==========

    /**
     * Saldo pendiente = monto presupuestado - monto facturado acumulado.
     * Nunca negativo en la representación (la sobre-ejecución se modela aparte).
     */
    @Transient
    public BigDecimal getSaldoPendiente() {
        if (montoPresupuestado == null || montoFacturadoAcumulado == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal saldo = montoPresupuestado.subtract(montoFacturadoAcumulado);
        return saldo.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : saldo;
    }

    /**
     * Porcentaje de cumplimiento = facturado / presupuestado.
     * Puede exceder 1.0 en sobre-ejecución.
     */
    @Transient
    public BigDecimal getPorcentajeCumplimiento() {
        if (montoPresupuestado == null || montoPresupuestado.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return montoFacturadoAcumulado.divide(montoPresupuestado, 4, java.math.RoundingMode.HALF_UP);
    }

    @Transient
    public boolean isEstadoFinal() {
        return estado == EstadoCompromiso.CUMPLIDO || estado == EstadoCompromiso.NO_LOGRADO;
    }

    @Transient
    public boolean isVencido() {
        return !isEstadoFinal()
                && fechaEsperadaActual != null
                && fechaEsperadaActual.isBefore(LocalDate.now());
    }

    // ========== Getters y Setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public GcContrato getContrato() { return contrato; }
    public void setContrato(GcContrato contrato) { this.contrato = contrato; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getMontoPresupuestado() { return montoPresupuestado; }
    public void setMontoPresupuestado(BigDecimal montoPresupuestado) { this.montoPresupuestado = montoPresupuestado; }

    public BigDecimal getMontoEsperadoActual() { return montoEsperadoActual; }
    public void setMontoEsperadoActual(BigDecimal montoEsperadoActual) { this.montoEsperadoActual = montoEsperadoActual; }

    public BigDecimal getMontoFacturadoAcumulado() { return montoFacturadoAcumulado; }
    public void setMontoFacturadoAcumulado(BigDecimal montoFacturadoAcumulado) { this.montoFacturadoAcumulado = montoFacturadoAcumulado; }

    public LocalDate getFechaEsperadaOriginal() { return fechaEsperadaOriginal; }
    public void setFechaEsperadaOriginal(LocalDate fechaEsperadaOriginal) { this.fechaEsperadaOriginal = fechaEsperadaOriginal; }

    public LocalDate getFechaEsperadaActual() { return fechaEsperadaActual; }
    public void setFechaEsperadaActual(LocalDate fechaEsperadaActual) { this.fechaEsperadaActual = fechaEsperadaActual; }

    public EstadoCompromiso getEstado() { return estado; }
    public void setEstado(EstadoCompromiso estado) { this.estado = estado; }

    public TipoCompromiso getTipo() { return tipo; }
    public void setTipo(TipoCompromiso tipo) { this.tipo = tipo; }

    public Boolean getEnRiesgo() { return enRiesgo; }
    public void setEnRiesgo(Boolean enRiesgo) { this.enRiesgo = enRiesgo; }

    public LocalDateTime getFechaUltimaActividad() { return fechaUltimaActividad; }
    public void setFechaUltimaActividad(LocalDateTime fechaUltimaActividad) { this.fechaUltimaActividad = fechaUltimaActividad; }

    public String getMotivoPerdida() { return motivoPerdida; }
    public void setMotivoPerdida(String motivoPerdida) { this.motivoPerdida = motivoPerdida; }

    public GcCompromisoIngreso getReemplazaA() { return reemplazaA; }
    public void setReemplazaA(GcCompromisoIngreso reemplazaA) { this.reemplazaA = reemplazaA; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
}
