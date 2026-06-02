package com.arquitecsoft.gestion.domain.usuario.service;

import com.arquitecsoft.gestion.domain.usuario.dto.UsuarioResponse;
import com.arquitecsoft.gestion.domain.usuario.repository.GcUsuarioRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lectura de usuarios para selección (p. ej. propietario de empresa/persona — F-RP4/F-RP5).
 */
@Service
public class UsuarioService {

    private final GcUsuarioRepository usuarioRepository;

    public UsuarioService(GcUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Usuarios activos, ordenados por nombre, para selección como propietario/responsable. */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarActivos() {
        return usuarioRepository.findAll(Sort.by("apellidos", "nombres").ascending()).stream()
                .filter(u -> u.getActivo() != null && u.getActivo() == 1)
                .map(UsuarioResponse::fromEntity)
                .toList();
    }
}
