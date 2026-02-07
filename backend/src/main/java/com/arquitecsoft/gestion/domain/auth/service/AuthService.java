package com.arquitecsoft.gestion.domain.auth.service;

import com.arquitecsoft.gestion.domain.auth.dto.LoginRequest;
import com.arquitecsoft.gestion.domain.auth.dto.LoginResponse;
import com.arquitecsoft.gestion.domain.usuario.dto.UsuarioResponse;
import com.arquitecsoft.gestion.domain.usuario.entity.GcUsuario;
import com.arquitecsoft.gestion.domain.usuario.repository.GcUsuarioRepository;
import com.arquitecsoft.gestion.infrastructure.exception.BusinessException;
import com.arquitecsoft.gestion.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final GcUsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(GcUsuarioRepository usuarioRepository, 
                       PasswordEncoder passwordEncoder, 
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        GcUsuario usuario = usuarioRepository.findByUsernameAndActivo(request.getUsername())
                .orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Usuario o contraseña incorrectos"));

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new BusinessException("INVALID_CREDENTIALS", "Usuario o contraseña incorrectos");
        }

        String token = jwtService.generateToken(
                usuario.getId(), 
                usuario.getUsername(), 
                usuario.getRol().name()
        );

        return new LoginResponse(
                token,
                jwtService.getExpiration(),
                UsuarioResponse.fromEntity(usuario)
        );
    }

    public UsuarioResponse getCurrentUser(Long userId) {
        GcUsuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("NOT_FOUND", "Usuario no encontrado"));
        
        return UsuarioResponse.fromEntity(usuario);
    }
}
