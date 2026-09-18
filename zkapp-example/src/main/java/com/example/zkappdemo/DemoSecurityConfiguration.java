package com.example.zkappdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

/**
 * Usuarios de demostracion, unicamente para poder ejecutar la aplicacion de ejemplo sin depender
 * de un servidor LDAP real.
 * <p>
 * Esto demuestra el punto de extension descrito en la documentacion del Starter: al declarar aqui
 * un bean {@link AuthenticationProvider}, la configuracion LDAP por defecto de
 * {@code zkapp-security} retrocede automaticamente (via {@code @ConditionalOnMissingBean}) sin
 * necesidad de tocar el Starter. Una aplicacion real simplemente apuntaria
 * {@code zkapp.security.ldap.*} a su directorio corporativo y no necesitaria esta clase.
 */
@Configuration(proxyBeanMethods = false)
public class DemoSecurityConfiguration {

    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(demoUserDetailsService(passwordEncoder));
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private UserDetailsService demoUserDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("demo")
                        .password(passwordEncoder.encode("demo"))
                        .roles("USER")
                        .build(),
                User.withUsername("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles("USER", "ADMIN")
                        .build());
    }
}
