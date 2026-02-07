package com.arquitecsoft.gestion.domain.usuario.repository;

import com.arquitecsoft.gestion.domain.usuario.entity.GcUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GcUsuarioRepository extends JpaRepository<GcUsuario, Long> {
    
    Optional<GcUsuario> findByUsername(String username);
    
    Optional<GcUsuario> findByEmail(String email);
    
    @Query("SELECT u FROM GcUsuario u WHERE u.username = :username AND u.activo = 1")
    Optional<GcUsuario> findByUsernameAndActivo(@Param("username") String username);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
