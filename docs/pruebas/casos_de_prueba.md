# Pruebas del módulo AGI (versión 3.1)

Evidencia GA7-220501096-AA3-EV02 · Módulos de software stand-alone, web y móvil codificados y probados.

Las pruebas se ejecutaron el 15 de noviembre de 2026 sobre la aplicación en `http://localhost:8080/agi/` (Spring Boot 4.1 con MySQL 8.0) y con las pruebas automatizadas de `src/test/java`.

## Historias de usuario

### HU-01 · Iniciar sesión

Como usuario registrado, quiero iniciar sesión con mi correo y mi contraseña para entrar a la administración de AGI.

Criterios de aceptación:

- El correo y la contraseña son obligatorios.
- Si los datos no coinciden, el mensaje es el mismo para correo o contraseña errados, sin revelar cuál falló.
- El correo se reconoce sin importar mayúsculas ni espacios.
- Al entrar se muestra el panel con el saludo y el resumen de la base de datos.

### HU-02 · Crear una cuenta

Como persona adulta mayor o familiar que la acompaña, quiero crear una cuenta con mis datos básicos para usar las guías de AGI.

Criterios de aceptación:

- Nombres y apellidos: obligatorios, máximo 60 caracteres, solo letras (con tildes y ñ), espacios, apóstrofo, punto y guion.
- Cédula: solo números, entre 6 y 15 dígitos, y no puede estar repetida.
- Correo: formato nombre@dominio, máximo 100 caracteres y no repetido.
- Fecha de nacimiento: obligatoria, desde 1900 y anterior a hoy.
- Contraseña: entre 6 y 50 caracteres, igual a la confirmación y guardada con cifrado BCrypt.

### HU-03 · Consultar usuarios

Como administrador, quiero ver la lista de usuarios y buscar uno por su id para revisar quién está registrado.

Criterios de aceptación:

- La lista muestra id, nombre completo, cédula, correo y fecha de nacimiento.
- La búsqueda por id muestra solo ese usuario o un mensaje si no existe.
- Un id con letras se rechaza sin que la aplicación falle.

### HU-04 · Registrar usuarios desde la administración

Como administrador, quiero registrar usuarios desde el panel para ayudar a las personas que no pueden hacerlo solas.

Criterios de aceptación:

- Se aplican las mismas validaciones del registro público (HU-02).
- Al guardar, el usuario aparece en la lista con un mensaje de confirmación.

### HU-05 · Actualizar usuarios

Como administrador, quiero modificar los datos de un usuario para mantener la información al día.

Criterios de aceptación:

- El formulario se abre con los datos actuales.
- La contraseña es opcional: si se deja en blanco se conserva la actual.
- No se permite usar la cédula o el correo de otra cuenta.

### HU-06 · Eliminar usuarios

Como administrador, quiero eliminar un usuario con una confirmación previa para retirar cuentas que ya no se usan.

Criterios de aceptación:

- Antes de eliminar se pide confirmación.
- Al eliminar se muestra un mensaje y el usuario desaparece de la lista.
- Un usuario que no existe muestra la página de error 404.

### HU-07 · Registrar y consultar guías

Como administrador, quiero crear guías y verlas filtradas por categoría para organizar el contenido de la aplicación.

Criterios de aceptación:

- Título obligatorio de máximo 100 caracteres y categoría obligatoria.
- Descripción opcional de máximo 255 caracteres.
- Los caracteres especiales y las etiquetas HTML se guardan y se muestran como texto.
- El listado se puede filtrar por categoría.

### HU-08 · Gestionar los pasos de una guía

Como administrador, quiero agregar, editar y eliminar los pasos de una guía para explicar cada tarea en orden.

Criterios de aceptación:

- El número del paso es un entero entre 1 y 50 y no se repite dentro de la misma guía.
- Título del paso obligatorio de máximo 100 caracteres e instrucción obligatoria de máximo 255.
- El formulario sugiere el número del siguiente paso.
- Los pasos se muestran en orden y se pueden editar y eliminar.

