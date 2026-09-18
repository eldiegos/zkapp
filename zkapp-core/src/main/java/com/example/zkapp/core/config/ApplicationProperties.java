package com.example.zkapp.core.config;

import jakarta.validation.constraints.NotBlank;

/**
 * Identidad de la aplicacion consumidora del starter.
 */
public class ApplicationProperties {

    /**
     * Nombre visible de la aplicacion, usado por defecto en la UI comun.
     */
    @NotBlank
    private String name = "ZK Application";

    /**
     * Codigo corto que identifica inequivocamente la aplicacion (logging, auditoria, etc).
     */
    @NotBlank
    private String code = "APP";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
