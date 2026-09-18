package com.example.zkapp.session;

import com.example.zkapp.core.api.CurrentUserService;
import com.example.zkapp.core.api.SessionService;
import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import com.example.zkapp.core.config.ZkAppProperties;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.RequestContextFilter;

import java.util.NoSuchElementException;

/**
 * AutoConfiguration del modulo de sesion: {@link com.example.zkapp.core.api.SessionService} y
 * enriquecimiento de logging tecnico mediante MDC.
 */
@AutoConfiguration
@AutoConfigureAfter(ZkAppAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ZkAppSessionAutoConfiguration {

    /**
     * Punto de extension: una aplicacion puede sustituir la gestion de sesion proporcionando su
     * propio bean {@link com.example.zkapp.core.api.SessionService}.
     */
    @Bean
    @ConditionalOnMissingBean(SessionService.class)
    public HttpSessionService sessionService() {
        return new HttpSessionService();
    }

    /**
     * Publica los atributos de la peticion actual en {@link org.springframework.web.context.request.RequestContextHolder}
     * incluso cuando la peticion es atendida por el servlet de ZK (que no pasa por Spring MVC).
     */
    @Bean
    public FilterRegistrationBean<RequestContextFilter> zkAppRequestContextFilter() {
        FilterRegistrationBean<RequestContextFilter> registration =
                new FilterRegistrationBean<>(new RequestContextFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        registration.setName("zkappRequestContextFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<Filter> loggingContextFilter(
            ZkAppProperties properties, ObjectProvider<CurrentUserService> currentUserService) {
        LoggingContextFilter filter = new LoggingContextFilter(properties, currentUserServiceOrNull(currentUserService));
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        registration.addUrlPatterns("/*");
        registration.setName("zkappLoggingContextFilter");
        return registration;
    }

    private CurrentUserService currentUserServiceOrNull(ObjectProvider<CurrentUserService> provider) {
        try {
            return provider.getIfAvailable();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }
}
