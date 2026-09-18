package com.example.zkapp.core.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ZkAppPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ZkAppAutoConfiguration.class));

    @Test
    void loadsDefaultValues() {
        contextRunner.run(context -> {
            ZkAppProperties properties = context.getBean(ZkAppProperties.class);
            assertThat(properties.getApplication().getName()).isEqualTo("ZK Application");
            assertThat(properties.getApplication().getCode()).isEqualTo("APP");
            assertThat(properties.getUi().getHomePage()).isEqualTo("/index.zul");
            assertThat(properties.getSecurity().getAuthentication().getProvider())
                    .isEqualTo(AuthenticationProvider.LDAP);
            assertThat(properties.getSecurity().getLdap().isEnabled()).isFalse();
            assertThat(properties.getSecurity().getMicrosoft().isEnabled()).isFalse();
            assertThat(properties.getSession().getTimeout()).isEqualTo(Duration.ofMinutes(30));
            assertThat(properties.getAudit().isEnabled()).isTrue();
        });
    }

    @Test
    void appliesOverrides() {
        contextRunner
                .withPropertyValues(
                        "zkapp.application.name=Mi Aplicacion",
                        "zkapp.application.code=MIAPP",
                        "zkapp.ui.home-page=/custom/index.zul",
                        "zkapp.security.authentication.provider=microsoft",
                        "zkapp.session.timeout=15m"
                )
                .run(context -> {
                    ZkAppProperties properties = context.getBean(ZkAppProperties.class);
                    assertThat(properties.getApplication().getName()).isEqualTo("Mi Aplicacion");
                    assertThat(properties.getApplication().getCode()).isEqualTo("MIAPP");
                    assertThat(properties.getUi().getHomePage()).isEqualTo("/custom/index.zul");
                    assertThat(properties.getSecurity().getAuthentication().getProvider())
                            .isEqualTo(AuthenticationProvider.MICROSOFT);
                    assertThat(properties.getSession().getTimeout()).isEqualTo(Duration.ofMinutes(15));
                });
    }

    @Test
    void failsFastWhenApplicationCodeIsBlank() {
        contextRunner
                .withPropertyValues("zkapp.application.code=")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void effectiveUiTitleFallsBackToApplicationName() {
        ZkAppProperties properties = new ZkAppProperties();
        properties.getApplication().setName("Mi Aplicacion");
        assertThat(properties.getEffectiveUiTitle()).isEqualTo("Mi Aplicacion");

        properties.getUi().setTitle("Titulo custom");
        assertThat(properties.getEffectiveUiTitle()).isEqualTo("Titulo custom");
    }
}