### HU-09 · Actualizar y eliminar guías

Como administrador, quiero corregir o eliminar una guía para que el contenido esté vigente.

Criterios de aceptación:

- La edición conserva los pasos de la guía.
- Al eliminar una guía también se eliminan sus pasos.

### HU-10 · Seguir una guía en la app móvil

Como persona adulta mayor, quiero abrir una guía en el celular y avanzar paso a paso para aprender a usar mi teléfono a mi ritmo.

Criterios de aceptación:

- El catálogo se puede filtrar por categoría.
- Cada paso muestra «Paso X de N» y una barra de progreso.
- Los botones Anterior, Siguiente y Terminar permiten avanzar y retroceder.
- La API REST responde en JSON y devuelve 404 o 400 con un mensaje claro cuando la guía no existe o el id no es numérico.

### HU-11 · Cerrar sesión y proteger las páginas

Como usuario, quiero cerrar mi sesión y que nadie entre a la administración sin iniciarla para proteger la información.

Criterios de aceptación:

- El botón Salir cierra la sesión y muestra un mensaje.
- Las páginas de administración redirigen al inicio de sesión cuando no hay sesión.

## Casos de prueba funcionales y de validación

| Id | Historia | Tipo | Entrada | Resultado esperado | Resultado obtenido | Estado |
|---|---|---|---|---|---|---|
| CP-01 | HU-01 | Obligatorio | Correo y contraseña vacíos | Mensaje «Escriba su correo y su contraseña.» | Se muestra el mensaje y no se inicia sesión | Cumple |
| CP-02 | HU-01 | Seguridad | admin@agi.com con contraseña ClaveErrada1 | Mensaje genérico y el correo se conserva | «El correo o la contraseña no son correctos.»; el correo sigue escrito | Cumple |
| CP-03 | HU-02 | Obligatorio | Formulario de registro vacío | Un mensaje junto a cada campo obligatorio | Seis campos marcados en rojo con su mensaje | Cumple |
| CP-04 | HU-02 | Texto | Nombres «Ana123», apellidos «Pérez#@» | Rechazo: solo letras y espacios | «Los nombres/apellidos solo pueden tener letras y espacios.» | Cumple |
| CP-05 | HU-02 | Número | Cédula «12AB34» | Rechazo: solo números de 6 a 15 dígitos | Mensaje de cédula inválida | Cumple |
| CP-06 | HU-02 | Fecha | Fecha de nacimiento 10/05/2030 | Rechazo: debe ser anterior a hoy | «La fecha de nacimiento debe ser anterior a hoy.» | Cumple |
| CP-07 | HU-02 | Caracteres especiales | Correo «ana.correo.com» (sin @) | Rechazo con ejemplo de formato | «Escriba un correo válido, por ejemplo nombre@correo.com.» | Cumple |
| CP-08 | HU-02 | Longitud | Contraseña «123» y confirmación «456» | Rechazo por longitud y por no coincidir | Mensajes de 6 a 50 caracteres y «Las contraseñas no coinciden.» | Cumple |
| CP-09 | HU-02 | Longitud | Nombres de 63 caracteres | Rechazo: máximo 60 | «Los nombres no pueden superar 60 caracteres.» | Cumple |
| CP-10 | HU-02 | Número / longitud | Cédula «12345» (5 dígitos) | Rechazo: mínimo 6 dígitos | Mensaje de cédula inválida | Cumple |
| CP-11 | HU-02 | Fecha | Fecha 31/12/1899 | Rechazo: no puede ser anterior a 1900 | «La fecha de nacimiento no puede ser anterior a 1900.» | Cumple |
| CP-12 | HU-02 | Caracteres especiales | Apellidos «O'Neill-Ríos» | Aceptado (apóstrofo, guion y tilde) | El campo no muestra error | Cumple |
| CP-13 | HU-02 | Funcional | Datos correctos; correo «Maria.Gomez@Correo.com» | Cuenta creada y redirección al login | «Su cuenta quedó registrada. Ya puede iniciar sesión.» | Cumple |
| CP-14 | HU-02 | Unicidad | Misma cédula 1012345678 otra vez | Rechazo de la cédula repetida | «Ya existe un usuario registrado con esa cédula.» | Cumple |
| CP-15 | HU-01 | Funcional | MARIA.GOMEZ@CORREO.COM y su contraseña | Inicio de sesión sin importar mayúsculas | Panel con «Bienvenido(a), María José.» | Cumple |
| CP-16 | HU-03 | Funcional | Abrir /usuarios | Lista ordenada por id | Tres usuarios con sus datos y acciones | Cumple |
| CP-17 | HU-03 | Funcional | Buscar id 3 | Solo el usuario 3 | Se muestra María José Gómez Rincón | Cumple |
| CP-18 | HU-03 | Número | Buscar id 999 | Mensaje de no encontrado | «No existe un usuario con el id 999.» | Cumple |
| CP-19 | HU-03 | Número | Buscar id «abc» en la URL | Rechazo controlado | Página de error con código 400, sin caída de la aplicación | Cumple |
| CP-20 | HU-04 | Caracteres especiales | Luis Ángel O'Connor-Díaz, cédula 79865432 | Usuario registrado | «El usuario quedó registrado correctamente.» | Cumple |
| CP-21 | HU-05 | Unicidad | Cambiar el correo al de otra cuenta | Rechazo del correo repetido | «Ya existe un usuario registrado con ese correo.» | Cumple |
| CP-22 | HU-05 | Funcional | Nuevos apellidos, correo y fecha; contraseña en blanco | Datos actualizados y contraseña conservada | «Los datos del usuario se actualizaron correctamente.» | Cumple |
| CP-23 | HU-06 | Funcional | Eliminar el usuario 4 y confirmar | El usuario sale de la lista | «El usuario fue eliminado.»; quedan 3 usuarios | Cumple |
| CP-24 | HU-06 | Número | Abrir /usuarios/999/editar | Página de error 404 | «No existe un usuario con el id 999.» con código 404 | Cumple |
| CP-25 | HU-07 | Funcional | Filtrar por categoría Multimedia | Solo guías de esa categoría | Se muestra «Tomar una foto» | Cumple |
| CP-26 | HU-07 | Obligatorio | Guía sin título y sin categoría | Rechazo de ambos campos | «Escriba el título de la guía.» y «Seleccione una categoría.» | Cumple |
| CP-27 | HU-07 | Longitud | Título de 112 y descripción de 256 caracteres | Rechazo: máximos 100 y 255 | Mensajes de longitud en los dos campos | Cumple |
| CP-28 | HU-07 | Caracteres especiales | Título con ¿ ? ( ) & y <b>más</b>; descripción con ñ, ü, #, %, € y emoji | Se guarda y se muestra como texto | La guía 4 se registra; las etiquetas se ven como texto y no se ejecutan | Cumple |
| CP-29 | HU-08 | Número / obligatorio | Número 0, título vacío, instrucción con espacios | Rechazo de los tres campos | «El número del paso debe ser mayor que cero.» y campos obligatorios | Cumple |
| CP-30 | HU-08 | Número | Número del paso «abc» | Rechazo de tipo de dato | «Escriba un número entero válido.» | Cumple |
| CP-31 | HU-08 | Longitud | Número 51, título de 101 e instrucción de 256 caracteres | Rechazo de los tres límites | Mensajes de máximo 50, 100 y 255 | Cumple |
| CP-32 | HU-08 | Funcional | Pasos 1, 2 y 3 válidos | Pasos agregados en orden | «Se agregó el paso 3.»; el formulario sugiere el 4 | Cumple |
| CP-33 | HU-08 | Unicidad | Agregar otro paso con el número 2 | Rechazo del número repetido | «Esta guía ya tiene un paso con el número 2.» | Cumple |
| CP-34 | HU-08 | Unicidad | Editar el paso 3 y cambiarle el número a 1 | Rechazo del número repetido | «Esta guía ya tiene un paso con el número 1.» | Cumple |
| CP-35 | HU-08 | Funcional | Editar título e instrucción del paso 3 | Paso actualizado | «El paso se actualizó correctamente.» | Cumple |
| CP-36 | HU-08 | Funcional | Eliminar un paso temporal (número 4) | Paso eliminado | «El paso fue eliminado.»; quedan 3 pasos | Cumple |
| CP-37 | HU-09 | Funcional | Editar título y descripción de la guía 4 | Guía actualizada con sus pasos | «La guía se actualizó correctamente.» | Cumple |
| CP-38 | HU-09 | Funcional | Eliminar una guía temporal | Guía y pasos eliminados | «La guía y sus pasos fueron eliminados.» | Cumple |
| CP-39 | HU-10 | Funcional | Filtro Comunicación en la app móvil | Guías de comunicación | Aparecen «Hacer una llamada» y la nueva guía de WhatsApp | Cumple |
| CP-40 | HU-10 | Funcional | Empezar la guía y avanzar con Siguiente | Paso X de N con barra de progreso | Paso 1 de 3, 2 de 3 y 3 de 3 con el botón Terminar | Cumple |
| CP-41 | HU-10 | Funcional | Pulsar Terminar | Mensaje de guía terminada | «¡Lo logró!» y botón para volver a las guías | Cumple |
| CP-42 | HU-10 | Funcional | GET /agi/api/guias/4 | JSON con la guía y sus pasos en orden | Código 200 con los 3 pasos | Cumple |
| CP-43 | HU-10 | Número | GET /agi/api/guias/999 | Código 404 en JSON | 404 «Recurso no encontrado» | Cumple |
| CP-44 | HU-10 | Número | GET /agi/api/guias/abc | Código 400 en JSON | 400 «El parámetro 'idGuia' debe ser un número entero.» | Cumple |
| CP-45 | HU-11 | Seguridad | Pulsar Salir | Sesión cerrada | «Cerró sesión correctamente.» | Cumple |
| CP-46 | HU-11 | Seguridad | Abrir /guias sin sesión | Redirección al inicio de sesión | «Inicie sesión para continuar.» | Cumple |
| CP-47 | HU-01 | Seguridad | Abrir /usuarios sin sesión (al inicio de las pruebas) | Redirección al inicio de sesión | Se redirige a /login?sesion con el aviso | Cumple |

