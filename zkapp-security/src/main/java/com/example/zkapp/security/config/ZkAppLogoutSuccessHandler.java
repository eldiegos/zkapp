package com.example.zkapp.security.config;

import com.example.zkapp.core.event.LogoutEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import java.io.IOException;

/**
 * Publica un {@link LogoutEvent} y redirige a {@code /login?logout}.
 * <p>
 * El {@link Authentication} recibido representa al usuario que acaba de cerrar sesion: se captura
 * antes de que {@code SecurityContextHolder} se limpie, por lo que sigue disponible en este punto.
 */
public class ZkAppLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final SimpleUrlLogoutSuccessHandler delegate;

    public ZkAppLogoutSuccessHandler(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.delegate = new SimpleUrlLogoutSuccessHandler();
        this.delegate.setDefaultTargetUrl("/login?logout");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String username = authentication != null ? authentication.getName() : null;
        eventPublisher.publishEvent(new LogoutEvent(this, username));
        delegate.onLogoutSuccess(request, response, authentication);
    }
}
