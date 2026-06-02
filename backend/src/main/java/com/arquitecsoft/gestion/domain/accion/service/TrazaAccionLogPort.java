package com.arquitecsoft.gestion.domain.accion.service;

import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementación por defecto del puerto de trazabilidad: NO persiste todavía un
 * GcActividad, solo deja constancia en el log.
 *
 * PENDIENTE: reemplazar/condicionar por el adaptador real contra ActividadService
 * cuando se confirme su API (mapeo tipoAccion -> tipoActividad, spec §8). Al añadir
 * el adaptador real, marcarlo @Primary o condicionar este por defecto.
 */
@Component
public class TrazaAccionLogPort implements TrazaAccionPort {

    private static final Logger log = LoggerFactory.getLogger(TrazaAccionLogPort.class);

    @Override
    public void registrar(GcAccion accion, GcDisposicion disposicion) {
        log.info("[TRAZA-STUB] Acción {} completada (tipo={}, disposicion={}). "
                        + "Pendiente persistir GcActividad inmutable.",
                accion.getId(), accion.getTipoAccion(),
                disposicion != null ? disposicion.getCodigo() : null);
    }
}
