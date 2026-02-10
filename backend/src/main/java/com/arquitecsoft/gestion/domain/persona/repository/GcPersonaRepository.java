package com.arquitecsoft.gestion.domain.persona.repository;

import com.arquitecsoft.gestion.domain.persona.entity.GcPersona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GcPersonaRepository extends JpaRepository<GcPersona, Long> {

    @Query("SELECT p FROM GcPersona p WHERE " +
           "(:nombre IS NULL OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "p.activo = 1")
    Page<GcPersona> findWithFilters(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT DISTINCT p FROM GcPersona p JOIN p.empresas pe WHERE pe.empresa.id = :empresaId AND p.activo = 1")
    Page<GcPersona> findByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);

    Optional<GcPersona> findByEmail(String email);

    boolean existsByEmail(String email);
}
