package com.arquitecsoft.gestion.domain.accion.service;

import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Contrato de extensión del Motor de Acción (spec §11).
 *
 * Cada subsistema (Generación de Demanda, Avance Guiado) implementa esta interfaz
 * como un @Component para su {@code origen}. El motor la descubre por inyección de
 * {@code List<ManejadorEfectoAccion>} y la invoca cuando una disposición tiene un
 * efecto DELEGADO (AVANZAR_PASO, CALIFICAR, DESCARTAR, CUMPLE_REQUISITO).
 *
 * Los efectos GENÉRICOS (REINTENTAR, NO_APLICA) los resuelve el propio motor.
 */
public interface ManejadorEfectoAccion {

    /** Origen de las acciones que este manejador atiende. */
    GcAccion.Origen getOrigen();

    /**
     * Se invoca dentro de la transacción de completar(). El manejador aplica los
     * efectos sobre sus propias entidades (avanzar enrolamiento, crear oportunidad,
     * recalcular %, etc.) y devuelve las solicitudes de nuevas acciones que el motor
     * materializará. Puede devolver una lista vacía.
     */
    List<SolicitudAccion> alCompletar(GcAccion accion, GcDisposicion disposicion);

    /**
     * Descripción de una nueva acción a materializar. Objeto de valor interno del
     * subsistema (no es un DTO de API).
     */
    record SolicitudAccion(
            Long responsableId,
            GcAccion.TipoAccion tipoAccion,
            GcAccion.Prioridad prioridad,
            GcAccion.Origen origen,
            Long origenRefId,
            GcAccion.ReferenciaTipo referenciaTipo,
            Long referenciaId,
            LocalDateTime disponibleDesde,
            LocalDateTime vencimiento
    ) {}
}
