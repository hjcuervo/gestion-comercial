package com.arquitecsoft.gestion.domain.accion.repository;

import com.arquitecsoft.gestion.domain.accion.entity.GcAccionEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcAccionEventoRepository extends JpaRepository<GcAccionEvento, Long> {

    /** Historial cronológico del ciclo de vida de una acción. */
    List<GcAccionEvento> findByAccionIdOrderByFechaEventoAsc(Long accionId);
}
