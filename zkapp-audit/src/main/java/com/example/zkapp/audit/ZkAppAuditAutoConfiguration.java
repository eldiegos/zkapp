package com.example.zkapp.audit;

import com.example.zkapp.core.api.AuditService;
import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import com.example.zkapp.core.config.ZkAppProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * AutoConfiguration de auditoria tecnica. Se activa por defecto ({@code zkapp.audit.enabled=true}).
 */
@AutoConfiguration
@AutoConfigureAfter(ZkAppAutoConfiguration.class)
@ConditionalOnProperty(prefix = "zkapp.audit", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ZkAppAuditAutoConfiguration {

    /**
     * Punto de extension: una aplicacion puede sustituir la implementacion por defecto (logging)
     * por una que persista la auditoria, proporcionando su propio bean {@link AuditService}.
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditService auditService() {
        return new LoggingAuditService();
    }

    @Bean
    public SecurityAuditEventListener securityAuditEventListener(AuditService auditService, ZkAppProperties properties) {
        return new SecurityAuditEventListener(auditService, properties);
    }
}
