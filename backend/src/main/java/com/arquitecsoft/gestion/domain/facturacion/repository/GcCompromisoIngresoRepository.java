package com.arquitecsoft.gestion.domain.facturacion.repository;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoIngreso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio de compromisos de ingreso.
 *
 * Reemplaza al antiguo GcContratoFormaPagoRepository. Se mueve al paquete
 * domain/facturacion/repository porque la entidad ahora pertenece al dominio
 * de facturación, no al de contrato.
 */
@Repository
public interface GcCompromisoIngresoRepository extends JpaRepository<GcCompromisoIngreso, Long> {

    /**
     * Compromisos de un contrato ordenados por fecha esperada actual ascendente.
     * Útil para flujo de caja proyectado del contrato.
     */
    List<GcCompromisoIngreso> findByContratoIdOrderByFechaEsperadaActualAsc(Long contratoId);

    /**
     * Suma del monto presupuestado de todos los compromisos del contrato.
     * Reemplaza al antiguo sumValorByContratoId. El monto presupuestado es
     * inmutable — representa el valor original comprometido.
     */
    @Query("SELECT COALESCE(SUM(c.montoPresupuestado), 0) " +
           "FROM GcCompromisoIngreso c " +
           "WHERE c.contrato.id = :contratoId")
    BigDecimal sumMontoPresupuestadoByContratoId(@Param("contratoId") Long contratoId);

    /**
     * Suma del monto ya facturado acumulado de todos los compromisos del contrato.
     */
    @Query("SELECT COALESCE(SUM(c.montoFacturadoAcumulado), 0) " +
           "FROM GcCompromisoIngreso c " +
           "WHERE c.contrato.id = :contratoId")
    BigDecimal sumMontoFacturadoByContratoId(@Param("contratoId") Long contratoId);

    /**
     * Compromisos activos (no finales) cuya fecha esperada actual cae dentro
     * de un rango. Base para la vista de periodo (spec §6.2).
     */
    @Query("SELECT c FROM GcCompromisoIngreso c " +
           "WHERE c.fechaEsperadaActual BETWEEN :desde AND :hasta " +
           "AND c.estado NOT IN ('CUMPLIDO', 'NO_LOGRADO') " +
           "ORDER BY c.fechaEsperadaActual ASC")
    List<GcCompromisoIngreso> findActivosEnPeriodo(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);

    /**
     * Compromisos vencidos (fecha esperada actual en el pasado) y no finales.
     * Candidatos a reprogramación o pérdida.
     */
    @Query("SELECT c FROM GcCompromisoIngreso c " +
           "WHERE c.fechaEsperadaActual < :hoy " +
           "AND c.estado NOT IN ('CUMPLIDO', 'NO_LOGRADO') " +
           "ORDER BY c.fechaEsperadaActual ASC")
    List<GcCompromisoIngreso> findVencidos(@Param("hoy") LocalDate hoy);

    /**
     * Compromisos marcados en riesgo (flag enRiesgo = true) y aún activos.
     */
    @Query("SELECT c FROM GcCompromisoIngreso c " +
           "WHERE c.enRiesgo = true " +
           "AND c.estado NOT IN ('CUMPLIDO', 'NO_LOGRADO')")
    List<GcCompromisoIngreso> findEnRiesgoActivos();
}
