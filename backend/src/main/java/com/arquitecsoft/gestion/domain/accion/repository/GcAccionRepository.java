package com.arquitecsoft.gestion.domain.accion.repository;

import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion.EstadoAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion.ReferenciaTipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GcAccionRepository extends JpaRepository<GcAccion, Long> {

    /**
     * Cola de trabajo del usuario: acciones PENDIENTE ya disponibles, ordenadas según
     * RB-MA (§6 spec): (1) vencidas primero, (2) prioridad ALTA &gt; MEDIA &gt; BAJA,
     * (3) vencimiento ascendente (nulls last), (4) id ascendente.
     *
     * Nativa por el ORDER BY condicional sobre los valores de enum. La dosificación
     * (topeVisible) se aplica vía Pageable desde el service:
     * {@code PageRequest.of(0, topeVisible)} devuelve "Hoy"; el resto es "En espera".
     */
    @Query(value =
        "SELECT * FROM GC_ACCION a " +
        "WHERE a.responsable_id = :responsableId " +
        "  AND a.estado = 'PENDIENTE' " +
        "  AND a.disponible_desde <= :ahora " +
        "ORDER BY " +
        "  CASE WHEN a.vencimiento IS NOT NULL AND a.vencimiento < :ahora THEN 0 ELSE 1 END, " +
        "  CASE a.prioridad WHEN 'ALTA' THEN 0 WHEN 'MEDIA' THEN 1 ELSE 2 END, " +
        "  a.vencimiento ASC NULLS LAST, " +
        "  a.id ASC",
        countQuery =
        "SELECT count(*) FROM GC_ACCION a " +
        "WHERE a.responsable_id = :responsableId " +
        "  AND a.estado = 'PENDIENTE' " +
        "  AND a.disponible_desde <= :ahora",
        nativeQuery = true)
    Page<GcAccion> findColaPendiente(@Param("responsableId") Long responsableId,
                                     @Param("ahora") LocalDateTime ahora,
                                     Pageable pageable);

    /** Detalle con la disposición resuelta (para el response al completar una acción). */
    @Query("SELECT a FROM GcAccion a LEFT JOIN FETCH a.disposicion WHERE a.id = :id")
    Optional<GcAccion> findByIdWithRelations(@Param("id") Long id);

    /**
     * Reconciliación (§9 spec): acciones no terminales que referencian una entidad que
     * cambió de estado, para invalidarlas. El service pasa los estados no terminales
     * (PENDIENTE, EN_CURSO, POSPUESTA).
     */
    List<GcAccion> findByReferenciaTipoAndReferenciaIdAndEstadoIn(
            ReferenciaTipo referenciaTipo, Long referenciaId, Collection<EstadoAccion> estados);

    long countByResponsableIdAndEstado(Long responsableId, EstadoAccion estado);
}
