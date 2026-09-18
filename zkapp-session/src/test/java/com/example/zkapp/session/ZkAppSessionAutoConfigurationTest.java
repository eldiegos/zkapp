package com.example.zkapp.session;

import com.example.zkapp.core.api.SessionService;
import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class ZkAppSessionAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ZkAppAutoConfiguration.class, ZkAppSessionAutoConfiguration.class));

    @Test
    void registersDefaultSessionService() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SessionService.class);
            assertThat(context).hasSingleBean(HttpSessionService.class);
        });
    }

    @Test
    void allowsReplacingSessionService() {
        contextRunner.withUserConfiguration(CustomSessionServiceConfiguration.class).run(context -> {
            assertThat(context).hasSingleBean(SessionService.class);
            assertThat(context.getBean(SessionService.class)).isInstanceOf(CustomSessionService.class);
        });
    }

    @Configuration
    static class CustomSessionServiceConfiguration {
        @Bean
        SessionService sessionService() {
            return new CustomSessionService();
        }
    }

    static class CustomSessionService implements SessionService {
        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public String getSessionId() {
            return null;
        }

        @Override
        public void invalidate() {
        }

        @Override
        public <T> void set(String key, T value) {
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            return null;
        }
    }
}
