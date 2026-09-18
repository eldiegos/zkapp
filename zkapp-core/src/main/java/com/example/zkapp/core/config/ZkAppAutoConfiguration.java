package com.example.zkapp.core.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

/**
 * Habilita la configuracion tipada raiz de la plataforma ({@code zkapp.*}).
 * <p>
 * El resto de AutoConfigurations de la plataforma ({@code zkapp-session}, {@code zkapp-security},
 * {@code zkapp-audit}, {@code zkapp-zk}) se ordenan despues de esta mediante
 * {@code @AutoConfigureAfter} y consumen {@link ZkAppProperties} como bean ya disponible.
 * <p>
 * {@code @EnableConfigurationProperties} se usa sin argumentos unicamente para registrar de forma
 * autonoma la infraestructura de binding/validacion ({@code ConfigurationPropertiesBindingPostProcessor}),
 * sin depender de que {@code ConfigurationPropertiesAutoConfiguration} tambien este presente en el
 * contexto (por ejemplo en un test que carga esta AutoConfiguration de forma aislada). El bean en
 * si se declara explicitamente mas abajo para obtener el nombre predecible {@code zkAppProperties},
 * usado por las paginas ZUL del modulo {@code zkapp-zk} para resolver
 * {@code ${zkAppProperties....}} mediante {@code DelegatingVariableResolver}.
 */
@AutoConfiguration
@EnableConfigurationProperties
public class ZkAppAutoConfiguration {

    @Bean(name = "zkAppProperties")
    @ConfigurationProperties(prefix = "zkapp")
    @ConditionalOnMissingBean
    @Validated
    public ZkAppProperties zkAppProperties() {
        return new ZkAppProperties();
    }
}
