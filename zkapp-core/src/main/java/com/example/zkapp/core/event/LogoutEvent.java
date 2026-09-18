package com.example.zkapp.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * Evento tecnico publicado cuando un usuario cierra sesion correctamente.
 * <p>
 * Vive en {@code zkapp-core} para que tanto {@code zkapp-security} (que lo publica) como
 * {@code zkapp-audit} (que lo escucha) dependan unicamente del modulo base, sin acoplarse entre
 * si.
 */
public class LogoutEvent extends ApplicationEvent {

    private final String username;

    public LogoutEvent(Object source, String username) {
        super(source);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
