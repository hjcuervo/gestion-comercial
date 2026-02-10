package com.arquitecsoft.gestion.domain.persona.repository;

import com.arquitecsoft.gestion.domain.persona.entity.GcPersonaEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcPersonaEmpresaRepository extends JpaRepository<GcPersonaEmpresa, Long> {

    @Query("SELECT pe FROM GcPersonaEmpresa pe WHERE pe.persona.id = :personaId")
    List<GcPersonaEmpresa> findByPersonaId(@Param("personaId") Long personaId);

    @Query("SELECT pe FROM GcPersonaEmpresa pe WHERE pe.empresa.id = :empresaId")
    List<GcPersonaEmpresa> findByEmpresaId(@Param("empresaId") Long empresaId);

    @Query("SELECT pe FROM GcPersonaEmpresa pe WHERE pe.persona.id = :personaId AND pe.empresa.id = :empresaId")
    Optional<GcPersonaEmpresa> findByPersonaIdAndEmpresaId(
            @Param("personaId") Long personaId, 
            @Param("empresaId") Long empresaId);

    boolean existsByPersonaIdAndEmpresaId(Long personaId, Long empresaId);
}
