package com.mokah.veterinary.security;

import com.mokah.veterinary.security.model.Credentials;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class ProfessionalVisibility {
    private ProfessionalVisibility() {}

    public static boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public static boolean isOwnUser(UUID userExternalId, String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() &&
                (auth.getPrincipal() instanceof Credentials credentials
                        && credentials.getUser() != null
                        && userExternalId.equals(credentials.getUser().getExternalId())
                        || email != null && email.equalsIgnoreCase(auth.getName()));
    }
}
