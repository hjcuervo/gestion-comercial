package com.arquitecsoft.gestion.domain.pipeline.repository;

import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline;
import com.arquitecsoft.gestion.domain.pipeline.entity.GcPipeline.EstadoPipeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcPipelineRepository extends JpaRepository<GcPipeline, Long> {

    @Query("SELECT p FROM GcPipeline p WHERE " +
           "(:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "(:estado IS NULL OR p.estado = :estado)")
    Page<GcPipeline> findWithFilters(
            @Param("nombre") String nombre,
            @Param("estado") EstadoPipeline estado,
            Pageable pageable);

    @Query("SELECT p FROM GcPipeline p WHERE p.estado = 'ACTIVO'")
    List<GcPipeline> findAllActivos();

    @Query("SELECT p FROM GcPipeline p LEFT JOIN FETCH p.etapas e WHERE p.id = :id ORDER BY e.orden")
    Optional<GcPipeline> findByIdWithEtapas(@Param("id") Long id);

    boolean existsByNombreIgnoreCase(String nombre);
}
