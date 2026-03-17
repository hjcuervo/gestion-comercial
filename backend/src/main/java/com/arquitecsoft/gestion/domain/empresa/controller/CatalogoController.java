package com.arquitecsoft.gestion.domain.empresa.controller;

import com.arquitecsoft.gestion.domain.empresa.entity.GcDepartamento;
import com.arquitecsoft.gestion.domain.empresa.entity.GcDocumentType;
import com.arquitecsoft.gestion.domain.empresa.entity.GcMunicipio;
import com.arquitecsoft.gestion.domain.empresa.entity.GcPais;
import com.arquitecsoft.gestion.domain.empresa.repository.GcDepartamentoRepository;
import com.arquitecsoft.gestion.domain.empresa.repository.GcDocumentTypeRepository;
import com.arquitecsoft.gestion.domain.empresa.repository.GcMunicipioRepository;
import com.arquitecsoft.gestion.domain.empresa.repository.GcPaisRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogos")
public class CatalogoController {

    private final GcDocumentTypeRepository documentTypeRepo;
    private final GcPaisRepository paisRepo;
    private final GcDepartamentoRepository departamentoRepo;
    private final GcMunicipioRepository municipioRepo;

    public CatalogoController(
            GcDocumentTypeRepository documentTypeRepo,
            GcPaisRepository paisRepo,
            GcDepartamentoRepository departamentoRepo,
            GcMunicipioRepository municipioRepo) {
        this.documentTypeRepo = documentTypeRepo;
        this.paisRepo = paisRepo;
        this.departamentoRepo = departamentoRepo;
        this.municipioRepo = municipioRepo;
    }

    @GetMapping("/tipos-documento")
    public ResponseEntity<List<GcDocumentType>> listarTiposDocumento() {
        return ResponseEntity.ok(documentTypeRepo.findAllByOrderByNombreAsc());
    }

    @GetMapping("/paises")
    public ResponseEntity<List<GcPais>> listarPaises() {
        return ResponseEntity.ok(paisRepo.findAllByOrderByNombreAsc());
    }

    @GetMapping("/departamentos")
    public ResponseEntity<List<GcDepartamento>> listarDepartamentos() {
        return ResponseEntity.ok(departamentoRepo.findAllByOrderByDescripcionAsc());
    }

    @GetMapping("/municipios")
    public ResponseEntity<List<GcMunicipio>> listarMunicipios(
            @RequestParam(required = false) String departamento) {
        if (departamento != null && !departamento.isBlank()) {
            return ResponseEntity.ok(
                municipioRepo.findByCodigoDepartamentoOrderByDescripcionAsc(departamento));
        }
        return ResponseEntity.ok(municipioRepo.findAllByOrderByDescripcionAsc());
    }
}
