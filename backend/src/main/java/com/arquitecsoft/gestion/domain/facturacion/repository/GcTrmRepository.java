package com.arquitecsoft.gestion.domain.facturacion.repository;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcTrm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcTrmRepository extends JpaRepository<GcTrm, Long> {

    Optional<GcTrm> findByAnioAndMesAndMonedaOrigenAndMonedaDestino(
            Integer anio, Integer mes, String monedaOrigen, String monedaDestino);

    @Query("SELECT t FROM GcTrm t WHERE t.monedaOrigen = :origen AND t.monedaDestino = :destino " +
           "AND (t.anio * 100 + t.mes) <= (:anio * 100 + :mes) ORDER BY t.anio DESC, t.mes DESC")
    List<GcTrm> findMostRecentBefore(@Param("anio") Integer anio, @Param("mes") Integer mes,
                                      @Param("origen") String origen, @Param("destino") String destino);

    @Query("SELECT t FROM GcTrm t WHERE t.monedaOrigen = :origen AND t.monedaDestino = :destino ORDER BY t.anio DESC, t.mes DESC")
    List<GcTrm> findAllByMonedaOrderDesc(@Param("origen") String origen, @Param("destino") String destino);
}
