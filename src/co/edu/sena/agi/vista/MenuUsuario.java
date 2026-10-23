package co.edu.sena.agi.vista;

import co.edu.sena.agi.dao.UsuarioDAO;
import co.edu.sena.agi.dao.UsuarioDAOImpl;
import co.edu.sena.agi.modelo.Usuario;
import co.edu.sena.agi.util.SeguridadUtil;
import co.edu.sena.agi.util.ValidacionUtil;
import java.io.Console;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menú de consola del módulo de gestión de usuarios de AGI.
 * Conecta lo que escribe el usuario con las operaciones CRUD del DAO.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public class MenuUsuario {

    private static final String LINEA = "-".repeat(90);
    private static final String ESTADO_DUPLICADO = "23";

    private final UsuarioDAO usuarioDao = new UsuarioDAOImpl();
    private final Scanner lector;

    public MenuUsuario() {
        // Lee el teclado con la codificación de la consola para respetar tildes y eñes
        Console consola = System.console();
        Charset codificacion = (consola != null) ? consola.charset() : Charset.defaultCharset();
        this.lector = new Scanner(System.in, codificacion);
    }

    /** Muestra el menú hasta que el usuario elija salir. */
    public void iniciar() {
        int opcion;
        do {
            mostrarOpciones();
            opcion = leerEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1 -> registrarUsuario();
                case 2 -> listarUsuarios();
                case 3 -> buscarUsuario();
                case 4 -> actualizarUsuario();
                case 5 -> eliminarUsuario();
                case 0 -> System.out.println("Hasta pronto.");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void mostrarOpciones() {
        System.out.println();
        System.out.println("===== AGI - Gestión de usuarios =====");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Listar usuarios");
        System.out.println("3. Buscar usuario por id");
        System.out.println("4. Actualizar usuario");
        System.out.println("5. Eliminar usuario");
        System.out.println("0. Salir");
    }

    // ---------------------------------------------------------------
    // C - Create
    // ---------------------------------------------------------------
    private void registrarUsuario() {
        System.out.println("\n--- Registrar usuario ---");
        String nombres = leerTextoObligatorio("Nombres: ");
        String apellidos = leerTextoObligatorio("Apellidos: ");
        String cedula = leerCedula("Cédula: ");
        String correo = leerCorreo("Correo: ");
        String contrasena = leerContrasena("Contraseña (mínimo 6 caracteres): ");
        LocalDate fechaNacimiento = leerFecha("Fecha de nacimiento (AAAA-MM-DD): ");

        Usuario nuevoUsuario = new Usuario(nombres, apellidos, cedula, correo,
                SeguridadUtil.cifrarContrasena(contrasena), fechaNacimiento);
        try {
            int idGenerado = usuarioDao.insertarUsuario(nuevoUsuario);
            System.out.println("Usuario registrado con id " + idGenerado + ".");
        } catch (SQLException e) {
            mostrarErrorBaseDatos(e);
        }
    }

    // ---------------------------------------------------------------
    // R - Read
    // ---------------------------------------------------------------
    private void listarUsuarios() {
        System.out.println("\n--- Usuarios registrados ---");
        try {
            List<Usuario> usuarios = usuarioDao.listarUsuarios();
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios registrados.");
                return;
            }
            System.out.printf("%-4s %-28s %-12s %-30s %s%n",
                    "ID", "NOMBRE COMPLETO", "CÉDULA", "CORREO", "NACIMIENTO");
            System.out.println(LINEA);
            usuarios.forEach(System.out::println);
            System.out.println(LINEA);
            System.out.println("Total: " + usuarios.size() + " usuario(s).");
        } catch (SQLException e) {
            mostrarErrorBaseDatos(e);
        }
    }

    private void buscarUsuario() {
        System.out.println("\n--- Buscar usuario ---");
        int idUsuario = leerEntero("Id del usuario: ");
        try {
            Optional<Usuario> usuarioEncontrado = usuarioDao.buscarUsuarioPorId(idUsuario);
            if (usuarioEncontrado.isPresent()) {
                mostrarDetalle(usuarioEncontrado.get());
            } else {
                System.out.println("No existe un usuario con id " + idUsuario + ".");
            }
        } catch (SQLException e) {
            mostrarErrorBaseDatos(e);
        }
    }

    // ---------------------------------------------------------------
    // U - Update
    // ---------------------------------------------------------------
    private void actualizarUsuario() {
        System.out.println("\n--- Actualizar usuario ---");
        int idUsuario = leerEntero("Id del usuario a actualizar: ");
        try {
            Optional<Usuario> usuarioEncontrado = usuarioDao.buscarUsuarioPorId(idUsuario);
            if (usuarioEncontrado.isEmpty()) {
                System.out.println("No existe un usuario con id " + idUsuario + ".");
                return;
            }
            Usuario usuario = usuarioEncontrado.get();
            mostrarDetalle(usuario);
            System.out.println("Presione Enter para conservar el valor actual.");

            usuario.setNombres(leerTextoOpcional("Nombres", usuario.getNombres()));
            usuario.setApellidos(leerTextoOpcional("Apellidos", usuario.getApellidos()));
            usuario.setCedula(leerCedulaOpcional(usuario.getCedula()));
            usuario.setCorreo(leerCorreoOpcional(usuario.getCorreo()));
            usuario.setFechaNacimiento(leerFechaOpcional(usuario.getFechaNacimiento()));

            String nuevaContrasena = leerLinea("Nueva contraseña (Enter para no cambiarla): ");
            if (!nuevaContrasena.isEmpty()) {
                if (ValidacionUtil.esContrasenaValida(nuevaContrasena)) {
                    usuario.setContrasena(SeguridadUtil.cifrarContrasena(nuevaContrasena));
                } else {
                    System.out.println("Contraseña muy corta: se conserva la anterior.");
                }
            }

            if (usuarioDao.actualizarUsuario(usuario)) {
                System.out.println("Usuario actualizado correctamente.");
            }
        } catch (SQLException e) {
            mostrarErrorBaseDatos(e);
        }
    }

    // ---------------------------------------------------------------
    // D - Delete
    // ---------------------------------------------------------------
    private void eliminarUsuario() {
        System.out.println("\n--- Eliminar usuario ---");
        int idUsuario = leerEntero("Id del usuario a eliminar: ");
        try {
            Optional<Usuario> usuarioEncontrado = usuarioDao.buscarUsuarioPorId(idUsuario);
            if (usuarioEncontrado.isEmpty()) {
                System.out.println("No existe un usuario con id " + idUsuario + ".");
                return;
            }
            String confirmacion = leerLinea("¿Eliminar a "
                    + usuarioEncontrado.get().obtenerNombreCompleto() + "? (S/N): ");
            if (confirmacion.equalsIgnoreCase("S")) {
                if (usuarioDao.eliminarUsuario(idUsuario)) {
                    System.out.println("Usuario eliminado.");
                }
            } else {
                System.out.println("Operación cancelada.");
            }
        } catch (SQLException e) {
            mostrarErrorBaseDatos(e);
        }
    }

    // ---------------------------------------------------------------
    // Métodos de apoyo para leer y mostrar datos
    // ---------------------------------------------------------------
    private void mostrarDetalle(Usuario usuario) {
        System.out.println("Id: " + usuario.getIdUsuario());
        System.out.println("Nombre: " + usuario.obtenerNombreCompleto());
        System.out.println("Cédula: " + usuario.getCedula());
        System.out.println("Correo: " + usuario.getCorreo());
        System.out.println("Fecha de nacimiento: " + usuario.getFechaNacimiento());
    }

    private void mostrarErrorBaseDatos(SQLException e) {
        String estado = e.getSQLState();
        if (estado != null && estado.startsWith(ESTADO_DUPLICADO)) {
            System.out.println("No se guardó: la cédula o el correo ya están registrados.");
        } else {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return lector.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = leerLinea(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Escriba un número entero.");
            }
        }
    }

    private String leerTextoObligatorio(String mensaje) {
        String texto = leerLinea(mensaje);
        while (!ValidacionUtil.esTextoValido(texto)) {
            System.out.println("Este campo es obligatorio.");
            texto = leerLinea(mensaje);
        }
        return texto;
    }

    private String leerCedula(String mensaje) {
        String cedula = leerLinea(mensaje);
        while (!ValidacionUtil.esCedulaValida(cedula)) {
            System.out.println("La cédula debe tener solo números (6 a 15 dígitos).");
            cedula = leerLinea(mensaje);
        }
        return cedula;
    }

    private String leerCorreo(String mensaje) {
        String correo = leerLinea(mensaje);
        while (!ValidacionUtil.esCorreoValido(correo)) {
            System.out.println("Correo no válido. Ejemplo: nombre@correo.com");
            correo = leerLinea(mensaje);
        }
        return correo.toLowerCase();
    }

    private String leerContrasena(String mensaje) {
        String contrasena = leerLinea(mensaje);
        while (!ValidacionUtil.esContrasenaValida(contrasena)) {
            System.out.println("La contraseña debe tener al menos 6 caracteres.");
            contrasena = leerLinea(mensaje);
        }
        return contrasena;
    }

    private LocalDate leerFecha(String mensaje) {
        LocalDate fecha = ValidacionUtil.convertirFecha(leerLinea(mensaje));
        while (fecha == null) {
            System.out.println("Fecha no válida. Use el formato AAAA-MM-DD, por ejemplo 1958-04-23.");
            fecha = ValidacionUtil.convertirFecha(leerLinea(mensaje));
        }
        return fecha;
    }

    private String leerTextoOpcional(String campo, String valorActual) {
        String texto = leerLinea(campo + " [" + valorActual + "]: ");
        return texto.isEmpty() ? valorActual : texto;
    }

    private String leerCedulaOpcional(String valorActual) {
        String cedula = leerLinea("Cédula [" + valorActual + "]: ");
        if (cedula.isEmpty()) {
            return valorActual;
        }
        if (!ValidacionUtil.esCedulaValida(cedula)) {
            System.out.println("Cédula no válida: se conserva la actual.");
            return valorActual;
        }
        return cedula;
    }

    private String leerCorreoOpcional(String valorActual) {
        String correo = leerLinea("Correo [" + valorActual + "]: ");
        if (correo.isEmpty()) {
            return valorActual;
        }
        if (!ValidacionUtil.esCorreoValido(correo)) {
            System.out.println("Correo no válido: se conserva el actual.");
            return valorActual;
        }
        return correo.toLowerCase();
    }

    private LocalDate leerFechaOpcional(LocalDate valorActual) {
        String texto = leerLinea("Fecha de nacimiento [" + valorActual + "]: ");
        if (texto.isEmpty()) {
            return valorActual;
        }
        LocalDate fecha = ValidacionUtil.convertirFecha(texto);
        if (fecha == null) {
            System.out.println("Fecha no válida: se conserva la actual.");
            return valorActual;
        }
        return fecha;
    }
}
