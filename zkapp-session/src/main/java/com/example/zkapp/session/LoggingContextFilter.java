package com.example.zkapp.session;

import com.example.zkapp.core.api.CurrentUserService;
import com.example.zkapp.core.config.LoggingProperties;
import com.example.zkapp.core.config.ZkAppProperties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Puebla el {@code MDC} de SLF4J con datos tecnicos de la peticion actual (aplicacion, usuario,
 * sesion, correlation-id) para que la aplicacion consumidora pueda incluirlos en sus patrones de
 * logging sin acoplar la plataforma a ninguna infraestructura de logging concreta.
 */
public class LoggingContextFilter extends OncePerRequestFilter {

    private static final String MDC_APPLICATION_CODE = "applicationCode";
    private static final String MDC_USER = "user";
    private static final String MDC_SESSION_ID = "sessionId";
    private static final String MDC_CORRELATION_ID = "correlationId";

    private final ZkAppProperties properties;
    private final CurrentUserService currentUserService;

    public LoggingContextFilter(ZkAppProperties properties, CurrentUserService currentUserService) {
        this.properties = properties;
        this.currentUserService = currentUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        LoggingProperties logging = properties.getLogging();
        try {
            if (logging.isIncludeApplicationCode()) {
                MDC.put(MDC_APPLICATION_CODE, properties.getApplication().getCode());
            }
            if (logging.isIncludeUser() && currentUserService != null && currentUserService.isAuthenticated()) {
                MDC.put(MDC_USER, currentUserService.getUsername());
            }
            if (logging.isIncludeSessionId() && request.getSession(false) != null) {
                MDC.put(MDC_SESSION_ID, request.getSession(false).getId());
            }
            if (logging.isIncludeCorrelationId()) {
                MDC.put(MDC_CORRELATION_ID, resolveCorrelationId(request, logging.getCorrelationIdHeader()));
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_APPLICATION_CODE);
            MDC.remove(MDC_USER);
            MDC.remove(MDC_SESSION_ID);
            MDC.remove(MDC_CORRELATION_ID);
        }
    }

    private String resolveCorrelationId(HttpServletRequest request, String headerName) {
        String incoming = request.getHeader(headerName);
        return (incoming != null && !incoming.isBlank()) ? incoming : UUID.randomUUID().toString();
    }
}
