package com.example.zkapp.core.config;

/**
 * Configuracion del proveedor de autenticacion Microsoft (Microsoft Entra ID / Azure AD),
 * basado en OAuth2/OIDC.
 */
public class MicrosoftProperties {

    /**
     * Activa la autenticacion Microsoft. Solo tiene efecto si
     * {@code zkapp.security.authentication.provider=microsoft}.
     */
    private boolean enabled = false;

    /**
     * Identificador del tenant de Microsoft Entra ID.
     */
    private String tenantId;

    /**
     * Client ID de la aplicacion registrada en Microsoft Entra ID.
     */
    private String clientId;

    /**
     * Client secret de la aplicacion. Nunca debe hardcodearse: use variables de entorno o un
     * gestor de secretos, por ejemplo {@code ${MICROSOFT_CLIENT_SECRET}}.
     */
    private String clientSecret;

    /**
     * Scopes solicitados durante la autenticacion OAuth2/OIDC.
     */
    private String scopes = "openid,profile,email";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getScopes() {
        return scopes;
    }

    public void setScopes(String scopes) {
        this.scopes = scopes;
    }
}
