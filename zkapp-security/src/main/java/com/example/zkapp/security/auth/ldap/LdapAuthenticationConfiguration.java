package com.example.zkapp.security.auth.ldap;

import com.example.zkapp.core.config.AuthenticationProvider;
import com.example.zkapp.core.config.LdapProperties;
import com.example.zkapp.core.config.ZkAppProperties;
import com.example.zkapp.security.auth.ZkAppAuthenticationConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

import java.util.List;

/**
 * Configura la autenticacion LDAP cuando {@code zkapp.security.authentication.provider=ldap}.
 * <p>
 * No se asume ninguna estructura de directorio concreta: la base DN, la base de busqueda y el
 * filtro de busqueda son totalmente configurables mediante {@code zkapp.security.ldap.*}.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "zkapp.security.authentication", name = "provider", havingValue = "ldap")
@ConditionalOnProperty(prefix = "zkapp.security.ldap", name = "enabled", havingValue = "true")
public class LdapAuthenticationConfiguration {

    @Bean
    @ConditionalOnMissingBean(org.springframework.security.authentication.AuthenticationProvider.class)
    public org.springframework.security.authentication.AuthenticationProvider ldapAuthenticationProvider(
            ZkAppProperties properties) {
        LdapProperties ldap = properties.getSecurity().getLdap();

        DefaultSpringSecurityContextSource contextSource =
                new DefaultSpringSecurityContextSource(List.of(ldap.getUrl()), ldap.getBase());
        if (ldap.getManagerDn() != null && !ldap.getManagerDn().isBlank()) {
            contextSource.setUserDn(ldap.getManagerDn());
            contextSource.setPassword(ldap.getManagerPassword());
        }
        contextSource.afterPropertiesSet();

        LdapUserSearch userSearch = new FilterBasedLdapUserSearch(
                ldap.getUserSearchBase(), ldap.getUserSearchFilter(), contextSource);

        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(userSearch);

        return new LdapAuthenticationProvider(authenticator);
    }

    @Bean
    public ZkAppAuthenticationConfigurer ldapAuthenticationConfigurer(ZkAppProperties properties) {
        String homePage = properties.getUi().getHomePage();
        return new ZkAppAuthenticationConfigurer() {
            @Override
            public AuthenticationProvider supportedProvider() {
                return AuthenticationProvider.LDAP;
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {
                http.formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl(homePage, true)
                        .failureUrl("/login?error"));
            }
        };
    }
}
