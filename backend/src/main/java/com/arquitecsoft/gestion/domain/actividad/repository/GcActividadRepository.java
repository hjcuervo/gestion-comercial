package com.arquitecsoft.gestion.domain.actividad.repository;

import com.arquitecsoft.gestion.domain.actividad.entity.GcActividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcActividadRepository extends JpaRepository<GcActividad, Long> {

    @Query("SELECT a FROM GcActividad a " +
           "JOIN FETCH a.oportunidad o " +
           "WHERE a.id = :id")
    Optional<GcActividad> findByIdWithOportunidad(@Param("id") Long id);

    @Query("SELECT a FROM GcActividad a " +
           "JOIN FETCH a.oportunidad o " +
           "WHERE a.oportunidad.id = :oportunidadId " +
           "ORDER BY a.fechaHora DESC")
    List<GcActividad> findByOportunidadId(@Param("oportunidadId") Long oportunidadId);

    @Query("SELECT a FROM GcActividad a " +
           "JOIN FETCH a.oportunidad o " +
           "WHERE (:oportunidadId IS NULL OR a.oportunidad.id = :oportunidadId) " +
           "ORDER BY a.fechaHora DESC")
    Page<GcActividad> findWithFilters(@Param("oportunidadId") Long oportunidadId, Pageable pageable);
}
