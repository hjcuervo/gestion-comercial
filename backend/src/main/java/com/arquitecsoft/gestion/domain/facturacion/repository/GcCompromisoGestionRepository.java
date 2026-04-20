package com.arquitecsoft.gestion.domain.facturacion.repository;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoGestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de notas libres del usuario sobre compromisos de ingreso.
 * Reemplaza al antiguo GcFormaPagoGestionRepository.
 *
 * Para eventos de sistema (máquina de estados), ver GcCompromisoEventoRepository.
 */
@Repository
public interface GcCompromisoGestionRepository extends JpaRepository<GcCompromisoGestion, Long> {

    @Query("SELECT g FROM GcCompromisoGestion g " +
           "WHERE g.compromiso.id = :compromisoId " +
           "ORDER BY g.fechaCreacion DESC")
    List<GcCompromisoGestion> findByCompromisoIdOrderByFechaCreacionDesc(
            @Param("compromisoId") Long compromisoId);

    @Query("SELECT g FROM GcCompromisoGestion g " +
           "WHERE g.compromiso.id = :compromisoId " +
           "ORDER BY g.fechaCreacion DESC " +
           "FETCH FIRST 1 ROWS ONLY")
    GcCompromisoGestion findLastByCompromisoId(@Param("compromisoId") Long compromisoId);
}
