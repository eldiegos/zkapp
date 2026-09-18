package com.example.zkapp.core.audit;

import java.time.Instant;
import java.util.Map;

/**
 * Evento de auditoria tecnica inmutable.
 *
 * @param type            tipo de evento.
 * @param username        usuario relacionado con el evento, o {@code null} si no se conoce
 *                        (por ejemplo un intento de login con un usuario inexistente).
 * @param applicationCode codigo de la aplicacion en la que se origino el evento
 *                        ({@code zkapp.application.code}).
 * @param detail          detalle legible del evento.
 * @param metadata        metadatos adicionales (por ejemplo direccion IP, user-agent, recurso
 *                        solicitado). Nunca debe contener secretos ni credenciales.
 * @param timestamp       instante en el que ocurrio el evento.
 */
public record AuditEvent(
        AuditEventType type,
        String username,
        String applicationCode,
        String detail,
        Map<String, String> metadata,
        Instant timestamp
) {

    public AuditEvent {
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (metadata == null) {
            metadata = Map.of();
        }
        if (timestamp == null) {
            timestamp = Instant.now();
        }
    }

    public static AuditEvent of(AuditEventType type, String username, String applicationCode, String detail) {
        return new AuditEvent(type, username, applicationCode, detail, Map.of(), Instant.now());
    }
}
