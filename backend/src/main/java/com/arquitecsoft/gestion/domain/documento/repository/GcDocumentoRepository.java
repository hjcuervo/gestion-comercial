package com.arquitecsoft.gestion.domain.documento.repository;

import com.arquitecsoft.gestion.domain.documento.entity.GcDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcDocumentoRepository extends JpaRepository<GcDocumento, Long> {

    @Query("SELECT d FROM GcDocumento d WHERE d.oportunidad.id = :oportunidadId ORDER BY d.fechaCarga DESC")
    List<GcDocumento> findByOportunidadId(@Param("oportunidadId") Long oportunidadId);

    @Query("SELECT d FROM GcDocumento d WHERE d.actividad.id = :actividadId ORDER BY d.fechaCarga DESC")
    List<GcDocumento> findByActividadId(@Param("actividadId") Long actividadId);

    @Query("SELECT d FROM GcDocumento d WHERE " +
           "(:oportunidadId IS NULL OR d.oportunidad.id = :oportunidadId) AND " +
           "(:actividadId IS NULL OR d.actividad.id = :actividadId) AND " +
           "(:tipoDocumentoId IS NULL OR d.tipoDocumentoId = :tipoDocumentoId)")
    Page<GcDocumento> findWithFilters(
            @Param("oportunidadId") Long oportunidadId,
            @Param("actividadId") Long actividadId,
            @Param("tipoDocumentoId") Long tipoDocumentoId,
            Pageable pageable);

    @Query("SELECT COUNT(d) FROM GcDocumento d WHERE d.oportunidad.id = :oportunidadId")
    long countByOportunidadId(@Param("oportunidadId") Long oportunidadId);
}
