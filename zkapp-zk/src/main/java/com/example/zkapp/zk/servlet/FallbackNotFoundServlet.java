package com.example.zkapp.zk.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet de respaldo registrado en {@code "/"} (catch-all).
 * <p>
 * Sin ningun servlet mapeado a la raiz, un contenedor Servlet puede devolver 404 antes de invocar
 * siquiera la cadena de filtros para rutas que unicamente gestiona un filtro (por ejemplo
 * {@code /logout}, gestionado por el {@code LogoutFilter} de Spring Security, o los endpoints
 * OAuth2 {@code /oauth2/authorization/**} y {@code /login/oauth2/code/**} de la integracion
 * Microsoft). Este servlet garantiza que el contenedor siempre encuentre una correspondencia,
 * dejando pasar la peticion por Spring Security antes de, en caso de no ser gestionada por nadie
 * mas, responder con un 404 limpio.
 */
public class FallbackNotFoundServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
