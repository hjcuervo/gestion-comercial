package com.arquitecsoft.gestion.domain.pipeline.repository;

import com.arquitecsoft.gestion.domain.pipeline.entity.GcEtapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcEtapaRepository extends JpaRepository<GcEtapa, Long> {

    @Query("SELECT e FROM GcEtapa e WHERE e.pipeline.id = :pipelineId ORDER BY e.orden")
    List<GcEtapa> findByPipelineIdOrderByOrden(@Param("pipelineId") Long pipelineId);

    @Query("SELECT e FROM GcEtapa e WHERE e.pipeline.id = :pipelineId AND e.estado = 'ACTIVA' ORDER BY e.orden")
    List<GcEtapa> findActivasByPipelineId(@Param("pipelineId") Long pipelineId);

    @Query("SELECT MAX(e.orden) FROM GcEtapa e WHERE e.pipeline.id = :pipelineId")
    Integer findMaxOrdenByPipelineId(@Param("pipelineId") Long pipelineId);

    boolean existsByPipelineIdAndNombreIgnoreCase(Long pipelineId, String nombre);
}
