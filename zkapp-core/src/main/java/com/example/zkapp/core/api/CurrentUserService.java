package com.example.zkapp.core.api;

import java.util.Set;

/**
 * API publica y estable para acceder al usuario autenticado, independiente de los detalles
 * internos del mecanismo de seguridad utilizado (Spring Security).
 * <p>
 * Las capas de negocio de la aplicacion consumidora deben depender de este servicio en lugar de
 * acceder directamente a {@code SecurityContextHolder}, {@code Authentication} o
 * {@code Principal}.
 */
public interface CurrentUserService {

    /**
     * @return {@code true} si hay un usuario autenticado en el contexto actual.
     */
    boolean isAuthenticated();

    /**
     * @return el identificador tecnico (login) del usuario autenticado, o {@code null} si no hay
     * usuario autenticado.
     */
    String getUsername();

    /**
     * @return el nombre para mostrar del usuario autenticado. Si el proveedor de autenticacion no
     * lo proporciona, puede devolver el mismo valor que {@link #getUsername()}.
     */
    String getDisplayName();

    /**
     * @return el correo electronico del usuario autenticado, si esta disponible.
     */
    String getEmail();

    /**
     * @return los roles (sin el prefijo {@code ROLE_}) asignados al usuario autenticado.
     */
    Set<String> getRoles();

    /**
     * @return todas las authorities (roles incluidos) asignadas al usuario autenticado.
     */
    Set<String> getAuthorities();

    /**
     * @param role nombre del rol, sin el prefijo {@code ROLE_}.
     * @return {@code true} si el usuario autenticado tiene el rol indicado.
     */
    boolean hasRole(String role);

    /**
     * @param authority nombre exacto de la authority.
     * @return {@code true} si el usuario autenticado tiene la authority indicada.
     */
    boolean hasAuthority(String authority);
}
