package com.arquitecsoft.gestion.domain.empresa.controller;

import com.arquitecsoft.gestion.domain.actividad.entity.GcTipoActividad;
import com.arquitecsoft.gestion.domain.actividad.repository.GcTipoActividadRepository;
import com.arquitecsoft.gestion.domain.documento.entity.GcTipoDocumentoOpp;
import com.arquitecsoft.gestion.domain.documento.repository.GcTipoDocumentoOppRepository;
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
    private final GcTipoActividadRepository tipoActividadRepo;
    private final GcTipoDocumentoOppRepository tipoDocumentoOppRepo;

    public CatalogoController(
            GcDocumentTypeRepository documentTypeRepo,
            GcPaisRepository paisRepo,
            GcDepartamentoRepository departamentoRepo,
            GcMunicipioRepository municipioRepo,
            GcTipoActividadRepository tipoActividadRepo,
            GcTipoDocumentoOppRepository tipoDocumentoOppRepo) {
        this.documentTypeRepo = documentTypeRepo;
        this.paisRepo = paisRepo;
        this.departamentoRepo = departamentoRepo;
        this.municipioRepo = municipioRepo;
        this.tipoActividadRepo = tipoActividadRepo;
        this.tipoDocumentoOppRepo = tipoDocumentoOppRepo;
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

    @GetMapping("/tipos-actividad")
    public ResponseEntity<List<GcTipoActividad>> listarTiposActividad() {
        return ResponseEntity.ok(tipoActividadRepo.findByActivoTrueOrderByNombreAsc());
    }

    @GetMapping("/tipos-documento-opp")
    public ResponseEntity<List<GcTipoDocumentoOpp>> listarTiposDocumentoOpp() {
        return ResponseEntity.ok(tipoDocumentoOppRepo.findByActivoTrueOrderByNombreAsc());
    }
}
