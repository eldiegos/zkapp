package com.example.zkapp.core.api;

import com.example.zkapp.core.audit.AuditEvent;

/**
 * API publica para registrar eventos de auditoria tecnica.
 * <p>
 * La implementacion por defecto esta desacoplada de cualquier mecanismo de persistencia: unicamente
 * publica el evento como {@code ApplicationEvent} de Spring y lo registra mediante logging. Una
 * aplicacion consumidora que necesite persistir la auditoria puede sustituir el bean
 * {@code AuditService} o escuchar el evento de dominio publicado.
 */
public interface AuditService {

    /**
     * Registra un evento de auditoria tecnica.
     */
    void record(AuditEvent event);
}
