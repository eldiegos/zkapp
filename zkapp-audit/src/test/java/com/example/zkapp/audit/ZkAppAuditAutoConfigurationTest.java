package com.example.zkapp.audit;

import com.example.zkapp.core.api.AuditService;
import com.example.zkapp.core.audit.AuditEvent;
import com.example.zkapp.core.audit.AuditEventType;
import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import com.example.zkapp.core.event.AccessDeniedAuditEvent;
import com.example.zkapp.core.event.LogoutEvent;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ZkAppAuditAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ZkAppAutoConfiguration.class, ZkAppAuditAutoConfiguration.class));

    @Test
    void registersDefaultLoggingAuditService() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(LoggingAuditService.class));
    }

    @Test
    void canBeDisabled() {
        contextRunner.withPropertyValues("zkapp.audit.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(AuditService.class));
    }

    @Test
    void allowsReplacingAuditService() {
        contextRunner.withUserConfiguration(RecordingAuditServiceConfiguration.class).run(context -> {
            assertThat(context.getBean(AuditService.class)).isInstanceOf(RecordingAuditService.class);
        });
    }

    @Test
    void translatesLogoutAndAccessDeniedEvents() {
        contextRunner.withUserConfiguration(RecordingAuditServiceConfiguration.class)
                .withPropertyValues("zkapp.application.code=TEST")
                .run(context -> {
                    RecordingAuditService auditService = (RecordingAuditService) context.getBean(AuditService.class);
                    context.publishEvent(new LogoutEvent(this, "alice"));
                    context.publishEvent(new AccessDeniedAuditEvent(this, "alice", "/secret.zul"));

                    assertThat(auditService.events).hasSize(2);
                    assertThat(auditService.events.get(0).type()).isEqualTo(AuditEventType.LOGOUT);
                    assertThat(auditService.events.get(0).applicationCode()).isEqualTo("TEST");
                    assertThat(auditService.events.get(1).type()).isEqualTo(AuditEventType.ACCESS_DENIED);
                });
    }

    @Configuration
    static class RecordingAuditServiceConfiguration {
        @Bean
        AuditService auditService() {
            return new RecordingAuditService();
        }
    }

    static class RecordingAuditService implements AuditService {
        final List<AuditEvent> events = new CopyOnWriteArrayList<>();

        @Override
        public void record(AuditEvent event) {
            events.add(event);
        }
    }
}
