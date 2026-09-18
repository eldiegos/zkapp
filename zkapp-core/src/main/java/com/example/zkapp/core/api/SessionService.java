package com.example.zkapp.core.api;

/**
 * API publica y estable para gestionar la sesion de usuario, independiente de {@code HttpSession}
 * y de los detalles del contenedor servlet.
 * <p>
 * Las capas de negocio no deben propagar {@code HttpSession} entre capas: deben depender de este
 * servicio.
 */
public interface SessionService {

    /**
     * @return {@code true} si la sesion actual pertenece a un usuario autenticado.
     */
    boolean isAuthenticated();

    /**
     * @return el identificador de la sesion actual, o {@code null} si no existe sesion.
     */
    String getSessionId();

    /**
     * Invalida la sesion actual.
     */
    void invalidate();

    /**
     * Almacena un valor en la sesion actual.
     *
     * @param key   clave bajo la que se almacena el valor.
     * @param value valor a almacenar.
     */
    <T> void set(String key, T value);

    /**
     * Recupera un valor previamente almacenado en la sesion actual.
     *
     * @param key  clave bajo la que se almaceno el valor.
     * @param type tipo esperado del valor.
     * @return el valor almacenado, o {@code null} si no existe o no es del tipo esperado.
     */
    <T> T get(String key, Class<T> type);
}
