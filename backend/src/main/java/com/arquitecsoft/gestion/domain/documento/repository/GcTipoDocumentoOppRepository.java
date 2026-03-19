package com.arquitecsoft.gestion.domain.documento.repository;

import com.arquitecsoft.gestion.domain.documento.entity.GcTipoDocumentoOpp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GcTipoDocumentoOppRepository extends JpaRepository<GcTipoDocumentoOpp, Long> {

    List<GcTipoDocumentoOpp> findByActivoTrueOrderByNombreAsc();
}
