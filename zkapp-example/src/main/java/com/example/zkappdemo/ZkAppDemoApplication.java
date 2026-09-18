package com.example.zkappdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Aplicacion de ejemplo minima. Unicamente consume {@code zkapp-spring-boot-starter} y aporta un
 * bean de autenticacion de demostracion (vease {@link DemoSecurityConfiguration}): no reimplementa
 * login, logout, {@code SecurityFilterChain}, {@code SessionService}, {@code CurrentUserService}
 * ni la configuracion ZK comun, todo ello proporcionado por el Starter.
 */
@SpringBootApplication
public class ZkAppDemoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ZkAppDemoApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(ZkAppDemoApplication.class);
    }
}
