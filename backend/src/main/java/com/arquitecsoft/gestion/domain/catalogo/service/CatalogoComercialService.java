package com.arquitecsoft.gestion.domain.catalogo.service;

import com.arquitecsoft.gestion.domain.catalogo.dto.OrigenResponse;
import com.arquitecsoft.gestion.domain.catalogo.dto.SectorResponse;
import com.arquitecsoft.gestion.domain.catalogo.repository.GcOrigenRepository;
import com.arquitecsoft.gestion.domain.catalogo.repository.GcSectorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CatalogoComercialService {

    private final GcSectorRepository sectorRepository;
    private final GcOrigenRepository origenRepository;

    public CatalogoComercialService(GcSectorRepository sectorRepository,
                                    GcOrigenRepository origenRepository) {
        this.sectorRepository = sectorRepository;
        this.origenRepository = origenRepository;
    }

    @Transactional(readOnly = true)
    public List<SectorResponse> listarSectores() {
        return sectorRepository.findByActivoOrderByOrdenAscNombreAsc(1).stream()
                .map(SectorResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrigenResponse> listarOrigenes() {
        return origenRepository.findByActivoOrderByOrdenAscNombreAsc(1).stream()
                .map(OrigenResponse::fromEntity)
                .toList();
    }
}
