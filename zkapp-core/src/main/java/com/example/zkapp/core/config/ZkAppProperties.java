package com.example.zkapp.core.config;

import jakarta.validation.Valid;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Raiz de la configuracion tipada de la plataforma zkapp. Todas las propiedades propias de la
 * plataforma cuelgan exclusivamente del namespace {@code zkapp.*}.
 * <p>
 * Esta clase, y sus propiedades anidadas, forman parte de la API publica del Starter: las
 * aplicaciones consumidoras pueden inyectar {@link ZkAppProperties} para leer, por ejemplo, el
 * nombre y codigo de la aplicacion desde codigo Java o desde ViewModels ZK.
 */
@ConfigurationProperties(prefix = "zkapp")
@Validated
public class ZkAppProperties {

    @NestedConfigurationProperty
    @Valid
    private ApplicationProperties application = new ApplicationProperties();

    @NestedConfigurationProperty
    @Valid
    private UiProperties ui = new UiProperties();

    @NestedConfigurationProperty
    @Valid
    private SecurityProperties security = new SecurityProperties();

    @NestedConfigurationProperty
    @Valid
    private SessionProperties session = new SessionProperties();

    @NestedConfigurationProperty
    @Valid
    private AuditProperties audit = new AuditProperties();

    @NestedConfigurationProperty
    @Valid
    private LoggingProperties logging = new LoggingProperties();

    public ApplicationProperties getApplication() {
        return application;
    }

    public void setApplication(ApplicationProperties application) {
        this.application = application;
    }

    public UiProperties getUi() {
        return ui;
    }

    public void setUi(UiProperties ui) {
        this.ui = ui;
    }

    public SecurityProperties getSecurity() {
        return security;
    }

    public void setSecurity(SecurityProperties security) {
        this.security = security;
    }

    public SessionProperties getSession() {
        return session;
    }

    public void setSession(SessionProperties session) {
        this.session = session;
    }

    public AuditProperties getAudit() {
        return audit;
    }

    public void setAudit(AuditProperties audit) {
        this.audit = audit;
    }

    public LoggingProperties getLogging() {
        return logging;
    }

    public void setLogging(LoggingProperties logging) {
        this.logging = logging;
    }

    /**
     * Titulo efectivo de la UI: {@code zkapp.ui.title} si esta definido, o en su defecto
     * {@code zkapp.application.name}.
     */
    public String getEffectiveUiTitle() {
        return (ui.getTitle() != null && !ui.getTitle().isBlank()) ? ui.getTitle() : application.getName();
    }
}
