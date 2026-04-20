package com.arquitecsoft.gestion.domain.facturacion.repository;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repositorio de aplicaciones de factura contra compromisos de ingreso.
 */
@Repository
public interface GcCompromisoFacturaRepository extends JpaRepository<GcCompromisoFactura, Long> {

    /**
     * Todas las aplicaciones (incluidas revertidas) de un compromiso.
     * Para cálculo de acumulado usar sólo las no revertidas.
     */
    @Query("SELECT cf FROM GcCompromisoFactura cf " +
           "JOIN FETCH cf.factura " +
           "WHERE cf.compromiso.id = :compromisoId " +
           "ORDER BY cf.fechaCreacion ASC")
    List<GcCompromisoFactura> findByCompromisoIdWithFactura(@Param("compromisoId") Long compromisoId);

    /**
     * Suma del monto aplicado NO revertido. Esta es la fuente de verdad para
     * recalcular montoFacturadoAcumulado en el compromiso.
     */
    @Query("SELECT COALESCE(SUM(cf.montoAplicado), 0) FROM GcCompromisoFactura cf " +
           "WHERE cf.compromiso.id = :compromisoId AND cf.revertida = false")
    BigDecimal sumAplicadoVigenteByCompromisoId(@Param("compromisoId") Long compromisoId);

    /**
     * Todas las aplicaciones de una factura — útil para validar que la suma
     * de aplicaciones no excede el valor total de la factura.
     */
    @Query("SELECT cf FROM GcCompromisoFactura cf WHERE cf.factura.id = :facturaId")
    List<GcCompromisoFactura> findByFacturaId(@Param("facturaId") Long facturaId);

    @Query("SELECT COALESCE(SUM(cf.montoAplicado), 0) FROM GcCompromisoFactura cf " +
           "WHERE cf.factura.id = :facturaId AND cf.revertida = false")
    BigDecimal sumAplicadoVigenteByFacturaId(@Param("facturaId") Long facturaId);
}
