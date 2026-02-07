package com.arquitecsoft.gestion.domain.auth.controller;

import com.arquitecsoft.gestion.domain.auth.dto.LoginRequest;
import com.arquitecsoft.gestion.domain.auth.dto.LoginResponse;
import com.arquitecsoft.gestion.domain.auth.service.AuthService;
import com.arquitecsoft.gestion.domain.usuario.dto.UsuarioResponse;
import com.arquitecsoft.gestion.domain.usuario.entity.GcUsuario;
import com.arquitecsoft.gestion.domain.usuario.repository.GcUsuarioRepository;
import com.arquitecsoft.gestion.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final GcUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, 
                          GcUsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> me(@AuthenticationPrincipal AuthenticatedUser user) {
        UsuarioResponse response = authService.getCurrentUser(user.getUserId());
        return ResponseEntity.ok(response);
    }

    // ENDPOINT TEMPORAL PARA DEBUG - ELIMINAR EN PRODUCCIÓN
    @GetMapping("/debug/{username}")
    public ResponseEntity<Map<String, Object>> debug(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<GcUsuario> usuarioOpt = usuarioRepository.findByUsername(username);
        if (usuarioOpt.isEmpty()) {
            result.put("error", "Usuario no encontrado con findByUsername");
            return ResponseEntity.ok(result);
        }
        
        GcUsuario usuario = usuarioOpt.get();
        result.put("id", usuario.getId());
        result.put("username", usuario.getUsername());
        result.put("activo", usuario.getActivo());
        result.put("isActivo", usuario.isActivo());
        result.put("passwordHashLength", usuario.getPasswordHash().length());
        result.put("passwordHashStart", usuario.getPasswordHash().substring(0, 20));
        
        // Verificar password
        String testPassword = "admin123";
        boolean matches = passwordEncoder.matches(testPassword, usuario.getPasswordHash());
        result.put("passwordMatches_admin123", matches);
        
        // Generar nuevo hash para comparar
        String newHash = passwordEncoder.encode(testPassword);
        result.put("newHashExample", newHash);
        
        // Verificar query con activo
        Optional<GcUsuario> usuarioActivoOpt = usuarioRepository.findByUsernameAndActivo(username);
        result.put("foundWithActivo", usuarioActivoOpt.isPresent());
        
        return ResponseEntity.ok(result);
    }
}
