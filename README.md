# AGI – Módulo de gestión de usuarios (JDBC)

Módulo del proyecto **AGI – Asistente Guía Interactivo** que registra, consulta, actualiza y elimina usuarios en la base de datos MySQL `agi` por medio de JDBC.

Evidencia **GA7-220501096-AA2-EV01** – Codificación de módulos del software.
Aprendiz: Andrea Nathaly Rey Castiblanco · SENA · Análisis y Desarrollo de Software · Ficha 3336213

## Tecnologías

- Java 21 (compatible desde Java 17)
- MySQL Server 8.0 + MySQL Workbench 8.0
- MySQL Connector/J (driver JDBC)
- Visual Studio Code con *Extension Pack for Java*
- Git y GitHub

## Estructura

```
AGI_Modulo_Usuarios/
├── config/db.properties.ejemplo   plantilla de conexión (copiar como db.properties)
├── db/agi_bd.sql                  script de la base de datos agi
├── lib/                           aquí va el .jar de MySQL Connector/J
└── src/co/edu/sena/agi/
    ├── AplicacionAgi.java         punto de entrada (main)
    ├── conexion/ConexionBD.java   conexión JDBC
    ├── modelo/Usuario.java        entidad usuario
    ├── dao/UsuarioDAO.java        contrato del CRUD
    ├── dao/UsuarioDAOImpl.java    CRUD con PreparedStatement
    ├── util/SeguridadUtil.java    hash SHA-256 de la contraseña
    ├── util/ValidacionUtil.java   validación de datos de entrada
    └── vista/MenuUsuario.java     menú de consola
```

## Operaciones del menú

| Opción | Operación | Sentencia SQL |
|---|---|---|
| 1 | Registrar usuario | `INSERT` |
| 2 | Listar usuarios | `SELECT` |
| 3 | Buscar por id | `SELECT ... WHERE` |
| 4 | Actualizar usuario | `UPDATE` |
| 5 | Eliminar usuario | `DELETE` |
