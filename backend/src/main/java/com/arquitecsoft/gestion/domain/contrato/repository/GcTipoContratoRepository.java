package com.arquitecsoft.gestion.domain.contrato.repository;

import com.arquitecsoft.gestion.domain.contrato.entity.GcTipoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcTipoContratoRepository extends JpaRepository<GcTipoContrato, Long> {

    List<GcTipoContrato> findByActivoTrueOrderByNombreAsc();
}
