package com.arquitecsoft.gestion.domain.contrato.repository;

import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato;
import com.arquitecsoft.gestion.domain.contrato.entity.GcContrato.EstadoContrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GcContratoRepository extends JpaRepository<GcContrato, Long> {

    @Query("SELECT c FROM GcContrato c " +
           "JOIN FETCH c.empresa " +
           "JOIN FETCH c.tipoContrato " +
           "LEFT JOIN FETCH c.empresaFacturacion " +
           "LEFT JOIN FETCH c.oportunidad " +
           "LEFT JOIN FETCH c.procesoContratacion " +
           "LEFT JOIN FETCH c.modificaciones " +
           "WHERE c.id = :id")
    Optional<GcContrato> findByIdWithRelations(@Param("id") Long id);

    @Query("SELECT c FROM GcContrato c " +
           "JOIN FETCH c.empresa " +
           "JOIN FETCH c.tipoContrato " +
           "WHERE (:estado IS NULL OR c.estado = :estado) AND " +
           "(:empresaId IS NULL OR c.empresa.id = :empresaId) AND " +
           "(:moneda IS NULL OR c.moneda = :moneda)")
    Page<GcContrato> findWithFilters(
            @Param("estado") EstadoContrato estado,
            @Param("empresaId") Long empresaId,
            @Param("moneda") String moneda,
            Pageable pageable);

    @Query("SELECT c FROM GcContrato c " +
           "JOIN FETCH c.empresa " +
           "JOIN FETCH c.tipoContrato " +
           "WHERE c.oportunidad.id = :oportunidadId " +
           "ORDER BY c.fechaCreacion DESC")
    List<GcContrato> findByOportunidadId(@Param("oportunidadId") Long oportunidadId);

    @Query("SELECT c FROM GcContrato c " +
           "JOIN FETCH c.empresa " +
           "JOIN FETCH c.tipoContrato " +
           "WHERE c.procesoContratacion.id = :procesoId")
    List<GcContrato> findByProcesoContratacionId(@Param("procesoId") Long procesoId);

    List<GcContrato> findByEstado(EstadoContrato estado);

    @Query("SELECT c FROM GcContrato c WHERE c.empresa.id = :empresaId AND c.estado = 'VIGENTE'")
    List<GcContrato> findVigentesByEmpresaId(@Param("empresaId") Long empresaId);

    /**
     * Variante con JOIN FETCH a compromisos de ingreso del contrato.
     * Reemplaza al antiguo findByEstadoWithFormasPago, que cargaba
     * c.formasPago (campo renombrado a c.compromisos tras el rediseño del
     * Mundo 3). Mantiene la misma semántica: traer contratos por estado
     * con sus compromisos ya cargados.
     */
    @Query("SELECT DISTINCT c FROM GcContrato c " +
           "JOIN FETCH c.empresa " +
           "LEFT JOIN FETCH c.tipoContrato " +
           "LEFT JOIN FETCH c.compromisos " +
           "WHERE c.estado = :estado")
    List<GcContrato> findByEstadoWithCompromisos(@Param("estado") EstadoContrato estado);
}
