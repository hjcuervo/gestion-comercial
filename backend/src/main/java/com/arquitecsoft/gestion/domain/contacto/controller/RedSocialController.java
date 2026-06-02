package com.arquitecsoft.gestion.domain.contacto.controller;

import com.arquitecsoft.gestion.domain.contacto.dto.RedSocialResponse;
import com.arquitecsoft.gestion.domain.contacto.service.ContactoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/redes-sociales")
public class RedSocialController {

    private final ContactoService contactoService;

    public RedSocialController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public ResponseEntity<List<RedSocialResponse>> listar() {
        return ResponseEntity.ok(contactoService.listarRedesSociales());
    }
}
