package com.example.zkapp.core.event;

import org.springframework.context.ApplicationEvent;

/**
 * Evento tecnico publicado cuando se deniega el acceso a un recurso protegido.
 * <p>
 * Vive en {@code zkapp-core} por el mismo motivo que {@link LogoutEvent}: desacopla
 * {@code zkapp-security} (publicador) de {@code zkapp-audit} (consumidor).
 */
public class AccessDeniedAuditEvent extends ApplicationEvent {

    private final String username;
    private final String requestUri;

    public AccessDeniedAuditEvent(Object source, String username, String requestUri) {
        super(source);
        this.username = username;
        this.requestUri = requestUri;
    }

    public String getUsername() {
        return username;
    }

    public String getRequestUri() {
        return requestUri;
    }
}
