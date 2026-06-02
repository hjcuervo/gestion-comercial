package com.arquitecsoft.gestion.domain.accion.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Unidad de trabajo materializada del Motor de Acción.
 * Ver: GestCom_Spec_Motor_Accion_v1.md (§3 ciclo de vida) y modelo lógico §4.
 *
 * Referencias polimórficas (referenciaTipo/referenciaId y origenRefId) NO son
 * relaciones JPA: se manejan por tipo + id, sin FK, y se validan en la capa de
 * aplicación (ver modelo lógico §7). El usuario se referencia por Long (convención
 * de auditoría del proyecto), no como @ManyToOne.
 */
@Entity
@Table(name = "GC_ACCION")
public class GcAccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "responsable_id", nullable = false)
    private Long responsableId;

    @Column(name = "estado", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoAccion estado = EstadoAccion.PENDIENTE;

    @Column(name = "tipo_accion", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private TipoAccion tipoAccion;

    @Column(name = "prioridad", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Prioridad prioridad = Prioridad.MEDIA;

    @Column(name = "origen", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Origen origen;

    // Fuente generadora (paso de secuencia / criterio de etapa). Polimórfico, sin FK.
    @Column(name = "origen_ref_id")
    private Long origenRefId;

    @Column(name = "referencia_tipo", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ReferenciaTipo referenciaTipo;

    // Entidad trabajada (persona / empresa / oportunidad). Polimórfico, sin FK.
    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @Column(name = "disponible_desde", nullable = false)
    private LocalDateTime disponibleDesde;

    @Column(name = "vencimiento")
    private LocalDateTime vencimiento;

    // Disposición aplicada al completar (opcional; sin nullable=false por lección #2).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disposicion_id")
    private GcDisposicion disposicion;

    @Column(name = "motivo", length = 500)
    private String motivo;

    // Bloqueo optimista (concurrencia, §12 spec).
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // Auditoría (FK a usuario por Long, no string).
    @Column(name = "creado_por", nullable = false)
    private Long creadoPor;

    @Column(name = "modificado_por")
    private Long modificadoPor;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    public enum EstadoAccion { PENDIENTE, EN_CURSO, COMPLETADA, POSPUESTA, OMITIDA, INVALIDADA }
    public enum TipoAccion { LLAMADA, EMAIL, WHATSAPP, RED_SOCIAL, REUNION, ENVIO_PROPUESTA, TAREA_MANUAL }
    public enum Prioridad { ALTA, MEDIA, BAJA }
    public enum Origen { ENROLAMIENTO, FLUJO_ETAPA, MANUAL }
    public enum ReferenciaTipo { CONTACTO_PERSONA, CONTACTO_EMPRESA, OPORTUNIDAD }

    public GcAccion() {}

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.disponibleDesde == null) {
            this.disponibleDesde = this.fechaCreacion;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getResponsableId() { return responsableId; }
    public void setResponsableId(Long responsableId) { this.responsableId = responsableId; }

    public EstadoAccion getEstado() { return estado; }
    public void setEstado(EstadoAccion estado) { this.estado = estado; }

    public TipoAccion getTipoAccion() { return tipoAccion; }
    public void setTipoAccion(TipoAccion tipoAccion) { this.tipoAccion = tipoAccion; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public Origen getOrigen() { return origen; }
    public void setOrigen(Origen origen) { this.origen = origen; }

    public Long getOrigenRefId() { return origenRefId; }
    public void setOrigenRefId(Long origenRefId) { this.origenRefId = origenRefId; }

    public ReferenciaTipo getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(ReferenciaTipo referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public LocalDateTime getDisponibleDesde() { return disponibleDesde; }
    public void setDisponibleDesde(LocalDateTime disponibleDesde) { this.disponibleDesde = disponibleDesde; }

    public LocalDateTime getVencimiento() { return vencimiento; }
    public void setVencimiento(LocalDateTime vencimiento) { this.vencimiento = vencimiento; }

    public GcDisposicion getDisposicion() { return disposicion; }
    public void setDisposicion(GcDisposicion disposicion) { this.disposicion = disposicion; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    public Long getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Long creadoPor) { this.creadoPor = creadoPor; }

    public Long getModificadoPor() { return modificadoPor; }
    public void setModificadoPor(Long modificadoPor) { this.modificadoPor = modificadoPor; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime fechaModificacion) { this.fechaModificacion = fechaModificacion; }
}
