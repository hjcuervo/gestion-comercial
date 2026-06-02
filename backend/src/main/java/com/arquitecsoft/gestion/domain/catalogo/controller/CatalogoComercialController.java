package com.arquitecsoft.gestion.domain.catalogo.controller;

import com.arquitecsoft.gestion.domain.catalogo.dto.OrigenResponse;
import com.arquitecsoft.gestion.domain.catalogo.dto.SectorResponse;
import com.arquitecsoft.gestion.domain.catalogo.service.CatalogoComercialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expone los catálogos comerciales nuevos bajo /api/v1/catalogos
 * (sub-rutas distintas a las del CatalogoController existente: paises, departamentos, etc.).
 */
@RestController
@RequestMapping("/api/v1/catalogos")
public class CatalogoComercialController {

    private final CatalogoComercialService service;

    public CatalogoComercialController(CatalogoComercialService service) {
        this.service = service;
    }

    @GetMapping("/sectores")
    public ResponseEntity<List<SectorResponse>> sectores() {
        return ResponseEntity.ok(service.listarSectores());
    }

    @GetMapping("/origenes")
    public ResponseEntity<List<OrigenResponse>> origenes() {
        return ResponseEntity.ok(service.listarOrigenes());
    }
}
