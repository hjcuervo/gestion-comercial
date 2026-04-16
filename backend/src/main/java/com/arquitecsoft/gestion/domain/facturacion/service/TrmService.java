package com.arquitecsoft.gestion.domain.facturacion.service;

import com.arquitecsoft.gestion.domain.facturacion.dto.TrmCreateRequest;
import com.arquitecsoft.gestion.domain.facturacion.dto.TrmResponse;
import com.arquitecsoft.gestion.domain.facturacion.entity.GcTrm;
import com.arquitecsoft.gestion.domain.facturacion.repository.GcTrmRepository;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrmService {

    private final GcTrmRepository trmRepository;
    private final SecurityUtils securityUtils;

    public TrmService(GcTrmRepository trmRepository, SecurityUtils securityUtils) {
        this.trmRepository = trmRepository;
        this.securityUtils = securityUtils;
    }

    @Transactional(readOnly = true)
    public List<TrmResponse> listar() {
        return trmRepository.findAllByMonedaOrderDesc("USD", "COP").stream()
                .map(TrmResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public TrmResponse crear(TrmCreateRequest request) {
        if (request.getAnio() == null || request.getMes() == null || request.getMes() < 1 || request.getMes() > 12) {
            throw new BusinessException("VALIDATION_ERROR", "Año y mes son requeridos (mes 1-12)");
        }

        // Verificar si ya existe
        Optional<GcTrm> existente = trmRepository.findByAnioAndMesAndMonedaOrigenAndMonedaDestino(
                request.getAnio(), request.getMes(), "USD", "COP");

        if (existente.isPresent()) {
            // Actualizar
            GcTrm trm = existente.get();
            trm.setValor(request.getValor());
            trm = trmRepository.save(trm);
            return TrmResponse.fromEntity(trm);
        }

        GcTrm trm = new GcTrm();
        trm.setAnio(request.getAnio());
        trm.setMes(request.getMes());
        trm.setMonedaOrigen("USD");
        trm.setMonedaDestino("COP");
        trm.setValor(request.getValor());
        trm.setCreadoPor(securityUtils.getCurrentUserId());
        trm = trmRepository.save(trm);
        return TrmResponse.fromEntity(trm);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!trmRepository.existsById(id)) {
            throw new BusinessException("NOT_FOUND", "TRM no encontrada");
        }
        trmRepository.deleteById(id);
    }

    /**
     * Obtiene la TRM para un año-mes. Si no existe, busca la más reciente anterior.
     * RN-11: Si no hay TRM para un mes, se usa la del mes anterior más reciente.
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerTrm(int anio, int mes) {
        Optional<GcTrm> exact = trmRepository.findByAnioAndMesAndMonedaOrigenAndMonedaDestino(anio, mes, "USD", "COP");
        if (exact.isPresent()) return exact.get().getValor();

        List<GcTrm> recientes = trmRepository.findMostRecentBefore(anio, mes, "USD", "COP");
        if (!recientes.isEmpty()) return recientes.get(0).getValor();

        return null; // No hay TRM disponible
    }

    /**
     * Convierte un valor de USD a COP usando la TRM del mes.
     */
    public BigDecimal convertirACop(BigDecimal valorUsd, int anio, int mes) {
        if (valorUsd == null) return null;
        BigDecimal trm = obtenerTrm(anio, mes);
        if (trm == null) return null;
        return valorUsd.multiply(trm);
    }
}
