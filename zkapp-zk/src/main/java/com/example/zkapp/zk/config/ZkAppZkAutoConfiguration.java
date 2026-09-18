package com.example.zkapp.zk.config;

import com.example.zkapp.core.config.ZkAppAutoConfiguration;
import com.example.zkapp.zk.servlet.FallbackNotFoundServlet;
import com.example.zkapp.zk.servlet.ZulForwardServlet;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.zkoss.zk.au.http.DHtmlUpdateServlet;
import org.zkoss.zk.ui.http.DHtmlLayoutServlet;
import org.zkoss.zk.ui.http.HttpSessionListener;

/**
 * Registra la infraestructura ZK comun (servlets, listener de sesion y rutas amigables de
 * login/logout) sin requerir un {@code web.xml} en la aplicacion consumidora.
 */
@AutoConfiguration
@AutoConfigureAfter(ZkAppAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ZkAppZkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "zkLoaderServlet")
    public ServletRegistrationBean<DHtmlLayoutServlet> zkLoaderServlet() {
        ServletRegistrationBean<DHtmlLayoutServlet> registration =
                new ServletRegistrationBean<>(new DHtmlLayoutServlet(), "*.zul", "*.zhtml");
        registration.setName("zkLoader");
        registration.setLoadOnStartup(1);
        registration.addInitParameter("update-uri", "/zkau");
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "zkAuEngineServlet")
    public ServletRegistrationBean<DHtmlUpdateServlet> zkAuEngineServlet() {
        ServletRegistrationBean<DHtmlUpdateServlet> registration =
                new ServletRegistrationBean<>(new DHtmlUpdateServlet(), "/zkau/*");
        registration.setName("auEngine");
        registration.setLoadOnStartup(1);
        return registration;
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> zkHttpSessionListener() {
        return new ServletListenerRegistrationBean<>(new HttpSessionListener());
    }

    @Bean
    @ConditionalOnMissingBean(name = "loginPageServlet")
    public ServletRegistrationBean<ZulForwardServlet> loginPageServlet() {
        ServletRegistrationBean<ZulForwardServlet> registration =
                new ServletRegistrationBean<>(new ZulForwardServlet("/login.zul"), "/login");
        registration.setName("zkappLoginForward");
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(name = "accessDeniedPageServlet")
    public ServletRegistrationBean<ZulForwardServlet> accessDeniedPageServlet() {
        ServletRegistrationBean<ZulForwardServlet> registration =
                new ServletRegistrationBean<>(new ZulForwardServlet("/403.zul"), "/403");
        registration.setName("zkappAccessDeniedForward");
        return registration;
    }

    /**
     * Sin un servlet mapeado a {@code "/"}, el contenedor puede devolver 404 antes de invocar la
     * cadena de filtros para rutas gestionadas unicamente por un filtro (por ejemplo
     * {@code /logout}, o los endpoints OAuth2 de la integracion Microsoft). Vease
     * {@link FallbackNotFoundServlet}.
     */
    @Bean
    @ConditionalOnMissingBean(name = "defaultFallbackServlet")
    public ServletRegistrationBean<FallbackNotFoundServlet> defaultFallbackServlet() {
        ServletRegistrationBean<FallbackNotFoundServlet> registration =
                new ServletRegistrationBean<>(new FallbackNotFoundServlet(), "/");
        registration.setName("zkappFallback");
        return registration;
    }
}
