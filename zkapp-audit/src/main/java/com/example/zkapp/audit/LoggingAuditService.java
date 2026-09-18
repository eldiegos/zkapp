package com.example.zkapp.audit;

import com.example.zkapp.core.api.AuditService;
import com.example.zkapp.core.audit.AuditEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Implementacion por defecto de {@link AuditService}: registra cada evento en un logger dedicado
 * ({@code com.example.zkapp.audit}), desacoplada de cualquier mecanismo de persistencia.
 * <p>
 * Una aplicacion que necesite persistir la auditoria (por ejemplo en SQL Server via JPA) puede
 * sustituir este bean, o anadir su propio {@code @EventListener} sobre los eventos tecnicos que
 * publica {@code zkapp-security} y {@code spring-security-core}, sin modificar este modulo.
 */
public class LoggingAuditService implements AuditService {

    private static final Logger log = LoggerFactory.getLogger("com.example.zkapp.audit");
    private static final Marker AUDIT_MARKER = MarkerFactory.getMarker("ZKAPP_AUDIT");

    @Override
    public void record(AuditEvent event) {
        log.info(AUDIT_MARKER, "[{}] application={} user={} detail={} metadata={} timestamp={}",
                event.type(), event.applicationCode(), event.username(), event.detail(), event.metadata(),
                event.timestamp());
    }
}
