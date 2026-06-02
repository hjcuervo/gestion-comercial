package com.arquitecsoft.gestion.domain.accion.service;

import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion;

/**
 * Puerto de trazabilidad (spec §8, RB-MA-09). Al completar una acción, el motor
 * emite un registro de actividad INMUTABLE a través de este puerto.
 *
 * El motor no se acopla a {@code domain/actividad}: el adaptador real que persiste
 * un {@code GcActividad} (y mapea {@code tipoAccion -> tipoActividad}) lo provee el
 * subsistema/infra correspondiente. Mientras no exista, opera la implementación por
 * defecto {@link TrazaAccionLogPort}.
 */
public interface TrazaAccionPort {

    void registrar(GcAccion accion, GcDisposicion disposicion);
}
