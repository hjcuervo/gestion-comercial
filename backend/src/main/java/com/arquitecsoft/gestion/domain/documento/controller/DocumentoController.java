package com.arquitecsoft.gestion.domain.documento.controller;

import com.arquitecsoft.gestion.domain.documento.dto.DocumentoCreateRequest;
import com.arquitecsoft.gestion.domain.documento.dto.DocumentoResponse;
import com.arquitecsoft.gestion.domain.documento.service.DocumentoService;
import com.arquitecsoft.gestion.infrastructure.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<DocumentoResponse>> listar(
            @RequestParam(required = false, name = "oportunidad_id") Long oportunidadId,
            @RequestParam(required = false, name = "actividad_id") Long actividadId,
            @RequestParam(required = false, name = "tipo_documento_id") Long tipoDocumentoId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20", name = "page_size") int pageSize) {
        if (pageSize > 100) pageSize = 100;
        return ResponseEntity.ok(documentoService.listar(oportunidadId, actividadId, tipoDocumentoId, page, pageSize));
    }

    @GetMapping("/oportunidad/{oportunidadId}")
    public ResponseEntity<List<DocumentoResponse>> listarPorOportunidad(@PathVariable Long oportunidadId) {
        return ResponseEntity.ok(documentoService.listarPorOportunidad(oportunidadId));
    }

    @GetMapping("/actividad/{actividadId}")
    public ResponseEntity<List<DocumentoResponse>> listarPorActividad(@PathVariable Long actividadId) {
        return ResponseEntity.ok(documentoService.listarPorActividad(actividadId));
    }

    @GetMapping("/contrato/{contratoId}")
    public ResponseEntity<List<DocumentoResponse>> listarPorContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(documentoService.listarPorContrato(contratoId));
    }

    @GetMapping("/modificacion/{modificacionId}")
    public ResponseEntity<List<DocumentoResponse>> listarPorModificacion(@PathVariable Long modificacionId) {
        return ResponseEntity.ok(documentoService.listarPorModificacion(modificacionId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<DocumentoResponse> crear(@Valid @RequestBody DocumentoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.crear(request));
    }

    @PostMapping("/enlace")
    public ResponseEntity<DocumentoResponse> crearEnlace(@Valid @RequestBody DocumentoCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoService.crear(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        documentoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
