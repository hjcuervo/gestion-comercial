package com.arquitecsoft.gestion.domain.actividad.repository;

import com.arquitecsoft.gestion.domain.actividad.entity.GcCompromiso;
import com.arquitecsoft.gestion.domain.actividad.entity.GcCompromiso.EstadoCompromiso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcCompromisoRepository extends JpaRepository<GcCompromiso, Long> {

    @Query("SELECT c FROM GcCompromiso c " +
           "JOIN FETCH c.actividad a " +
           "WHERE c.id = :id")
    Optional<GcCompromiso> findByIdWithActividad(@Param("id") Long id);

    @Query("SELECT c FROM GcCompromiso c WHERE c.actividad.id = :actividadId ORDER BY c.fechaCompromiso")
    List<GcCompromiso> findByActividadId(@Param("actividadId") Long actividadId);

    @Query("SELECT c FROM GcCompromiso c " +
           "JOIN FETCH c.actividad a " +
           "WHERE (:actividadId IS NULL OR c.actividad.id = :actividadId) " +
           "AND (:estado IS NULL OR c.estado = :estado) " +
           "ORDER BY c.fechaCompromiso")
    Page<GcCompromiso> findWithFilters(
            @Param("actividadId") Long actividadId,
            @Param("estado") EstadoCompromiso estado,
            Pageable pageable);

    @Query("SELECT c FROM GcCompromiso c " +
           "JOIN c.actividad a " +
           "WHERE a.oportunidad.id = :oportunidadId AND c.estado IN ('PENDIENTE', 'EN_PROGRESO') " +
           "ORDER BY c.fechaCompromiso")
    List<GcCompromiso> findPendientesByOportunidadId(@Param("oportunidadId") Long oportunidadId);
}
