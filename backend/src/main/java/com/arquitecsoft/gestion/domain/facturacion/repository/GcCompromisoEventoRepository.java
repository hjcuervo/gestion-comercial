package com.arquitecsoft.gestion.domain.facturacion.repository;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio de eventos del sistema sobre compromisos de ingreso.
 *
 * Los eventos son inmutables — NO se proveen métodos de update o delete
 * intencionalmente. Spring Data JPA hereda save() y delete() de JpaRepository;
 * el servicio debe encapsular el acceso para prevenir borrados.
 */
@Repository
public interface GcCompromisoEventoRepository extends JpaRepository<GcCompromisoEvento, Long> {

    /**
     * Timeline completa del compromiso, evento más reciente primero.
     */
    @Query("SELECT e FROM GcCompromisoEvento e " +
           "WHERE e.compromiso.id = :compromisoId " +
           "ORDER BY e.fechaEvento DESC, e.id DESC")
    List<GcCompromisoEvento> findByCompromisoIdOrderByFechaDesc(
            @Param("compromisoId") Long compromisoId);

    /**
     * Timeline del compromiso filtrada por tipo de evento (útil para cálculos
     * específicos: por ejemplo, todas las FACTURA_REGISTRADA para reconstruir
     * el acumulado desde eventos).
     */
    @Query("SELECT e FROM GcCompromisoEvento e " +
           "WHERE e.compromiso.id = :compromisoId " +
           "AND e.tipoEvento = :tipoEvento " +
           "ORDER BY e.fechaEvento ASC")
    List<GcCompromisoEvento> findByCompromisoIdAndTipo(
            @Param("compromisoId") Long compromisoId,
            @Param("tipoEvento") GcCompromisoEvento.TipoEvento tipoEvento);

    /**
     * Último evento registrado para un compromiso — útil para saber qué acción
     * fue la más reciente antes de evaluar una transición.
     */
    @Query("SELECT e FROM GcCompromisoEvento e " +
           "WHERE e.compromiso.id = :compromisoId " +
           "ORDER BY e.fechaEvento DESC, e.id DESC " +
           "FETCH FIRST 1 ROWS ONLY")
    GcCompromisoEvento findLastByCompromisoId(@Param("compromisoId") Long compromisoId);

    /**
     * Compromisos sin eventos relevantes desde una fecha dada. Base para el
     * scheduler que dispara INACTIVIDAD_DETECTADA (spec §5.5 / §4.10).
     *
     * Devuelve IDs de compromisos activos cuyo evento más reciente es anterior
     * al corte indicado.
     */
    @Query("SELECT e.compromiso.id FROM GcCompromisoEvento e " +
           "WHERE e.compromiso.estado NOT IN ('CUMPLIDO', 'NO_LOGRADO') " +
           "GROUP BY e.compromiso.id " +
           "HAVING MAX(e.fechaEvento) < :corte")
    List<Long> findCompromisoIdsInactivosDesde(@Param("corte") LocalDateTime corte);

    /**
     * Cuenta total de reprogramaciones de un compromiso (métrica derivada
     * sugerida en spec §3.4).
     */
    @Query("SELECT COUNT(e) FROM GcCompromisoEvento e " +
           "WHERE e.compromiso.id = :compromisoId " +
           "AND e.tipoEvento = 'FECHA_REPROGRAMADA'")
    long countReprogramaciones(@Param("compromisoId") Long compromisoId);
}
