package com.example.zkapp.audit;

import com.example.zkapp.core.api.AuditService;
import com.example.zkapp.core.audit.AuditEvent;
import com.example.zkapp.core.audit.AuditEventType;
import com.example.zkapp.core.config.ZkAppProperties;
import com.example.zkapp.core.event.AccessDeniedAuditEvent;
import com.example.zkapp.core.event.LogoutEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

/**
 * Traduce los eventos tecnicos publicados por Spring Security (autenticacion) y por
 * {@code zkapp-security} (logout, acceso denegado) en llamadas a {@link AuditService}.
 * <p>
 * {@code zkapp-audit} no depende de {@code zkapp-security}: solo escucha eventos estandar de
 * {@code spring-security-core} y los eventos tecnicos definidos en {@code zkapp-core}.
 */
public class SecurityAuditEventListener {

    private final AuditService auditService;
    private final ZkAppProperties properties;

    public SecurityAuditEventListener(AuditService auditService, ZkAppProperties properties) {
        this.auditService = auditService;
        this.properties = properties;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        auditService.record(AuditEvent.of(
                AuditEventType.LOGIN_SUCCESS,
                event.getAuthentication().getName(),
                properties.getApplication().getCode(),
                "Autenticacion correcta"));
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        auditService.record(AuditEvent.of(
                AuditEventType.LOGIN_FAILURE,
                event.getAuthentication().getName(),
                properties.getApplication().getCode(),
                event.getException().getMessage()));
    }

    @EventListener
    public void onLogout(LogoutEvent event) {
        auditService.record(AuditEvent.of(
                AuditEventType.LOGOUT,
                event.getUsername(),
                properties.getApplication().getCode(),
                "Logout"));
    }

    @EventListener
    public void onAccessDenied(AccessDeniedAuditEvent event) {
        auditService.record(AuditEvent.of(
                AuditEventType.ACCESS_DENIED,
                event.getUsername(),
                properties.getApplication().getCode(),
                "Acceso denegado a " + event.getRequestUri()));
    }
}
