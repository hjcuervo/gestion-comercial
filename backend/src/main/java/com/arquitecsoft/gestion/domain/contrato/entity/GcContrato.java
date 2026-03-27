package com.arquitecsoft.gestion.domain.contrato.entity;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GC_CONTRATO")
public class GcContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proceso_contratacion_id", nullable = false)
    private GcProcesoContratacion procesoContratacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id", nullable = false)
    private GcOportunidad oportunidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private GcEmpresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_facturacion_id")
    private GcEmpresa empresaFacturacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_contrato_id", nullable = false)
    private GcTipoContrato tipoContrato;

    @Column(name = "numero_contrato_interno", length = 50)
    private String numeroContratoInterno;

    @Column(name = "numero_contrato_cliente", length = 100)
    private String numeroContratoCliente;

    @Column(name = "objeto", length = 1000)
    private String objeto;

    @Column(name = "moneda", length = 3)
    private String moneda = "COP";

    @Column(name = "valor_contrato", precision = 16, scale = 2)
    private BigDecimal valorContrato;

    @Column(name = "valor_ajuste", precision = 16, scale = 2)
    private BigDecimal valorAjuste = BigDecimal.ZERO;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoContrato estado = EstadoContrato.VIGENTE;

    @Column(name = "responsable_gestion", length = 100)
    private String responsableGestion;

    @Column(name = "interventor", length = 200)
    private String interventor;

    @Column(name = "observaciones", length = 2000)
    private String observaciones;

    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
    @OrderBy("fechaEstimadaPago ASC")
    private List<GcContratoFormaPago> formasPago = new ArrayList<>();

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL)
    @OrderBy("fechaModificacion DESC")
    private List<GcContratoModificacion> modificaciones = new ArrayList<>();

    public enum EstadoContrato {
        VIGENTE, SUSPENDIDO, TERMINADO, LIQUIDADO
    }

    public GcContrato() {}

    @PrePersist
    protected void onCreate() { this.fechaCreacion = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.fechaModificacion = LocalDateTime.now(); }

    public boolean isVigente() { return this.estado == EstadoContrato.VIGENTE; }
    public boolean isModificable() { return this.estado == EstadoContrato.VIGENTE; }

    public BigDecimal getValorTotal() {
        BigDecimal base = valorContrato != null ? valorContrato : BigDecimal.ZERO;
        BigDecimal ajuste = valorAjuste != null ? valorAjuste : BigDecimal.ZERO;
        return base.add(ajuste);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public GcProcesoContratacion getProcesoContratacion() { return procesoContratacion; }
    public void setProcesoContratacion(GcProcesoContratacion procesoContratacion) { this.procesoContratacion = procesoContratacion; }
    public GcOportunidad getOportunidad() { return oportunidad; }
    public void setOportunidad(GcOportunidad oportunidad) { this.oportunidad = oportunidad; }
    public GcEmpresa getEmpresa() { return empresa; }
    public void setEmpresa(GcEmpresa empresa) { this.empresa = empresa; }
    public GcEmpresa getEmpresaFacturacion() { return empresaFacturacion; }
    public void setEmpresaFacturacion(GcEmpresa empresaFacturacion) { this.empresaFacturacion = empresaFacturacion; }
    public GcTipoContrato getTipoContrato() { return tipoContrato; }
    public void setTipoContrato(GcTipoContrato tipoContrato) { this.tipoContrato = tipoContrato; }
    public String getNumeroContratoInterno() { return numeroContratoInterno; }
    public void setNumeroContratoInterno(String numeroContratoInterno) { this.numeroContratoInterno = numeroContratoInterno; }
    public String getNumeroContratoCliente() { return numeroContratoCliente; }
    public void setNumeroContratoCliente(String numeroContratoCliente) { this.numeroContratoCliente = numeroContratoCliente; }
    public String getObjeto() { return objeto; }
    public void setObjeto(String objeto) { this.objeto = objeto; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public BigDecimal getValorContrato() { return valorContrato; }
    public void setValorContrato(BigDecimal valorContrato) { this.valorContrato = valorContrato; }
    public BigDecimal getValorAjuste() { return valorAjuste; }
    public void setValorAjuste(BigDecimal valorAjuste) { this.valorAjuste = valorAjuste; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public EstadoContrato getEstado() { return estado; }
    public void setEstado(EstadoContrato estado) { this.estado = estado; }
    public String getResponsableGestion() { return responsableGestion; }
    public void setResponsableGestion(String responsableGestion) { this.responsableGestion = responsableGestion; }
    public String getInterventor() { return interventor; }
    public void setInterventor(String interventor) { this.interventor = interventor; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }
    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public List<GcContratoFormaPago> getFormasPago() { return formasPago; }
    public void setFormasPago(List<GcContratoFormaPago> formasPago) { this.formasPago = formasPago; }
    public List<GcContratoModificacion> getModificaciones() { return modificaciones; }
    public void setModificaciones(List<GcContratoModificacion> modificaciones) { this.modificaciones = modificaciones; }
}