## Pruebas automatizadas

| Clase de prueba | Qué valida | Pruebas |
|---|---|---|
| dto.UsuarioFormularioTest | Reglas de Bean Validation del formulario de usuario: textos, números, fechas, caracteres especiales y límites de longitud | 14 |
| dto.GuiaPasoFormularioTest | Reglas de los formularios de guía y de paso | 10 |
| servicio.ContrasenaServiceTest | Cifrado BCrypt y compatibilidad con el SHA-256 de la versión 2.0 | 5 |
| servicio.UsuarioServiceTest | Registro, inicio de sesión, actualización y eliminación con el repositorio simulado (Mockito) | 9 |
| servicio.PasoServiceTest | Número de paso único por guía y sugerencia del siguiente número | 5 |
| controlador.UsuarioControllerTest | Formulario POST /registro con MockMvc | 6 |
| controlador.InicioControllerTest | Inicio de sesión e interceptor de rutas protegidas | 5 |
| api.GuiaApiControllerTest | Respuestas JSON de la API REST (200, 404 y 400) | 4 |
| **Total** | | **58** |

Para ejecutarlas: `.\mvnw.cmd test` (Windows) o `./mvnw test` (Linux/Mac). No necesitan MySQL encendido: los repositorios se reemplazan por objetos simulados.
