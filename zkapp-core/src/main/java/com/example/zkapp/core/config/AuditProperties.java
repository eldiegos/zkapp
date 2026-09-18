package com.example.zkapp.core.config;

/**
 * Configuracion de la auditoria tecnica proporcionada por la plataforma.
 */
public class AuditProperties {

    /**
     * Activa la auditoria tecnica (login, logout, acceso denegado, etc).
     */
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
