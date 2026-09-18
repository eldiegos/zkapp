package com.example.zkapp.security.auth.microsoft;

import com.example.zkapp.core.config.AuthenticationProvider;
import com.example.zkapp.core.config.MicrosoftProperties;
import com.example.zkapp.core.config.ZkAppProperties;
import com.example.zkapp.security.auth.ZkAppAuthenticationConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

/**
 * Configura la autenticacion Microsoft (Microsoft Entra ID / Azure AD, OAuth2/OIDC) cuando
 * {@code zkapp.security.authentication.provider=microsoft}.
 * <p>
 * El {@link ClientRegistration} se construye programaticamente a partir de
 * {@code zkapp.security.microsoft.*}, en lugar de depender del namespace estandar
 * {@code spring.security.oauth2.client.*}, para mantener {@code zkapp.*} como unico namespace de
 * configuracion de la plataforma.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "zkapp.security.authentication", name = "provider", havingValue = "microsoft")
@ConditionalOnProperty(prefix = "zkapp.security.microsoft", name = "enabled", havingValue = "true")
public class MicrosoftAuthenticationConfiguration {

    private static final String REGISTRATION_ID = "microsoft";

    @Bean
    @ConditionalOnMissingBean(ClientRegistrationRepository.class)
    public ClientRegistrationRepository clientRegistrationRepository(ZkAppProperties properties) {
        return new InMemoryClientRegistrationRepository(microsoftClientRegistration(properties.getSecurity().getMicrosoft()));
    }

    @Bean
    public ZkAppAuthenticationConfigurer microsoftAuthenticationConfigurer(ZkAppProperties properties) {
        String homePage = properties.getUi().getHomePage();
        return new ZkAppAuthenticationConfigurer() {
            @Override
            public AuthenticationProvider supportedProvider() {
                return AuthenticationProvider.MICROSOFT;
            }

            @Override
            public void configure(HttpSecurity http) throws Exception {
                http.oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl(homePage, true));
            }
        };
    }

    private ClientRegistration microsoftClientRegistration(MicrosoftProperties microsoft) {
        String authority = "https://login.microsoftonline.com/" + microsoft.getTenantId();
        String[] scopes = microsoft.getScopes().split(",");
        for (int i = 0; i < scopes.length; i++) {
            scopes[i] = scopes[i].trim();
        }
        return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientId(microsoft.getClientId())
                .clientSecret(microsoft.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(scopes)
                .authorizationUri(authority + "/oauth2/v2.0/authorize")
                .tokenUri(authority + "/oauth2/v2.0/token")
                .jwkSetUri(authority + "/discovery/v2.0/keys")
                .issuerUri(authority + "/v2.0")
                .userNameAttributeName("sub")
                .clientName("Microsoft")
                .build();
    }
}
