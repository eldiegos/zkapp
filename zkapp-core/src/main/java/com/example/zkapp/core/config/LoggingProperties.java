package com.example.zkapp.core.config;

/**
 * Configuracion del enriquecimiento de logging tecnico (MDC) proporcionado por la plataforma.
 * <p>
 * No acopla la plataforma a ninguna infraestructura de logging concreta (ELK, Logstash, etc.):
 * unicamente puebla el {@code MDC} estandar de SLF4J para que la aplicacion configure sus propios
 * appenders/patrones.
 */
public class LoggingProperties {

    /**
     * Incluye el codigo de aplicacion ({@code zkapp.application.code}) en el MDC.
     */
    private boolean includeApplicationCode = true;

    /**
     * Incluye el usuario autenticado en el MDC.
     */
    private boolean includeUser = true;

    /**
     * Incluye el identificador de sesion en el MDC.
     */
    private boolean includeSessionId = true;

    /**
     * Incluye un correlation-id (generado o propagado por cabecera HTTP) en el MDC.
     */
    private boolean includeCorrelationId = true;

    /**
     * Nombre de la cabecera HTTP usada para propagar el correlation-id entrante.
     */
    private String correlationIdHeader = "X-Correlation-Id";

    public boolean isIncludeApplicationCode() {
        return includeApplicationCode;
    }

    public void setIncludeApplicationCode(boolean includeApplicationCode) {
        this.includeApplicationCode = includeApplicationCode;
    }

    public boolean isIncludeUser() {
        return includeUser;
    }

    public void setIncludeUser(boolean includeUser) {
        this.includeUser = includeUser;
    }

    public boolean isIncludeSessionId() {
        return includeSessionId;
    }

    public void setIncludeSessionId(boolean includeSessionId) {
        this.includeSessionId = includeSessionId;
    }

    public boolean isIncludeCorrelationId() {
        return includeCorrelationId;
    }

    public void setIncludeCorrelationId(boolean includeCorrelationId) {
        this.includeCorrelationId = includeCorrelationId;
    }

    public String getCorrelationIdHeader() {
        return correlationIdHeader;
    }

    public void setCorrelationIdHeader(String correlationIdHeader) {
        this.correlationIdHeader = correlationIdHeader;
    }
}
