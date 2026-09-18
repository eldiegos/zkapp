package com.example.zkapp.security.config;

import com.example.zkapp.core.api.CurrentUserService;
import com.example.zkapp.core.event.AccessDeniedAuditEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;

import java.io.IOException;

/**
 * Publica un {@link AccessDeniedAuditEvent} y delega en una pagina de acceso denegado comun.
 */
public class ZkAppAccessDeniedHandler implements AccessDeniedHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final CurrentUserService currentUserService;
    private final AccessDeniedHandlerImpl delegate;

    public ZkAppAccessDeniedHandler(ApplicationEventPublisher eventPublisher, CurrentUserService currentUserService) {
        this.eventPublisher = eventPublisher;
        this.currentUserService = currentUserService;
        this.delegate = new AccessDeniedHandlerImpl();
        this.delegate.setErrorPage("/403.zul");
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        String username = currentUserService.isAuthenticated() ? currentUserService.getUsername() : null;
        eventPublisher.publishEvent(new AccessDeniedAuditEvent(this, username, request.getRequestURI()));
        delegate.handle(request, response, accessDeniedException);
    }
}
