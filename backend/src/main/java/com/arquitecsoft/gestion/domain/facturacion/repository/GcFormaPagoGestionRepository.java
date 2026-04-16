package com.arquitecsoft.gestion.domain.facturacion.repository;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcFormaPagoGestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcFormaPagoGestionRepository extends JpaRepository<GcFormaPagoGestion, Long> {

    @Query("SELECT g FROM GcFormaPagoGestion g WHERE g.formaPago.id = :formaPagoId ORDER BY g.fechaCreacion DESC")
    List<GcFormaPagoGestion> findByFormaPagoIdOrderByFechaCreacionDesc(@Param("formaPagoId") Long formaPagoId);

    @Query("SELECT g FROM GcFormaPagoGestion g WHERE g.formaPago.id = :formaPagoId ORDER BY g.fechaCreacion DESC FETCH FIRST 1 ROWS ONLY")
    GcFormaPagoGestion findLastByFormaPagoId(@Param("formaPagoId") Long formaPagoId);
}
