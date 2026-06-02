package com.arquitecsoft.gestion.domain.accion.controller;

import com.arquitecsoft.gestion.domain.accion.dto.CrearDisposicionRequest;
import com.arquitecsoft.gestion.domain.accion.dto.DisposicionResponse;
import com.arquitecsoft.gestion.domain.accion.entity.GcAccion;
import com.arquitecsoft.gestion.domain.accion.service.DisposicionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/disposiciones")
public class DisposicionController {

    private final DisposicionService disposicionService;

    public DisposicionController(DisposicionService disposicionService) {
        this.disposicionService = disposicionService;
    }

    /**
     * Lista el catálogo. Si se pasa {@code origen}, devuelve solo las aplicables a ese
     * origen (incluyendo las genéricas); si no, devuelve todas.
     */
    @GetMapping
    public ResponseEntity<List<DisposicionResponse>> listar(
            @RequestParam(required = false) GcAccion.Origen origen) {
        if (origen != null) {
            return ResponseEntity.ok(disposicionService.listarAplicables(origen));
        }
        return ResponseEntity.ok(disposicionService.listar());
    }

    // Alta de disposición. TODO: restringir a rol Admin al cablear la API de roles.
    @PostMapping
    public ResponseEntity<DisposicionResponse> crear(@Valid @RequestBody CrearDisposicionRequest request) {
        DisposicionResponse response = disposicionService.crear(
                request.getCodigo(), request.getNombre(), request.getEfecto(),
                request.getOrigenAplicable(), request.getEsperaReintentoMin());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
