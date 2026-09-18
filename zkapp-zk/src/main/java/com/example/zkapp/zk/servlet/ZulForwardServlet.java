package com.example.zkapp.zk.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Reenvia (forward) una ruta "amigable" sin extension (por ejemplo {@code /login}) hacia la
 * pagina ZUL real que la implementa (por ejemplo {@code /login.zul}), de forma que el servlet de
 * ZK ({@code DHtmlLayoutServlet}, mapeado a {@code *.zul}) la renderice normalmente.
 */
public class ZulForwardServlet extends HttpServlet {

    private final String targetZulPath;

    public ZulForwardServlet(String targetZulPath) {
        this.targetZulPath = targetZulPath;
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher(targetZulPath);
        dispatcher.forward(request, response);
    }
}
