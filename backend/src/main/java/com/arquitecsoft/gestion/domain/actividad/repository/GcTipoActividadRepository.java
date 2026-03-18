package com.arquitecsoft.gestion.domain.actividad.repository;

import com.arquitecsoft.gestion.domain.actividad.entity.GcTipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcTipoActividadRepository extends JpaRepository<GcTipoActividad, Long> {

    List<GcTipoActividad> findByActivoTrueOrderByNombreAsc();
}
