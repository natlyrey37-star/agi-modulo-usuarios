# AGI – Asistente Guía Interactivo

Módulo de **usuarios y guías paso a paso** del proyecto AGI, una aplicación que acompaña a las personas adultas mayores en el uso básico del celular. Desde la versión 3.0 está construido con el framework **Spring Boot** y guarda la información en la base de datos MySQL `agi`.

Aprendiz: Andrea Nathaly Rey Castiblanco · SENA · Análisis y Desarrollo de Software · Ficha 3336213

## Versiones

| Versión | Etiqueta | Qué incluye | Evidencia |
|---|---|---|---|
| 1.0 | `v1.0-consola` | CRUD de usuarios en consola con JDBC y patrón DAO | GA7-220501096-AA2-EV01 |
| 2.0 | `v2.0-web` | Aplicación web con servlets, JSP y formularios GET/POST en Tomcat | GA7-220501096-AA2-EV02 |
| 3.0 | `v3.0-spring` | Spring Boot: web con Thymeleaf, Spring Data JPA, API REST y cliente móvil | GA7-220501096-AA3-EV01 |

Las versiones anteriores se conservan en sus etiquetas: `git checkout v2.0-web`.

## Tecnologías (versión 3.0)

- Java 17 o superior (el equipo de desarrollo usa JDK 25)
- Spring Boot 4.1 (Spring MVC, Spring Data JPA con Hibernate, Bean Validation)
- Thymeleaf para las vistas web
- Tomcat embebido (no hay que instalar Tomcat aparte)
- MySQL Server 8.0 + MySQL Connector/J (lo descarga Maven)
- Spring Security Crypto (BCrypt) para cifrar contraseñas
- HTML, CSS y JavaScript para el cliente móvil
- Maven Wrapper (`mvnw`), Visual Studio Code, Git y GitHub

## Estructura

```
AGI_Modulo_Usuarios/
├── pom.xml                         dependencias y configuración de Maven
├── mvnw, mvnw.cmd, .mvn/           Maven Wrapper (no hace falta instalar Maven)
├── config/db.properties.ejemplo    plantilla de conexión (copiar como db.properties)
├── db/
│   ├── agi_bd.sql                  crea la base agi con datos iniciales
│   └── usuario_aplicacion.sql      cuenta de MySQL con permisos mínimos (opcional)
└── src/main/
    ├── java/co/edu/sena/agi/
    │   ├── AgiApplication.java     clase principal (arranca Spring Boot)
    │   ├── config/                 WebConfig, SesionInterceptor
    │   ├── modelo/                 entidades JPA: Usuario, Categoria, Guia, Paso
    │   ├── repositorio/            interfaces de Spring Data JPA
    │   ├── servicio/               reglas de negocio y transacciones
    │   ├── dto/                    formularios y objetos de la API
    │   ├── excepcion/              excepciones propias
    │   ├── controlador/            controladores web (Thymeleaf)
    │   └── api/                    controladores REST (JSON)
    └── resources/
        ├── application.properties
        ├── messages.properties
        ├── templates/              vistas Thymeleaf
        └── static/
            ├── css/estilos.css
            └── app/                cliente móvil (index.html, app.css, app.js)
```

## Cómo ejecutarlo

1. Ejecutar `db/agi_bd.sql` en MySQL Workbench (crea la base `agi` desde cero).
2. Copiar `config/db.properties.ejemplo` como `config/db.properties` y escribir el usuario y la clave de MySQL.
3. En la terminal de VS Code, dentro del proyecto: `.\mvnw.cmd spring-boot:run` (en Linux o Mac: `./mvnw spring-boot:run`).
4. Abrir `http://localhost:8080/agi/` e ingresar con `admin@agi.com` / `Agi2026*`.

Para generar el ejecutable independiente: `.\mvnw.cmd package` y luego `java -jar target\agi.jar`.

## Rutas de la aplicación web

| URL | Método | Acción |
|---|---|---|
| `/login` | GET / POST | Formulario e inicio de sesión |
| `/salir` | POST | Cerrar sesión |
| `/registro` | GET / POST | Crear cuenta (público) |
| `/panel` | GET | Resumen de la base de datos |
| `/usuarios` | GET | Listar usuarios (`?id=N` para buscar) |
| `/usuarios/nuevo` | GET | Formulario de registro |
| `/usuarios` | POST | Registrar usuario |
| `/usuarios/{id}/editar` | GET | Formulario de edición |
| `/usuarios/{id}` | POST | Actualizar usuario |
| `/usuarios/{id}/eliminar` | POST | Eliminar usuario |
| `/guias` | GET | Listar guías (`?categoria=N` para filtrar) |
| `/guias/nueva` | GET | Formulario de guía |
| `/guias` | POST | Registrar guía |
| `/guias/{id}` | GET | Detalle con sus pasos |
| `/guias/{id}/editar` | GET | Formulario de edición |
| `/guias/{id}` | POST | Actualizar guía |
| `/guias/{id}/eliminar` | POST | Eliminar guía y sus pasos |
| `/guias/{id}/pasos` | POST | Agregar paso |
| `/guias/{id}/pasos/{idPaso}/editar` | GET | Formulario de edición del paso |
| `/guias/{id}/pasos/{idPaso}` | POST | Actualizar paso |
| `/guias/{id}/pasos/{idPaso}/eliminar` | POST | Eliminar paso |

## API REST (la consume el cliente móvil)

| URL | Respuesta |
|---|---|
| `GET /agi/api/categorias` | Lista de categorías |
| `GET /agi/api/guias` | Catálogo de guías con el total de pasos |
| `GET /agi/api/guias?categoria=N` | Guías de una categoría |
| `GET /agi/api/guias/{id}` | Guía con sus pasos en orden (404 si no existe) |

Cliente móvil: `http://localhost:8080/agi/app/index.html`
