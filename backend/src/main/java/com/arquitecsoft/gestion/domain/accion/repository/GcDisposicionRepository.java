package com.arquitecsoft.gestion.domain.accion.repository;

import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion.OrigenAplicable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface GcDisposicionRepository extends JpaRepository<GcDisposicion, Long> {

    Optional<GcDisposicion> findByCodigo(String codigo);

    /**
     * Disposiciones activas aplicables a un origen. El service pasa el conjunto
     * { origenDeLaAccion, TODOS } para incluir las disposiciones genéricas (RB-MA-06).
     */
    List<GcDisposicion> findByActivoTrueAndOrigenAplicableIn(Collection<OrigenAplicable> origenes);
}
