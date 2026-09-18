package com.example.zkapp.zk.config;

import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class ZkAppZkAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ZkAppAutoConfiguration.class, ZkAppZkAutoConfiguration.class));

    @Test
    void registersZkServletsAndFriendlyRoutes() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("zkLoaderServlet");
            assertThat(context).hasBean("zkAuEngineServlet");
            assertThat(context).hasBean("zkHttpSessionListener");
            assertThat(context).hasBean("loginPageServlet");
            assertThat(context).hasBean("accessDeniedPageServlet");
        });
    }
}
