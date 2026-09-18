package com.example.zkapp.core.config;

import java.time.Duration;

/**
 * Configuracion de la gestion de sesion de usuario.
 */
public class SessionProperties {

    /**
     * Tiempo maximo de inactividad antes de que la sesion expire.
     */
    private Duration timeout = Duration.ofMinutes(30);

    /**
     * Si es {@code true}, la sesion HTTP se invalida al hacer logout.
     */
    private boolean invalidateOnLogout = true;

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public boolean isInvalidateOnLogout() {
        return invalidateOnLogout;
    }

    public void setInvalidateOnLogout(boolean invalidateOnLogout) {
        this.invalidateOnLogout = invalidateOnLogout;
    }
}
