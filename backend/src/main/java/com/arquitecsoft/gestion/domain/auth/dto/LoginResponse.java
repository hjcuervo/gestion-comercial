package com.arquitecsoft.gestion.domain.auth.dto;

import com.arquitecsoft.gestion.domain.usuario.dto.UsuarioResponse;

public class LoginResponse {
    
    private String token;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UsuarioResponse user;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long expiresIn, UsuarioResponse user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UsuarioResponse getUser() {
        return user;
    }

    public void setUser(UsuarioResponse user) {
        this.user = user;
    }
}
