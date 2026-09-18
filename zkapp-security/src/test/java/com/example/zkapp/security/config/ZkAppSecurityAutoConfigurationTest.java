package com.example.zkapp.security.config;

import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ZkAppSecurityAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ZkAppAutoConfiguration.class, ZkAppSecurityAutoConfiguration.class))
            .withUserConfiguration(StubAuthenticationProviderConfiguration.class)
            .withPropertyValues(
                    "zkapp.security.authentication.provider=ldap",
                    "zkapp.security.ldap.enabled=true");

    @Test
    void registersSecurityFilterChain() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(SecurityFilterChain.class));
    }

    @Test
    void unauthenticatedRequestIsRedirectedToLogin() {
        contextRunner.run(context -> {
            MockMvc mockMvc = mockMvc(context);
            mockMvc.perform(get("/index.zul"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        });
    }

    @Test
    void loginPageIsPubliclyAccessible() {
        contextRunner.run(context -> {
            MockMvc mockMvc = mockMvc(context);
            // No ZK servlet registered in this unit test context: a 404 (page not found) proves
            // the request reached the servlet layer instead of being redirected by Spring Security.
            mockMvc.perform(get("/login")).andExpect(status().is4xxClientError());
        });
    }

    @Test
    void logoutRedirectsToLoginWithLogoutParam() {
        contextRunner.run(context -> {
            MockMvc mockMvc = mockMvc(context);
            mockMvc.perform(post("/logout").with(user("alice")).with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?logout"));
        });
    }

    private MockMvc mockMvc(WebApplicationContext context) {
        return MockMvcBuilders.webAppContextSetup(context).apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Configuration
    static class StubAuthenticationProviderConfiguration {
        @Bean
        AuthenticationProvider testingAuthenticationProvider() {
            return new TestingAuthenticationProvider();
        }
    }
}
