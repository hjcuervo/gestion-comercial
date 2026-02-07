package com.arquitecsoft.gestion.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser) {
            return (AuthenticatedUser) authentication.getPrincipal();
        }
        return null;
    }

    public Long getCurrentUserId() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    public String getCurrentUsername() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    public boolean hasRole(String role) {
        AuthenticatedUser user = getCurrentUser();
        return user != null && user.getRol().equals(role);
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
