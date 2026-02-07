package com.arquitecsoft.gestion.infrastructure.security;

public class AuthenticatedUser {
    
    private final Long userId;
    private final String username;
    private final String rol;

    public AuthenticatedUser(Long userId, String username, String rol) {
        this.userId = userId;
        this.username = username;
        this.rol = rol;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRol() {
        return rol;
    }
}
