# AGI – Módulo de gestión de usuarios

Módulo del proyecto **AGI – Asistente Guía Interactivo** que registra, consulta, actualiza y elimina usuarios en la base de datos MySQL `agi`. Tiene dos interfaces que comparten las mismas clases de conexión, modelo y DAO:

| Versión | Interfaz | Evidencia |
|---|---|---|
| v1.0-consola | Menú de consola con JDBC | GA7-220501096-AA2-EV01 |
| v2.0-web | Aplicación web con servlets, JSP y formularios HTML (GET y POST) | GA7-220501096-AA2-EV02 |

Aprendiz: Andrea Nathaly Rey Castiblanco · SENA · Análisis y Desarrollo de Software · Ficha 3336213

## Tecnologías

- Java 21 (compatible desde Java 17)
- Apache Tomcat 10.1 (Jakarta Servlet 6.0 y JSP 3.1)
- MySQL Server 8.0 + MySQL Workbench 8.0
- MySQL Connector/J (driver JDBC)
- Visual Studio Code con *Extension Pack for Java*
- Git y GitHub

## Estructura

```
AGI_Modulo_Usuarios/
├── desplegar.bat                  compila y despliega la aplicación web en Tomcat
├── config/db.properties.ejemplo   plantilla de conexión (copiar como db.properties)
├── db/agi_bd.sql                  script de la base de datos agi
├── lib/                           aquí va el .jar de MySQL Connector/J
├── src/co/edu/sena/agi/
│   ├── AplicacionAgi.java         aplicación de consola (main)
│   ├── conexion/ConexionBD.java   conexión JDBC (consola y web)
│   ├── modelo/Usuario.java        entidad usuario
│   ├── dao/                       UsuarioDAO + UsuarioDAOImpl (CRUD)
│   ├── util/                      SeguridadUtil, ValidacionUtil, HtmlUtil
│   ├── vista/MenuUsuario.java     menú de consola
│   └── web/                       LoginServlet, UsuarioServlet
└── web/
    ├── index.jsp                  inicio de sesión
    ├── css/estilos.css
    └── WEB-INF/
        ├── web.xml
        └── vistas/                usuarios.jsp, formulario-usuario.jsp, error.jsp, fragmentos/
```

## Aplicación web

1. Ejecutar `db/agi_bd.sql` en MySQL Workbench.
2. Copiar `config/db.properties.ejemplo` como `config/db.properties` y escribir el usuario y la clave de MySQL.
3. Copiar `mysql-connector-j-26.10.0.jar` dentro de `lib/`.
4. Descomprimir Apache Tomcat 10.1 en `C:\tomcat` (o cambiar la ruta en `desplegar.bat`).
5. En la terminal, dentro del proyecto: `.\desplegar.bat`
6. Abrir `http://localhost:8080/agi/`

| URL | Método | Acción |
|---|---|---|
| `/login` | POST | Iniciar sesión |
| `/login?accion=salir` | GET | Cerrar sesión |
| `/usuarios` | GET | Listar usuarios |
| `/usuarios?accion=buscar&id=N` | GET | Buscar por id |
| `/usuarios?accion=nuevo` | GET | Formulario de registro |
| `/usuarios?accion=editar&id=N` | GET | Formulario de edición |
| `/usuarios` (accion=registrar) | POST | Registrar usuario |
| `/usuarios` (accion=actualizar) | POST | Actualizar usuario |
| `/usuarios` (accion=eliminar) | POST | Eliminar usuario |

## Aplicación de consola

Abrir la carpeta en VS Code y ejecutar `AplicacionAgi.java` con **Run**.
