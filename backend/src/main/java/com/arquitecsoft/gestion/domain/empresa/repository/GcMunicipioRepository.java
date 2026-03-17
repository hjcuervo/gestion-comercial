package com.arquitecsoft.gestion.domain.empresa.repository;

import com.arquitecsoft.gestion.domain.empresa.entity.GcMunicipio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GcMunicipioRepository extends JpaRepository<GcMunicipio, String> {
    List<GcMunicipio> findByCodigoDepartamentoOrderByDescripcionAsc(String codigoDepartamento);
    List<GcMunicipio> findAllByOrderByDescripcionAsc();
}
