package com.arquitecsoft.gestion.domain.catalogo.repository;

import com.arquitecsoft.gestion.domain.catalogo.entity.GcSector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcSectorRepository extends JpaRepository<GcSector, String> {
    List<GcSector> findByActivoOrderByOrdenAscNombreAsc(Integer activo);
}
