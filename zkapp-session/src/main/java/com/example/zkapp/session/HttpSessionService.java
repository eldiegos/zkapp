package com.example.zkapp.session;

import com.example.zkapp.core.api.SessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Implementacion por defecto de {@link SessionService} basada en {@code HttpSession}.
 * <p>
 * Esta clase es el unico punto de la plataforma que trata directamente con {@code HttpSession};
 * el resto de capas (incluida la aplicacion consumidora) deben depender de {@link SessionService}.
 */
public class HttpSessionService implements SessionService {

    @Override
    public boolean isAuthenticated() {
        HttpSession session = currentSession(false);
        return session != null;
    }

    @Override
    public String getSessionId() {
        HttpSession session = currentSession(false);
        return session != null ? session.getId() : null;
    }

    @Override
    public void invalidate() {
        HttpSession session = currentSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @Override
    public <T> void set(String key, T value) {
        currentSession(true).setAttribute(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        HttpSession session = currentSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(key);
        if (type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    private HttpSession currentSession(boolean create) {
        HttpServletRequest request = currentRequest();
        return request != null ? request.getSession(create) : null;
    }

    private HttpServletRequest currentRequest() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        return attributes.getRequest();
    }
}
