package com.arquitecsoft.gestion.domain.contacto.controller;

import com.arquitecsoft.gestion.domain.contacto.dto.*;
import com.arquitecsoft.gestion.domain.contacto.service.ContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ContactoController {

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    // ---- Contactos de empresa ----

    @GetMapping("/empresas/{empresaId}/contactos")
    public ResponseEntity<List<ContactoResponse>> listarPorEmpresa(
            @PathVariable Long empresaId,
            @RequestParam(required = false, name = "tipo_canal") String tipoCanal,
            @RequestParam(required = false, name = "incluir_inactivos", defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(contactoService.listarPorEmpresa(empresaId, tipoCanal, incluirInactivos));
    }

    @PostMapping("/empresas/{empresaId}/contactos")
    public ResponseEntity<ContactoResponse> crearParaEmpresa(
            @PathVariable Long empresaId,
            @Valid @RequestBody ContactoCreateRequest request) {
        ContactoResponse response = contactoService.crearParaEmpresa(empresaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---- Contactos de persona ----

    @GetMapping("/personas/{personaId}/contactos")
    public ResponseEntity<List<ContactoResponse>> listarPorPersona(
            @PathVariable Long personaId,
            @RequestParam(required = false, name = "tipo_canal") String tipoCanal,
            @RequestParam(required = false, name = "incluir_inactivos", defaultValue = "false") boolean incluirInactivos) {
        return ResponseEntity.ok(contactoService.listarPorPersona(personaId, tipoCanal, incluirInactivos));
    }

    @PostMapping("/personas/{personaId}/contactos")
    public ResponseEntity<ContactoResponse> crearParaPersona(
            @PathVariable Long personaId,
            @Valid @RequestBody ContactoCreateRequest request) {
        ContactoResponse response = contactoService.crearParaPersona(personaId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---- Operaciones sobre el contacto ----

    @PutMapping("/contactos/{id}")
    public ResponseEntity<ContactoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ContactoUpdateRequest request) {
        return ResponseEntity.ok(contactoService.actualizar(id, request));
    }

    @DeleteMapping("/contactos/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        contactoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/contactos/{id}/principal")
    public ResponseEntity<ContactoResponse> marcarPrincipal(@PathVariable Long id) {
        return ResponseEntity.ok(contactoService.marcarPrincipal(id));
    }

    // ---- Payloads de integración externa ----

    @GetMapping("/contactos/{id}/llamada")
    public ResponseEntity<AccionLlamadaResponse> payloadLlamada(@PathVariable Long id) {
        return ResponseEntity.ok(contactoService.payloadLlamada(id));
    }

    @GetMapping("/contactos/{id}/correo")
    public ResponseEntity<AccionCorreoResponse> payloadCorreo(@PathVariable Long id) {
        return ResponseEntity.ok(contactoService.payloadCorreo(id));
    }
}
