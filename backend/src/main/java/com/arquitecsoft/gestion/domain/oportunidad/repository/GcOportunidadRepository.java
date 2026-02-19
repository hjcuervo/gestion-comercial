package com.arquitecsoft.gestion.domain.oportunidad.repository;

import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad;
import com.arquitecsoft.gestion.domain.oportunidad.entity.GcOportunidad.EstadoMacro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GcOportunidadRepository extends JpaRepository<GcOportunidad, Long> {

    @Query("SELECT o FROM GcOportunidad o " +
           "JOIN FETCH o.empresa " +
           "JOIN FETCH o.pipeline " +
           "JOIN FETCH o.etapa " +
           "WHERE o.id = :id")
    Optional<GcOportunidad> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT o FROM GcOportunidad o " +
           "JOIN FETCH o.empresa " +
           "JOIN FETCH o.pipeline " +
           "JOIN FETCH o.etapa " +
           "WHERE (:nombre IS NULL OR LOWER(o.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:empresaId IS NULL OR o.empresa.id = :empresaId) " +
           "AND (:pipelineId IS NULL OR o.pipeline.id = :pipelineId) " +
           "AND (:etapaId IS NULL OR o.etapa.id = :etapaId) " +
           "AND (:estadoMacro IS NULL OR o.estadoMacro = :estadoMacro)")
    Page<GcOportunidad> findWithFilters(
            @Param("nombre") String nombre,
            @Param("empresaId") Long empresaId,
            @Param("pipelineId") Long pipelineId,
            @Param("etapaId") Long etapaId,
            @Param("estadoMacro") EstadoMacro estadoMacro,
            Pageable pageable);

    @Query("SELECT COUNT(o) FROM GcOportunidad o WHERE o.empresa.id = :empresaId AND o.estadoMacro IN ('ABIERTA', 'SEGUIMIENTO')")
    long countOportunidadesActivasByEmpresa(@Param("empresaId") Long empresaId);

    @Query("SELECT COUNT(o) FROM GcOportunidad o WHERE o.etapa.id = :etapaId AND o.estadoMacro IN ('ABIERTA', 'SEGUIMIENTO')")
    long countOportunidadesActivasByEtapa(@Param("etapaId") Long etapaId);
}
