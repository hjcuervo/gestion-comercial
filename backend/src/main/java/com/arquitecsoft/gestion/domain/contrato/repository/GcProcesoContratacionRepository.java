package com.arquitecsoft.gestion.domain.contrato.repository;

import com.arquitecsoft.gestion.domain.contrato.entity.GcProcesoContratacion;
import com.arquitecsoft.gestion.domain.contrato.entity.GcProcesoContratacion.EstadoProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcProcesoContratacionRepository extends JpaRepository<GcProcesoContratacion, Long> {

    @Query("SELECT p FROM GcProcesoContratacion p " +
           "JOIN FETCH p.oportunidad " +
           "JOIN FETCH p.empresa " +
           "JOIN FETCH p.pipeline " +
           "JOIN FETCH p.etapa " +
           "WHERE p.id = :id")
    Optional<GcProcesoContratacion> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT p FROM GcProcesoContratacion p " +
           "JOIN FETCH p.oportunidad " +
           "JOIN FETCH p.empresa " +
           "JOIN FETCH p.pipeline " +
           "JOIN FETCH p.etapa " +
           "WHERE p.oportunidad.id = :oportunidadId " +
           "ORDER BY p.fechaCreacion DESC")
    List<GcProcesoContratacion> findByOportunidadId(@Param("oportunidadId") Long oportunidadId);

    @Query("SELECT p FROM GcProcesoContratacion p " +
           "JOIN FETCH p.oportunidad " +
           "JOIN FETCH p.empresa " +
           "JOIN FETCH p.pipeline " +
           "JOIN FETCH p.etapa " +
           "WHERE p.oportunidad.id = :oportunidadId AND p.estado = :estado")
    Optional<GcProcesoContratacion> findByOportunidadIdAndEstado(
            @Param("oportunidadId") Long oportunidadId,
            @Param("estado") EstadoProceso estado);

    @Query("SELECT p FROM GcProcesoContratacion p " +
           "JOIN FETCH p.oportunidad " +
           "JOIN FETCH p.empresa " +
           "JOIN FETCH p.pipeline " +
           "JOIN FETCH p.etapa " +
           "WHERE p.estado = :estado " +
           "ORDER BY p.fechaInicio DESC")
    List<GcProcesoContratacion> findByEstado(@Param("estado") EstadoProceso estado);

    boolean existsByOportunidadIdAndEstado(Long oportunidadId, EstadoProceso estado);
}
