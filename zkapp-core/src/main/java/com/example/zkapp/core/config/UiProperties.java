package com.example.zkapp.core.config;

import jakarta.validation.constraints.NotBlank;

/**
 * Configuracion de la UI comun proporcionada por el starter.
 */
public class UiProperties {

    /**
     * Ruta, dentro del contexto de la aplicacion, a la que se navega tras un login correcto.
     * NO debe incluir el context path del WAR.
     */
    @NotBlank
    private String homePage = "/index.zul";

    /**
     * Titulo mostrado en la cabecera de la UI comun. Si no se establece, se usa
     * {@link ApplicationProperties#getName()}.
     */
    private String title;

    /**
     * URL de un logo opcional mostrado en la cabecera de la UI comun.
     */
    private String logoUrl;

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
