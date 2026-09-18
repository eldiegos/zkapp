package com.example.zkapp.core.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Agrupa la configuracion de seguridad de la plataforma: proveedor de autenticacion activo y
 * configuracion especifica de cada proveedor soportado.
 */
public class SecurityProperties {

    @NestedConfigurationProperty
    @Valid
    private AuthenticationProperties authentication = new AuthenticationProperties();

    @NestedConfigurationProperty
    @Valid
    private LdapProperties ldap = new LdapProperties();

    @NestedConfigurationProperty
    @Valid
    private MicrosoftProperties microsoft = new MicrosoftProperties();

    public AuthenticationProperties getAuthentication() {
        return authentication;
    }

    public void setAuthentication(AuthenticationProperties authentication) {
        this.authentication = authentication;
    }

    public LdapProperties getLdap() {
        return ldap;
    }

    public void setLdap(LdapProperties ldap) {
        this.ldap = ldap;
    }

    public MicrosoftProperties getMicrosoft() {
        return microsoft;
    }

    public void setMicrosoft(MicrosoftProperties microsoft) {
        this.microsoft = microsoft;
    }
}
