package com.example.zkapp.core.config;

/**
 * Configuracion del proveedor de autenticacion LDAP.
 * <p>
 * No se asume ninguna estructura de directorio concreta: todas las rutas de busqueda son
 * configurables por la aplicacion consumidora.
 */
public class LdapProperties {

    /**
     * Activa la autenticacion LDAP. Solo tiene efecto si
     * {@code zkapp.security.authentication.provider=ldap}.
     */
    private boolean enabled = false;

    /**
     * URL del servidor LDAP, por ejemplo {@code ldap://ldap.example.com:389}.
     */
    private String url;

    /**
     * Base DN del directorio, por ejemplo {@code dc=example,dc=com}.
     */
    private String base;

    /**
     * Base de busqueda de usuarios, relativa al base DN.
     */
    private String userSearchBase = "";

    /**
     * Filtro de busqueda de usuarios, por ejemplo {@code (uid={0})}.
     */
    private String userSearchFilter = "(uid={0})";

    /**
     * DN del usuario tecnico (manager) usado para las busquedas, si el directorio no permite
     * bind anonimo.
     */
    private String managerDn;

    /**
     * Password del usuario tecnico. Debe externalizarse mediante variables de entorno o un
     * gestor de secretos, nunca hardcodearse.
     */
    private String managerPassword;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getUserSearchBase() {
        return userSearchBase;
    }

    public void setUserSearchBase(String userSearchBase) {
        this.userSearchBase = userSearchBase;
    }

    public String getUserSearchFilter() {
        return userSearchFilter;
    }

    public void setUserSearchFilter(String userSearchFilter) {
        this.userSearchFilter = userSearchFilter;
    }

    public String getManagerDn() {
        return managerDn;
    }

    public void setManagerDn(String managerDn) {
        this.managerDn = managerDn;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }
}
