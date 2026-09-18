package com.example.zkapp.security.config;

import com.example.zkapp.core.api.CurrentUserService;
import com.example.zkapp.core.config.AuthenticationProvider;
import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import com.example.zkapp.core.config.ZkAppProperties;
import com.example.zkapp.security.auth.ZkAppAuthenticationConfigurer;
import com.example.zkapp.security.auth.ldap.LdapAuthenticationConfiguration;
import com.example.zkapp.security.auth.microsoft.MicrosoftAuthenticationConfiguration;
import com.example.zkapp.security.user.DefaultCurrentUserService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * AutoConfiguration central de seguridad: registra {@link CurrentUserService}, publica eventos de
 * autenticacion y construye la {@link SecurityFilterChain} comun (login, logout, sesion, acceso
 * denegado, RBAC) delegando el mecanismo concreto de autenticacion en el
 * {@link ZkAppAuthenticationConfigurer} activo.
 * <p>
 * Una aplicacion consumidora que necesite personalizar la seguridad mas alla de lo que exponen las
 * propiedades {@code zkapp.security.*} puede sustituir cualquiera de estos beans, o declarar su
 * propio bean {@link SecurityFilterChain}, que hace retroceder por completo a esta
 * AutoConfiguration.
 */
@AutoConfiguration
@AutoConfigureAfter(ZkAppAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableWebSecurity
@Import({LdapAuthenticationConfiguration.class, MicrosoftAuthenticationConfiguration.class})
public class ZkAppSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CurrentUserService currentUserService() {
        return new DefaultCurrentUserService();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain zkAppSecurityFilterChain(
            HttpSecurity http,
            ZkAppProperties properties,
            ObjectProvider<ZkAppAuthenticationConfigurer> authenticationConfigurers,
            ApplicationEventPublisher eventPublisher,
            CurrentUserService currentUserService) throws Exception {

        AuthenticationProvider activeProvider = properties.getSecurity().getAuthentication().getProvider();
        List<ZkAppAuthenticationConfigurer> configurers = authenticationConfigurers.orderedStream().toList();
        ZkAppAuthenticationConfigurer configurer = configurers.stream()
                .filter(c -> c.supportedProvider() == activeProvider)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No hay ningun ZkAppAuthenticationConfigurer registrado para zkapp.security.authentication.provider="
                                + activeProvider));

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/login", "/login.zul", "/403", "/403.zul", "/error",
                                "/zkau/**", "/zkres/**", "/zkapp/**",
                                "/img/**", "/css/**", "/js/**", "/favicon.ico")
                        .permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/zkau/**"))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(new ZkAppLogoutSuccessHandler(eventPublisher))
                        .invalidateHttpSession(properties.getSession().isInvalidateOnLogout())
                        .clearAuthentication(true))
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(new ZkAppAccessDeniedHandler(eventPublisher, currentUserService)))
                .sessionManagement(session -> session
                        .sessionFixation(fixation -> fixation.migrateSession()));

        configurer.configure(http);

        return http.build();
    }
}
