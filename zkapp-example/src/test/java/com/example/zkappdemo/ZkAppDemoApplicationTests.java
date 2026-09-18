package com.example.zkappdemo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de extremo a extremo (a nivel de filtros de seguridad) de la aplicacion de ejemplo:
 * demuestra que el flujo login -> pagina protegida -> logout funciona unicamente con lo que aporta
 * zkapp-spring-boot-starter, sin codigo adicional de seguridad en la aplicacion (salvo el
 * {@link DemoSecurityConfiguration}, que sustituye el proveedor de autenticacion por usuarios en
 * memoria para poder ejecutarse sin un LDAP real).
 * <p>
 * {@code MockMvc} despacha internamente contra un {@code DispatcherServlet}, no contra los
 * servlets de ZK registrados por {@code zkapp-zk} (que no son Spring MVC). Por eso estas pruebas
 * verifican el comportamiento de la cadena de filtros de seguridad (redirecciones, autorizacion)
 * en lugar del renderizado real de las paginas ZUL, que se valida manualmente arrancando la
 * aplicacion (vease el README, seccion "Aplicacion de ejemplo").
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ZkAppDemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedRequestToHomePageIsRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/index.zul"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void loginPageIsNotRedirectedByAuthorization() throws Exception {
        MvcResult result = mockMvc.perform(get("/login")).andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(302);
    }

    @Test
    void authenticatedUserIsNotRedirectedToLoginForHomePage() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/index.zul").with(SecurityMockMvcRequestPostProcessors.user("demo").roles("USER")))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isNotEqualTo(302);
    }

    @Test
    void logoutRedirectsToLoginWithLogoutParam() throws Exception {
        mockMvc.perform(post("/logout")
                        .with(SecurityMockMvcRequestPostProcessors.user("demo").roles("USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout"));
    }
}
