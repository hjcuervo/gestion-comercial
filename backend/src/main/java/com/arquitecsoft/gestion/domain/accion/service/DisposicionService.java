package com.arquitecsoft.gestion.domain.accion.service;

import com.arquitecsoft.gestion.domain.accion.dto.DisposicionResponse;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion.Efecto;
import com.arquitecsoft.gestion.domain.accion.entity.GcDisposicion.OrigenAplicable;
import com.arquitecsoft.gestion.domain.accion.repository.GcDisposicionRepository;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Catálogo de disposiciones (configurable, D-4). La configuración (alta/edición) es
 * de rol Admin; el cableado de esa restricción a la API de roles del proyecto queda
 * pendiente (igual que en AccionService.reasignar).
 */
@Service
public class DisposicionService {

    private final GcDisposicionRepository disposicionRepository;
    private final SecurityUtils securityUtils;

    public DisposicionService(GcDisposicionRepository disposicionRepository,
                              SecurityUtils securityUtils) {
        this.disposicionRepository = disposicionRepository;
        this.securityUtils = securityUtils;
    }

    /** Disposiciones activas aplicables a un origen (incluye las genéricas TODOS). */
    @Transactional(readOnly = true)
    public List<DisposicionResponse> listarAplicables(GcAccion.Origen origen) {
        OrigenAplicable oa = OrigenAplicable.valueOf(origen.name());
        return disposicionRepository
                .findByActivoTrueAndOrigenAplicableIn(List.of(oa, OrigenAplicable.TODOS)).stream()
                .map(DisposicionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisposicionResponse> listar() {
        return disposicionRepository.findAll().stream()
                .map(DisposicionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public DisposicionResponse crear(String codigo, String nombre, Efecto efecto,
                                     OrigenAplicable origenAplicable, Integer esperaReintentoMin) {
        disposicionRepository.findByCodigo(codigo).ifPresent(d -> {
            throw new BusinessException("DISPOSICION_DUPLICADA", "Ya existe una disposición con código " + codigo);
        });
        GcDisposicion d = new GcDisposicion();
        d.setCodigo(codigo);
        d.setNombre(nombre);
        d.setEfecto(efecto);
        d.setOrigenAplicable(origenAplicable);
        d.setEsperaReintentoMin(esperaReintentoMin);
        d.setActivo(Boolean.TRUE);
        d.setCreadoPor(securityUtils.getCurrentUserId());
        return DisposicionResponse.fromEntity(disposicionRepository.save(d));
    }
}
