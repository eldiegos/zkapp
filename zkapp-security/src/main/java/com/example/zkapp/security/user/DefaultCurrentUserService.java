package com.example.zkapp.security.user;

import com.example.zkapp.core.api.CurrentUserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implementacion por defecto de {@link CurrentUserService} basada en
 * {@link SecurityContextHolder}.
 * <p>
 * Es el unico lugar de la plataforma que accede directamente a {@code SecurityContextHolder} /
 * {@code Authentication}: el resto de capas (incluida la aplicacion consumidora) dependen de la
 * API publica {@link CurrentUserService}.
 */
public class DefaultCurrentUserService implements CurrentUserService {

    @Override
    public boolean isAuthenticated() {
        Authentication authentication = authentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    @Override
    public String getUsername() {
        Authentication authentication = authentication();
        return isAuthenticated() ? authentication.getName() : null;
    }

    @Override
    public String getDisplayName() {
        if (!isAuthenticated()) {
            return null;
        }
        Object principal = authentication().getPrincipal();
        if (principal instanceof OAuth2User oAuth2User) {
            Object name = oAuth2User.getAttributes().get("name");
            if (name != null) {
                return name.toString();
            }
        }
        return getUsername();
    }

    @Override
    public String getEmail() {
        if (!isAuthenticated()) {
            return null;
        }
        Object principal = authentication().getPrincipal();
        if (principal instanceof OidcUser oidcUser && oidcUser.getEmail() != null) {
            return oidcUser.getEmail();
        }
        if (principal instanceof OAuth2User oAuth2User) {
            Object email = oAuth2User.getAttributes().get("email");
            if (email == null) {
                email = oAuth2User.getAttributes().get("preferred_username");
            }
            return email != null ? email.toString() : null;
        }
        return null;
    }

    @Override
    public Set<String> getRoles() {
        Set<String> roles = new LinkedHashSet<>();
        for (String authority : getAuthorities()) {
            if (authority.startsWith("ROLE_")) {
                roles.add(authority.substring("ROLE_".length()));
            }
        }
        return roles;
    }

    @Override
    public Set<String> getAuthorities() {
        Set<String> authorities = new LinkedHashSet<>();
        Authentication authentication = authentication();
        if (authentication != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                authorities.add(authority.getAuthority());
            }
        }
        return authorities;
    }

    @Override
    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    @Override
    public boolean hasAuthority(String authority) {
        return getAuthorities().contains(authority);
    }

    private Authentication authentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
