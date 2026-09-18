package com.example.zkapp.security.auth;

import com.example.zkapp.core.config.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * Punto de extension que desacopla el mecanismo de autenticacion HTTP (formulario LDAP, OAuth2/OIDC
 * Microsoft, ...) de la configuracion comun de seguridad.
 * <p>
 * La plataforma selecciona, en tiempo de arranque, el {@link ZkAppAuthenticationConfigurer} cuyo
 * {@link #supportedProvider()} coincide con {@code zkapp.security.authentication.provider} y le
 * delega la configuracion del mecanismo de login sobre {@link HttpSecurity}.
 * <p>
 * Anadir un nuevo proveedor de autenticacion consiste unicamente en implementar esta interfaz y
 * registrar el bean correspondiente: no requiere modificar {@code zkapp-security} ni la aplicacion
 * consumidora.
 */
public interface ZkAppAuthenticationConfigurer {

    /**
     * @return el proveedor de autenticacion que esta implementacion sabe configurar.
     */
    AuthenticationProvider supportedProvider();

    /**
     * Configura el mecanismo de autenticacion (formLogin, oauth2Login, ...) sobre la cadena de
     * filtros de seguridad comun.
     */
    void configure(HttpSecurity http) throws Exception;
}
