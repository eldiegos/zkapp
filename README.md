# zkapp

Plataforma reutilizable para construir aplicaciones web sobre **Java 21+, Spring Boot 4, Spring
Security, ZK 10, Tomcat 11 y Maven**, empaquetadas como WAR.

Se distribuye como un Spring Boot Starter (`zkapp-spring-boot-starter`) que aporta toda la
infraestructura web *comun* de una aplicacion — ZK, login, logout, autenticacion LDAP/Microsoft,
Spring Security, sesion, usuario autenticado, roles/authorities, UI comun, auditoria tecnica y
logging tecnico — para que cada aplicacion consumidora se dedique exclusivamente a su dominio,
logica de negocio, JPA y SQL Server.

> **Principio arquitectonico**: el Starter proporciona infraestructura comun y configurable. La
> aplicacion consumidora proporciona el negocio. Nunca se copia codigo del Starter a las
> aplicaciones.

---

## Indice

1. [Proposito](#1-proposito)
2. [Arquitectura](#2-arquitectura)
3. [Modulos](#3-modulos)
4. [Instalacion](#4-instalacion)
5. [Dependencia Maven](#5-dependencia-maven)
6. [Configuracion minima](#6-configuracion-minima)
7. [Catalogo de propiedades](#7-catalogo-de-propiedades)
8. [Login](#8-login)
9. [LDAP](#9-ldap)
10. [Microsoft](#10-microsoft)
11. [Sesion](#11-sesion)
12. [CurrentUser](#12-currentuser)
13. [RBAC](#13-rbac)
14. [ZK](#14-zk)
15. [Navegacion](#15-navegacion)
16. [Logout](#16-logout)
17. [Auditoria](#17-auditoria)
18. [Logging](#18-logging)
19. [Secretos](#19-secretos)
20. [Extensibilidad](#20-extensibilidad)
21. [WAR](#21-war)
22. [Tomcat 11](#22-tomcat-11)
23. [Aplicacion de ejemplo](#23-aplicacion-de-ejemplo)
24. [Troubleshooting](#24-troubleshooting)

---

## 1. Proposito

Evitar que cada aplicacion ZK + Spring Boot de la organizacion reimplemente login, seguridad,
sesion, auditoria y UI comun. Esa infraestructura se construye **una vez**, en `zkapp`, y cada
aplicacion nueva la obtiene anadiendo una unica dependencia Maven.

## 2. Arquitectura

```text
                    zkapp-spring-boot-starter
                              |
             +----------------+----------------+
             |                |                |
             v                v                v
        zkapp-core      zkapp-security     zkapp-zk
             |                |                |
             +----------------+----------------+
                              |
                         Aplicacion
                              |
             +----------------+----------------+
             v                v                v
            JPA           SQL Server        Negocio
```

`zkapp-session` y `zkapp-audit` completan la infraestructura comun (gestion de sesion y auditoria
tecnica respectivamente) y son consumidos internamente por `zkapp-spring-boot-starter` igual que
`zkapp-core`, `zkapp-security` y `zkapp-zk`.

Cada modulo de plataforma es una **Spring Boot AutoConfiguration** independiente, auto-registrada
mediante `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`. Esto
significa que:

- Anadir `zkapp-spring-boot-starter` activa automaticamente toda la plataforma.
- Cada modulo tambien funciona de forma independiente si una aplicacion avanzada decide depender
  solo de una parte (por ejemplo, solo `zkapp-security` sin ZK).
- Todos los puntos de extension usan `@ConditionalOnMissingBean`: declarar tu propio bean del tipo
  correspondiente sustituye el bean por defecto sin tocar el Starter.

## 3. Modulos

| Modulo | Contenido | Depende de |
|---|---|---|
| `zkapp-core` | `ZkAppProperties` (namespace `zkapp.*`), API publica (`CurrentUserService`, `SessionService`, `AuditService`), modelo de evento de auditoria | Spring Boot, Bean Validation |
| `zkapp-session` | `SessionService` por defecto (basado en `HttpSession`), filtro de enriquecimiento de logging (MDC) | `zkapp-core` |
| `zkapp-security` | Spring Security: `SecurityFilterChain` comun, autenticacion LDAP/Microsoft, `CurrentUserService` por defecto, login/logout, RBAC | `zkapp-core` |
| `zkapp-audit` | `AuditService` por defecto (logging), traduccion de eventos tecnicos de seguridad a eventos de auditoria | `zkapp-core` |
| `zkapp-zk` | Servlets ZK, pagina de login ZUL, UI comun (cabecera), pagina de acceso denegado | `zkapp-core` |
| `zkapp-spring-boot-starter` | Agrega los modulos anteriores. Sin codigo propio | todos los anteriores |
| `zkapp-bom` | Bill of Materials con las versiones de la plataforma, Spring Boot y ZK | - |
| `zkapp-example` | Aplicacion de ejemplo (WAR) que consume unicamente el Starter | `zkapp-spring-boot-starter` |

## 4. Instalacion

Este repositorio es un reactor Maven multi-modulo. Para instalar la plataforma en el repositorio
Maven local:

```bash
mvn clean install
```

> Verificado con `mvn clean verify` (Java 25, Maven 3.9.16): los 9 modulos compilan y los 19 tests
> pasan (`BUILD SUCCESS`).

## 5. Dependencia Maven

Una aplicacion nueva solo necesita:

```xml
<dependency>
    <groupId>com.example.zkapp</groupId>
    <artifactId>zkapp-spring-boot-starter</artifactId>
    <version>${zkapp.version}</version>
</dependency>
```

Se recomienda importar `zkapp-bom` en el `<dependencyManagement>` de la aplicacion para alinear
versiones de Spring Boot, Spring Security y ZK:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.example.zkapp</groupId>
            <artifactId>zkapp-bom</artifactId>
            <version>${zkapp.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Para desplegar como WAR en Tomcat 11 externo, la aplicacion debe marcar Tomcat embebido como
`provided` (ver [seccion 21](#21-war)) y extender `SpringBootServletInitializer`.

## 6. Configuracion minima

```properties
zkapp.application.name=Mi Aplicacion
zkapp.application.code=MIAPP
zkapp.ui.home-page=/index.zul

zkapp.security.authentication.provider=ldap
zkapp.security.ldap.enabled=true
zkapp.security.ldap.url=ldap://ldap.example.com:389
zkapp.security.ldap.base=dc=example,dc=com
zkapp.security.ldap.user-search-base=ou=people
zkapp.security.ldap.user-search-filter=(uid={0})
```

Con esto, la aplicacion obtiene automaticamente `/login`, `/logout`, seguridad, sesion, usuario
autenticado, auditoria y logging tecnico.

## 7. Catalogo de propiedades

Todas las propiedades de la plataforma cuelgan exclusivamente del namespace `zkapp.*` y estan
tipadas en `ZkAppProperties` (modulo `zkapp-core`).

| Property | Default | Required | Sensitive | Description |
|---|---|---|---|---|
| `zkapp.application.name` | `ZK Application` | No | No | Nombre visible de la aplicacion. |
| `zkapp.application.code` | `APP` | **Si** (no vacio) | No | Codigo que identifica inequivocamente la aplicacion (auditoria, logging). |
| `zkapp.ui.home-page` | `/index.zul` | **Si** (no vacio) | No | Ruta, dentro del contexto de la app, a la que se navega tras login. Sin context path. |
| `zkapp.ui.title` | *(nombre de la app)* | No | No | Titulo mostrado en la cabecera comun. |
| `zkapp.ui.logo-url` | *(ninguno)* | No | No | URL de un logo opcional en la cabecera comun. |
| `zkapp.security.authentication.provider` | `ldap` | No | No | `ldap` o `microsoft`. |
| `zkapp.security.ldap.enabled` | `false` | No | No | Activa la autenticacion LDAP (junto con `provider=ldap`). |
| `zkapp.security.ldap.url` | *(ninguno)* | Si LDAP activo | No | URL del servidor LDAP. |
| `zkapp.security.ldap.base` | *(ninguno)* | Si LDAP activo | No | Base DN del directorio. |
| `zkapp.security.ldap.user-search-base` | `` | No | No | Base de busqueda de usuarios, relativa al base DN. |
| `zkapp.security.ldap.user-search-filter` | `(uid={0})` | No | No | Filtro de busqueda de usuarios. |
| `zkapp.security.ldap.manager-dn` | *(ninguno)* | No | No | DN tecnico para busquedas, si no se permite bind anonimo. |
| `zkapp.security.ldap.manager-password` | *(ninguno)* | No | **Si** | Password del usuario tecnico. Externalizar siempre. |
| `zkapp.security.microsoft.enabled` | `false` | No | No | Activa la autenticacion Microsoft (junto con `provider=microsoft`). |
| `zkapp.security.microsoft.tenant-id` | *(ninguno)* | Si Microsoft activo | No | Tenant de Microsoft Entra ID. |
| `zkapp.security.microsoft.client-id` | *(ninguno)* | Si Microsoft activo | No | Client ID de la app registrada. |
| `zkapp.security.microsoft.client-secret` | *(ninguno)* | Si Microsoft activo | **Si** | Client secret. Externalizar siempre. |
| `zkapp.security.microsoft.scopes` | `openid,profile,email` | No | No | Scopes OAuth2/OIDC solicitados. |
| `zkapp.session.timeout` | `30m` | No | No | Tiempo maximo de inactividad de la sesion. |
| `zkapp.session.invalidate-on-logout` | `true` | No | No | Invalida la `HttpSession` al hacer logout. |
| `zkapp.audit.enabled` | `true` | No | No | Activa la auditoria tecnica. |
| `zkapp.logging.include-application-code` | `true` | No | No | Incluye el codigo de aplicacion en el MDC. |
| `zkapp.logging.include-user` | `true` | No | No | Incluye el usuario autenticado en el MDC. |
| `zkapp.logging.include-session-id` | `true` | No | No | Incluye el id de sesion en el MDC. |
| `zkapp.logging.include-correlation-id` | `true` | No | No | Incluye un correlation-id en el MDC. |
| `zkapp.logging.correlation-id-header` | `X-Correlation-Id` | No | No | Cabecera HTTP usada para propagar el correlation-id. |

Las propiedades marcadas como **Required** hacen fallar el arranque de la aplicacion (fail fast)
si estan vacias, mediante validacion Bean Validation sobre `ZkAppProperties`.

## 8. Login

Ruta estandar: **`GET /login`**. La peticion se reenvia internamente (`forward`, sin redireccion
visible) a `/login.zul`, servida por el servlet de ZK, sin necesidad de `web.xml` en la aplicacion
consumidora.

```text
GET /login  -->  forward interno  -->  /login.zul (ZK)
                                            |
                                    formulario HTML nativo
                                            |
                                    POST /login (Spring Security)
                                            |
                                      Autenticacion
                                            |
                                  Usuario autenticado
                                            |
                                   zkapp.ui.home-page
```

El formulario de `login.zul` es HTML nativo (namespace `native` de ZK), **no** un formulario AJAX
de ZK, porque debe ser procesado directamente por el filtro de Spring Security
(`UsernamePasswordAuthenticationFilter`) en `POST /login`, incluyendo el token CSRF.

La aplicacion consumidora no implementa login: ni la pagina, ni el `SecurityFilterChain`, ni el
procesamiento del formulario.

## 9. LDAP

```properties
zkapp.security.authentication.provider=ldap
zkapp.security.ldap.enabled=true
zkapp.security.ldap.url=${LDAP_URL}
zkapp.security.ldap.base=dc=example,dc=com
zkapp.security.ldap.user-search-base=ou=people
zkapp.security.ldap.user-search-filter=(uid={0})
zkapp.security.ldap.manager-dn=${LDAP_MANAGER_DN:}
zkapp.security.ldap.manager-password=${LDAP_MANAGER_PASSWORD:}
```

No se asume ninguna estructura de directorio: `user-search-base` y `user-search-filter` son
totalmente configurables. Internamente se usa `BindAuthenticator` de Spring Security LDAP: el
propio intento de login hace el bind contra el directorio.

## 10. Microsoft

```properties
zkapp.security.authentication.provider=microsoft
zkapp.security.microsoft.enabled=true
zkapp.security.microsoft.tenant-id=${MICROSOFT_TENANT_ID}
zkapp.security.microsoft.client-id=${MICROSOFT_CLIENT_ID}
zkapp.security.microsoft.client-secret=${MICROSOFT_CLIENT_SECRET}
```

Basado en OAuth2/OIDC (Microsoft Entra ID). El `ClientRegistration` se construye
programaticamente a partir de `zkapp.security.microsoft.*` (no del namespace estandar
`spring.security.oauth2.client.*`) para mantener `zkapp.*` como unico namespace de configuracion
de la plataforma. El flujo de login usa el mismo `loginPage("/login")` comun; ZK puede mostrar un
enlace a `/oauth2/authorization/microsoft` cuando este proveedor esta activo.

## 11. Sesion

API publica (`zkapp-core`):

```java
public interface SessionService {
    boolean isAuthenticated();
    String getSessionId();
    void invalidate();
    <T> void set(String key, T value);
    <T> T get(String key, Class<T> type);
}
```

Implementacion por defecto basada en `HttpSession` (`zkapp-session`). Las capas de negocio no
deben propagar `HttpSession`: deben depender de `SessionService`.

```properties
zkapp.session.timeout=30m
zkapp.session.invalidate-on-logout=true
```

## 12. CurrentUser

API publica (`zkapp-core`):

```java
public interface CurrentUserService {
    boolean isAuthenticated();
    String getUsername();
    String getDisplayName();
    String getEmail();
    Set<String> getRoles();
    Set<String> getAuthorities();
    boolean hasRole(String role);
    boolean hasAuthority(String authority);
}
```

La implementacion por defecto (`zkapp-security`) es el unico punto de la plataforma que accede a
`SecurityContextHolder`/`Authentication`. La capa de negocio de la aplicacion consumidora, y las
paginas ZUL (via `${currentUserService...}` con `DelegatingVariableResolver`), deben usar esta
API en lugar de `SecurityContextHolder`, `Authentication` o `Principal` directamente.

## 13. RBAC

La plataforma no define roles ni authorities de negocio: cada aplicacion define los suyos (por
ejemplo `MAT_ENROLLMENT_READ`, `MAT_ENROLLMENT_WRITE`) sin modificar `zkapp-security`.
`CurrentUserService.getRoles()`/`getAuthorities()`/`hasRole()`/`hasAuthority()` funcionan con
cualquier conjunto de authorities que el proveedor de autenticacion asigne (grupos LDAP, roles de
aplicacion, claims de Microsoft Entra ID mapeados por la aplicacion, etc.).

La arquitectura esta preparada para `RoleHierarchy`: una aplicacion que necesite jerarquia de
roles puede declarar su propio bean `RoleHierarchy` y conectarlo a sus reglas de autorizacion
(`@PreAuthorize`/`authorizeHttpRequests`) siguiendo la configuracion estandar de Spring Security,
sin modificar el Starter.

## 14. ZK

`zkapp-zk` registra, sin necesidad de `web.xml`:

- `DHtmlLayoutServlet` (`*.zul`, `*.zhtml`)
- `DHtmlUpdateServlet` (`/zkau/*`)
- `HttpSessionListener` de ZK
- Integracion Spring mediante `org.zkoss.zkplus.spring.DelegatingVariableResolver`
  (`?variable-resolver?` en cada pagina que necesita acceder a beans Spring por EL, por ejemplo
  `${currentUserService.displayName}` o `${zkAppProperties.application.name}`)
- Paginas comunes: `/login.zul`, `/403.zul`, y el fragmento incluible `/zkapp/header.zul`

Las aplicaciones consumidoras aportan sus propias `.zul`, ViewModels/Composers y logica de
negocio bajo `src/main/webapp`. Para incluir la cabecera comun:

```xml
<include src="/zkapp/header.zul"/>
```

## 15. Navegacion

```properties
zkapp.ui.home-page=/index.zul
```

o, por ejemplo:

```properties
zkapp.ui.home-page=/matricula/index.zul
```

Tras un login correcto, Spring Security navega a `zkapp.ui.home-page` (`defaultSuccessUrl`, con
"forzado" activado: siempre se navega a esta pagina tras login, independientemente de la URL
solicitada originalmente). La propiedad **no** incluye el context path del WAR: si el WAR se
despliega como `matricula.war` con context path `/matricula`, la URL final sera
`/matricula/index.zul`, resuelta automaticamente por el contenedor servlet. El Starter es
independiente del nombre del WAR.

## 16. Logout

Ruta estandar: **`POST /logout`**.

1. Cierra la autenticacion.
2. Invalida la `HttpSession` (configurable con `zkapp.session.invalidate-on-logout`).
3. Limpia el `SecurityContextHolder`.
4. Redirige a `/login?logout`.
5. Publica un evento tecnico de auditoria `LOGOUT`.

El fragmento de cabecera comun (`/zkapp/header.zul`) ya incluye el formulario de logout con su
token CSRF.

## 17. Auditoria

```properties
zkapp.audit.enabled=true
```

Eventos minimos: `LOGIN_SUCCESS`, `LOGIN_FAILURE`, `LOGOUT`, `ACCESS_DENIED`.

API publica:

```java
public interface AuditService {
    void record(AuditEvent event);
}
```

La implementacion por defecto (`zkapp-audit`) registra cada evento mediante logging (SLF4J,
logger `com.example.zkapp.audit`), sin ninguna dependencia de persistencia. Una aplicacion que
necesite persistir la auditoria (por ejemplo en SQL Server via JPA) sustituye el bean
`AuditService` con su propia implementacion:

```java
@Bean
public AuditService auditService(MyAuditRepository repository) {
    return event -> repository.save(MyAuditEntity.from(event));
}
```

## 18. Logging

`zkapp-session` registra un filtro que puebla el MDC de SLF4J en cada peticion con:

- `applicationCode` (`zkapp.application.code`)
- `user` (usuario autenticado, si lo hay)
- `sessionId`
- `correlationId` (propagado desde la cabecera `zkapp.logging.correlation-id-header`, o generado)

No se acopla a ELK, Logstash ni a ninguna infraestructura de logging concreta: la aplicacion
configura sus propios *appenders* y patrones (por ejemplo Logback) referenciando estas claves de
MDC, por ejemplo:

```xml
<pattern>%d{ISO8601} [%X{correlationId}] [%X{applicationCode}] [%X{user}] %-5level %logger - %msg%n</pattern>
```

## 19. Secretos

Nunca hardcodear passwords, tokens ni client secrets. Usar variables de entorno o un gestor de
secretos:

```properties
zkapp.security.ldap.manager-password=${LDAP_MANAGER_PASSWORD}
zkapp.security.microsoft.client-secret=${MICROSOFT_CLIENT_SECRET}
```

Propiedades sensibles (ver tabla de la [seccion 7](#7-catalogo-de-propiedades)):
`zkapp.security.ldap.manager-password`, `zkapp.security.microsoft.client-secret`. Ninguna
propiedad sensible tiene un valor de ejemplo en este repositorio ni en los tests.

## 20. Extensibilidad

Puntos de extension explicitos (todos usan `@ConditionalOnMissingBean` sobre un tipo publico):

| Bean | Tipo | Modulo por defecto |
|---|---|---|
| `SessionService` | `com.example.zkapp.core.api.SessionService` | `zkapp-session` (`HttpSessionService`) |
| `CurrentUserService` | `com.example.zkapp.core.api.CurrentUserService` | `zkapp-security` (`DefaultCurrentUserService`) |
| `AuditService` | `com.example.zkapp.core.api.AuditService` | `zkapp-audit` (`LoggingAuditService`) |
| `AuthenticationProvider` (Spring Security) | `org.springframework.security.authentication.AuthenticationProvider` | `zkapp-security` (LDAP `BindAuthenticator`) |
| `SecurityFilterChain` | `org.springframework.security.web.SecurityFilterChain` | `zkapp-security` |
| `ZkAppAuthenticationConfigurer` | `com.example.zkapp.security.auth.ZkAppAuthenticationConfigurer` | `zkapp-security` (LDAP, Microsoft) |

Ejemplo (usado por la propia `zkapp-example`, vease `DemoSecurityConfiguration`): sustituir el
proveedor de autenticacion sin tocar el Starter.

```java
@Bean
public AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(encoder);
    provider.setUserDetailsService(uds);
    return provider;
}
```

Anadir un nuevo proveedor de autenticacion (mas alla de LDAP/Microsoft) consiste en implementar
`ZkAppAuthenticationConfigurer` y registrar el bean correspondiente.

## 21. WAR

`zkapp-spring-boot-starter` no impide el empaquetado WAR ni fuerza un despliegue embebido
obligatorio. La aplicacion consumidora:

```xml
<packaging>war</packaging>
```

```java
@SpringBootApplication
public class MyApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MyApplication.class);
    }
}
```

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

Con Tomcat marcado `provided`, el WAR sigue siendo ejecutable localmente con
`mvn spring-boot:run` (o `java -jar` tras `repackage`) y, al desplegarse en un Tomcat 11 externo,
usa el servlet-api del contenedor en lugar de uno embebido.

## 22. Tomcat 11

La plataforma se basa en Jakarta Servlet 6.1 (Jakarta EE 11), la version soportada por Tomcat 11
y por Spring Boot 4/Spring Framework 7. No hay codigo especifico de Tomcat en el Starter: cualquier
contenedor compatible con esa version de la especificacion Servlet deberia funcionar igual.

## 23. Aplicacion de ejemplo

`zkapp-example` (modulo `zkapp-example/`) consume unicamente `zkapp-spring-boot-starter` y
demuestra el flujo completo:

```text
/login -> autenticacion -> /index.zul (protegida) -> /logout
```

No reimplementa login, logout, `SecurityFilterChain`, `SessionService`, `CurrentUserService` ni la
configuracion ZK comun. Unicamente aporta:

- `DemoSecurityConfiguration`: sustituye el `AuthenticationProvider` LDAP por dos usuarios en
  memoria (`demo`/`demo`, `admin`/`admin`) para poder ejecutarse sin un servidor LDAP real,
  demostrando el punto de extension de la [seccion 20](#20-extensibilidad).
- `index.zul` + `IndexComposer`: pagina de negocio propia que consume `CurrentUserService` y
  `SessionService`, e incluye la cabecera comun (`/zkapp/header.zul`).

Ejecucion local:

```bash
cd zkapp-example
mvn spring-boot:run
```

Luego visitar `http://localhost:8080/login` y autenticarse con `demo`/`demo`.

> Este flujo se ha verificado manualmente de extremo a extremo contra la aplicacion real en
> ejecucion (Tomcat 11 embebido, JDK 25, ZK 10.3.0.1 CE): `GET /login` (formulario renderizado),
> `POST /login` con `demo`/`demo` (302 a `/index.zul`), `GET /index.zul` (200, muestra usuario,
> roles y sesion), `POST /logout` (302 a `/login?logout`) y `GET /index.zul` posterior (302 de
> vuelta a login, sesion invalidada).

## 24. Troubleshooting

- **ZK CE esta en Maven Central**: los artefactos `org.zkoss.zk:{zk,zul,zkbind,zkplus}` (edicion
  CE) se resuelven directamente desde Maven Central en la version `10.3.0.1-jakarta` (sufijo
  `-jakarta` = build sobre Jakarta Servlet, el requerido por Tomcat 11 / Spring Boot 4). No hace
  falta declarar el repositorio propio de ZK (`mavensync.zkoss.org`). Si usas una edicion EE/PE con
  licencia, esas coordenadas y ese repositorio si son necesarios: ajusta `zk.version` y anade el
  repositorio correspondiente en `zkapp-bom`.
  - `org.zkoss.zk:zkplus` incluye las clases de integracion Spring
    (`org.zkoss.zkplus.spring.DelegatingVariableResolver`, `org.zkoss.zkplus.spring.SpringUtil`).
- **Verificado con `mvn clean verify`**: la plataforma se ha compilado y probado en un entorno con
  Java 25 (JDK 21+) y Maven 3.9.16 (`BUILD SUCCESS`, 9 modulos, 19 tests). Puntos que cambiaron
  respecto a versiones anteriores de Spring Boot/Spring Security y que tocaron al fijarlos:
  - `DaoAuthenticationProvider` ya no tiene un constructor `(PasswordEncoder)`: recibe el
    `UserDetailsService` en el constructor y el encoder se fija con `setPasswordEncoder(...)`
    (vease `DemoSecurityConfiguration` en `zkapp-example`).
  - Spring Boot 4 extrajo el slice de test `@AutoConfigureMockMvc`/`MockMvc` de
    `spring-boot-test-autoconfigure` a un modulo dedicado,
    `org.springframework.boot:spring-boot-webmvc-test` (paquete
    `org.springframework.boot.webmvc.test.autoconfigure`). Si usas `@AutoConfigureMockMvc`,
    anade esa dependencia en `scope=test` (vease `zkapp-example/pom.xml`).
  - Si tu aplicacion anade Spring MVC (`spring-boot-starter-web` o, como en los tests,
    `spring-boot-webmvc-test`), `WebMvcAutoConfiguration` registra su propio bean
    `requestContextFilter`. Por eso el bean equivalente de `zkapp-session` se llama
    `zkAppRequestContextFilter`: evita el choque de nombres sin desactivar ninguno de los dos.
  - `SecurityContextHolder` vive en `org.springframework.security.core.context`, no en
    `org.springframework.security.core`.
- **Verificado ejecutando la aplicacion real** (no solo tests): arrancar `zkapp-example` con
  `mvn spring-boot:run` y probar el flujo login/logout a mano revelo tres fallos que los tests
  unitarios (con `MockMvc`, que no despacha contra los servlets reales) no detectaban:
  - **Sin servlet en `/`, el contenedor nunca llega a invocar la cadena de filtros** para rutas
    que solo gestiona un filtro de Spring Security (por ejemplo `/logout`, o
    `/oauth2/authorization/**`/`/login/oauth2/code/**` en la integracion Microsoft): Tomcat
    respondia 404 sin que `FilterChainProxy` llegara siquiera a loguear la peticion. La solucion es
    `ZkAppZkAutoConfiguration` registra un servlet catch-all de respaldo en `/`
    (`FallbackNotFoundServlet`) para que el contenedor siempre encuentre una correspondencia y deje
    pasar la peticion por los filtros primero.
  - **`DHtmlLayoutServlet` de ZK requiere el init-param `update-uri`** apuntando a donde este
    mapeado el motor AJAX (`/zkau`); sin el, el servlet falla al cargar
    (`ServletException: The update-uri parameter must be specified...`).
  - **Los componentes `native` de ZK (`<n:.../>`) no tienen contrapartida en el servidor**: no se
    pueden conectar (`@Wire`) a un Composer. Para inyectar valores dinamicos (como el token CSRF)
    en sus atributos hay que usar EL directamente en el ZUL contra el implicit object
    `requestScope` (por ejemplo `${requestScope['_csrf'].token}`), no invocar metodos Java
    arbitrarios como `request.getAttribute('_csrf')` (el XEL de ZK no lo soporta igual que un EL
    3.0 completo) ni intentar establecer las propiedades desde Java tras la composicion.
  - En `Hbox`, la alineacion vertical se controla con `align` (valores `''`/`stretch`/`start`/
    `center`/`end`), no con `valign`; y `pack` solo acepta `''`/`start`/`center`/`end` (no
    `stretch`).
- **`No hay ningun ZkAppAuthenticationConfigurer registrado...`**: revisa que
  `zkapp.security.authentication.provider` coincide con un proveedor con su flag `enabled=true`
  (`zkapp.security.ldap.enabled` o `zkapp.security.microsoft.enabled`).
- **Los recursos ZK (`*.zul`, `/zkapp/...`) devuelven 404 en un WAR desplegado externamente**:
  confirma que el contenedor (Tomcat 11) escanea `META-INF/resources` de los JARs en
  `WEB-INF/lib` (comportamiento estandar de Servlet 3.0+; deberia estar activo por defecto).
- **Las peticiones AJAX de ZK (`/zkau/**`) fallan con 403**: estan explicitamente excluidas de la
  proteccion CSRF (`csrf().ignoringRequestMatchers("/zkau/**")`) porque ZK gestiona sus propias
  peticiones de actualizacion fuera del ciclo de formularios HTML; si sigues viendo 403, revisa que
  ninguna configuracion de la aplicacion haya sobrescrito el `SecurityFilterChain` sin mantener
  esa exclusion.
- **Tests que usan `MockMvc`**: la plataforma no usa Spring MVC en tiempo de ejecucion (ZK gestiona
  sus propios servlets), por lo que `spring-webmvc` solo se declara en `scope=test` para que
  `MockMvc` pueda simular el despacho de peticiones a traves de la cadena de filtros de seguridad;
  no valida el renderizado real de las paginas ZUL (eso se prueba arrancando la aplicacion).
