package com.arquitecsoft.gestion.domain.contrato.repository;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoFormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface GcContratoFormaPagoRepository extends JpaRepository<GcContratoFormaPago, Long> {

    List<GcContratoFormaPago> findByContratoIdOrderByFechaEstimadaPagoAsc(Long contratoId);

    @Query("SELECT COALESCE(SUM(fp.valor), 0) FROM GcContratoFormaPago fp WHERE fp.contrato.id = :contratoId")
    BigDecimal sumValorByContratoId(@Param("contratoId") Long contratoId);
}
