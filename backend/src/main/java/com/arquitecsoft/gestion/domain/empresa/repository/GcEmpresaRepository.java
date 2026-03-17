package com.arquitecsoft.gestion.domain.empresa.repository;

import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa;
import com.arquitecsoft.gestion.domain.empresa.entity.GcEmpresa.EstadoEmpresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GcEmpresaRepository extends JpaRepository<GcEmpresa, Long> {

    @Query("SELECT e FROM GcEmpresa e WHERE " +
           "(:razonSocial IS NULL OR LOWER(e.razonSocial) LIKE LOWER(CONCAT('%', :razonSocial, '%'))) AND " +
           "(:estado IS NULL OR e.estado = :estado)")
    Page<GcEmpresa> findWithFilters(
            @Param("razonSocial") String razonSocial,
            @Param("estado") EstadoEmpresa estado,
            Pageable pageable);

    Optional<GcEmpresa> findByIdentificacionTributaria(String identificacionTributaria);

    boolean existsByIdentificacionTributaria(String identificacionTributaria);

    boolean existsByRazonSocialIgnoreCase(String razonSocial);
}
