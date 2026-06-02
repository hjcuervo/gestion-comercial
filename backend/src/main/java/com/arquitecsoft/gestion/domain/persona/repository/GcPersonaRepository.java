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

    /**
     * Detalle con relaciones cargadas para serializar sin lazy fuera de sesión (lección #4).
     * `empresas` es la única colección (bag); el resto son @ManyToOne, así que no hay
     * MultipleBagFetchException (lección #6). DISTINCT para deduplicar el root por el JOIN al bag.
     */
    @Query("SELECT DISTINCT p FROM GcPersona p " +
           "LEFT JOIN FETCH p.origen " +
           "LEFT JOIN FETCH p.reportaA " +
           "LEFT JOIN FETCH p.empresas pe " +
           "LEFT JOIN FETCH pe.empresa " +
           "WHERE p.id = :id")
    Optional<GcPersona> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT p FROM GcPersona p WHERE " +
           "(:nombre IS NULL OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE LOWER(CONCAT('%', :nombre, '%'))) AND " +
           "p.activo = 1")
    Page<GcPersona> findWithFilters(@Param("nombre") String nombre, Pageable pageable);

    @Query("SELECT DISTINCT p FROM GcPersona p JOIN p.empresas pe WHERE pe.empresa.id = :empresaId AND p.activo = 1")
    Page<GcPersona> findByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);
}
