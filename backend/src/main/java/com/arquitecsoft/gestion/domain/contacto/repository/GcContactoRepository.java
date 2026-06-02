package com.arquitecsoft.gestion.domain.contacto.repository;

import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto;
import com.arquitecsoft.gestion.domain.contacto.entity.GcContacto.TipoCanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcContactoRepository extends JpaRepository<GcContacto, Long> {

    /**
     * Detalle con la red social cargada (única relación serializada que es LAZY
     * y se necesita fuera de transacción).
     */
    @Query("SELECT c FROM GcContacto c LEFT JOIN FETCH c.redSocial WHERE c.id = :id")
    Optional<GcContacto> findByIdWithRelations(@Param("id") Long id);

    /**
     * Contactos de una empresa, con filtros opcionales por tipo de canal y por
     * inclusión de inactivos. Orden: principales primero, luego por tipo.
     */
    @Query("SELECT c FROM GcContacto c LEFT JOIN FETCH c.redSocial " +
           "WHERE c.empresa.id = :empresaId " +
           "AND (:tipoCanal IS NULL OR c.tipoCanal = :tipoCanal) " +
           "AND (:incluirInactivos = true OR c.activo = 1) " +
           "ORDER BY c.esPrincipal DESC, c.tipoCanal ASC, c.id ASC")
    List<GcContacto> findByEmpresa(@Param("empresaId") Long empresaId,
                                   @Param("tipoCanal") TipoCanal tipoCanal,
                                   @Param("incluirInactivos") boolean incluirInactivos);

    /**
     * Contactos de una persona, mismos filtros que {@link #findByEmpresa}.
     */
    @Query("SELECT c FROM GcContacto c LEFT JOIN FETCH c.redSocial " +
           "WHERE c.persona.id = :personaId " +
           "AND (:tipoCanal IS NULL OR c.tipoCanal = :tipoCanal) " +
           "AND (:incluirInactivos = true OR c.activo = 1) " +
           "ORDER BY c.esPrincipal DESC, c.tipoCanal ASC, c.id ASC")
    List<GcContacto> findByPersona(@Param("personaId") Long personaId,
                                   @Param("tipoCanal") TipoCanal tipoCanal,
                                   @Param("incluirInactivos") boolean incluirInactivos);

    // --- Helpers para RB-25 (principal único) y RB-28 (duplicados) ---

    List<GcContacto> findByEmpresa_IdAndTipoCanalAndActivo(Long empresaId, TipoCanal tipoCanal, Integer activo);

    List<GcContacto> findByPersona_IdAndTipoCanalAndActivo(Long personaId, TipoCanal tipoCanal, Integer activo);
}
