package com.arquitecsoft.gestion.domain.catalogo.repository;

import com.arquitecsoft.gestion.domain.catalogo.entity.GcOrigen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcOrigenRepository extends JpaRepository<GcOrigen, String> {
    List<GcOrigen> findByActivoOrderByOrdenAscNombreAsc(Integer activo);
}
