package com.arquitecsoft.gestion.domain.facturacion.repository;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcFactura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GcFacturaRepository extends JpaRepository<GcFactura, Long> {

    @Query("SELECT f FROM GcFactura f " +
           "JOIN FETCH f.empresa " +
           "LEFT JOIN FETCH f.empresaFacturacion " +
           "WHERE f.id = :id")
    Optional<GcFactura> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT f FROM GcFactura f " +
           "JOIN FETCH f.empresa " +
           "WHERE (:empresaId IS NULL OR f.empresa.id = :empresaId) " +
           "AND (:moneda IS NULL OR f.moneda = :moneda)")
    Page<GcFactura> findWithFilters(
            @Param("empresaId") Long empresaId,
            @Param("moneda") String moneda,
            Pageable pageable);

    Optional<GcFactura> findByNumeroFacturaAndPrefijo(String numeroFactura, String prefijo);
}
