package com.example.zkapp.core.config;

/**
 * Proveedores de autenticacion soportados por la plataforma zkapp.
 * <p>
 * La arquitectura de autenticacion esta desacoplada de este enumerado (ver
 * {@code com.example.zkapp.security.auth.AuthenticationProviderConfigurer}), por lo que anadir
 * un nuevo proveedor no requiere modificar las aplicaciones consumidoras.
 */
public enum AuthenticationProvider {
    LDAP,
    MICROSOFT
}
