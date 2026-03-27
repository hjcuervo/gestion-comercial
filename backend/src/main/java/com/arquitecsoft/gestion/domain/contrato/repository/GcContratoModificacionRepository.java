package com.arquitecsoft.gestion.domain.contrato.repository;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContratoModificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcContratoModificacionRepository extends JpaRepository<GcContratoModificacion, Long> {

    List<GcContratoModificacion> findByContratoIdOrderByFechaModificacionContratoDesc(Long contratoId);
}
