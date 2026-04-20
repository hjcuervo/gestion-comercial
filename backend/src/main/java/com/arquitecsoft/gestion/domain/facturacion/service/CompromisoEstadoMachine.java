package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.facturacion.entity.GcCompromisoIngreso.EstadoCompromiso;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Tabla de transiciones válidas de la máquina de estados del compromiso de
 * ingreso, según la spec §4 (diagrama lógico).
 *
 *    Pendiente → En gestión | No logrado
 *    En gestión → Comprometido | Parcialmente cumplido | Reprogramado | No logrado
 *    Comprometido → Parcialmente cumplido | Cumplido | Reprogramado | No logrado
 *    Parcialmente cumplido → En gestión | Comprometido | Cumplido | Reprogramado | No logrado
 *    Reprogramado → En gestión | Comprometido | No logrado
 *    Cumplido (FINAL — sin salida)
 *    No logrado (FINAL — sin salida)
 *
 * Las transiciones DISPARADAS POR EVENTOS (FacturaRegistrada, etc.) se manejan
 * en el servicio; esta clase sólo responde "¿es válido pasar de X a Y?".
 */
public final class CompromisoEstadoMachine {

    private static final Map<EstadoCompromiso, Set<EstadoCompromiso>> TRANSICIONES;

    static {
        TRANSICIONES = new EnumMap<>(EstadoCompromiso.class);

        TRANSICIONES.put(EstadoCompromiso.PENDIENTE_GESTION, EnumSet.of(
                EstadoCompromiso.EN_GESTION,
                EstadoCompromiso.NO_LOGRADO
        ));

        TRANSICIONES.put(EstadoCompromiso.EN_GESTION, EnumSet.of(
                EstadoCompromiso.COMPROMETIDO,
                EstadoCompromiso.PARCIALMENTE_CUMPLIDO,
                EstadoCompromiso.REPROGRAMADO,
                EstadoCompromiso.NO_LOGRADO
        ));

        TRANSICIONES.put(EstadoCompromiso.COMPROMETIDO, EnumSet.of(
                EstadoCompromiso.PARCIALMENTE_CUMPLIDO,
                EstadoCompromiso.CUMPLIDO,
                EstadoCompromiso.REPROGRAMADO,
                EstadoCompromiso.NO_LOGRADO
        ));

        TRANSICIONES.put(EstadoCompromiso.PARCIALMENTE_CUMPLIDO, EnumSet.of(
                EstadoCompromiso.EN_GESTION,
                EstadoCompromiso.COMPROMETIDO,
                EstadoCompromiso.CUMPLIDO,
                EstadoCompromiso.REPROGRAMADO,
                EstadoCompromiso.NO_LOGRADO
        ));

        TRANSICIONES.put(EstadoCompromiso.REPROGRAMADO, EnumSet.of(
                EstadoCompromiso.EN_GESTION,
                EstadoCompromiso.COMPROMETIDO,
                EstadoCompromiso.NO_LOGRADO
        ));

        // Estados finales — sin transiciones de salida.
        TRANSICIONES.put(EstadoCompromiso.CUMPLIDO, EnumSet.noneOf(EstadoCompromiso.class));
        TRANSICIONES.put(EstadoCompromiso.NO_LOGRADO, EnumSet.noneOf(EstadoCompromiso.class));
    }

    private CompromisoEstadoMachine() {}

    public static boolean esTransicionValida(EstadoCompromiso desde, EstadoCompromiso hacia) {
        if (desde == null || hacia == null) return false;
        return TRANSICIONES.get(desde).contains(hacia);
    }

    public static boolean esEstadoFinal(EstadoCompromiso estado) {
        return estado == EstadoCompromiso.CUMPLIDO || estado == EstadoCompromiso.NO_LOGRADO;
    }

    /**
     * Lanza BusinessException si la transición no es válida.
     * Mensaje consistente para que el frontend pueda tratarlo de forma uniforme.
     */
    public static void validarTransicion(EstadoCompromiso desde, EstadoCompromiso hacia) {
        if (esEstadoFinal(desde)) {
            throw new BusinessException("BUSINESS_ERROR",
                "El compromiso está en estado final (" + desde + ") y no admite transiciones.");
        }
        if (!esTransicionValida(desde, hacia)) {
            throw new BusinessException("BUSINESS_ERROR",
                "Transición no permitida: " + desde + " → " + hacia);
        }
    }
}
