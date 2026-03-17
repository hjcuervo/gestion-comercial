package com.arquitecsoft.gestion.domain.empresa.repository;

import com.arquitecsoft.gestion.domain.empresa.entity.GcDepartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GcDepartamentoRepository extends JpaRepository<GcDepartamento, String> {
    List<GcDepartamento> findAllByOrderByDescripcionAsc();
}
