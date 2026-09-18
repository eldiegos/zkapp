package com.example.zkapp.core.config;

import jakarta.validation.constraints.NotNull;

/**
 * Selecciona el proveedor de autenticacion activo.
 */
public class AuthenticationProperties {

    /**
     * Proveedor de autenticacion utilizado por la aplicacion.
     */
    @NotNull
    private AuthenticationProvider provider = AuthenticationProvider.LDAP;

    public AuthenticationProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthenticationProvider provider) {
        this.provider = provider;
    }
}
