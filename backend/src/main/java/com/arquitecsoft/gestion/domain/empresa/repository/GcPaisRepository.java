package com.arquitecsoft.gestion.domain.empresa.repository;

import com.arquitecsoft.gestion.domain.empresa.entity.GcPais;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GcPaisRepository extends JpaRepository<GcPais, String> {
    List<GcPais> findAllByOrderByNombreAsc();
}
