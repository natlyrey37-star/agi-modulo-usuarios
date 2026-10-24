package co.edu.sena.agi.web;

import co.edu.sena.agi.dao.UsuarioDAO;
import co.edu.sena.agi.dao.UsuarioDAOImpl;
import co.edu.sena.agi.modelo.Usuario;
import co.edu.sena.agi.util.SeguridadUtil;
import co.edu.sena.agi.util.ValidacionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controlador web del módulo de gestión de usuarios de AGI.
 * Recibe las peticiones de los formularios HTML y usa el DAO para el CRUD.
 * <ul>
 *   <li>GET  /usuarios?accion=listar | buscar | nuevo | editar → consultas y formularios.</li>
 *   <li>POST /usuarios con accion=registrar | actualizar | eliminar → cambios en la base de datos.</li>
 * </ul>
 * Registrar es público (crear cuenta); el resto exige sesión iniciada.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
@WebServlet(name = "UsuarioServlet", urlPatterns = "/usuarios")
public class UsuarioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VISTA_LISTADO = "/WEB-INF/vistas/usuarios.jsp";
    private static final String VISTA_FORMULARIO = "/WEB-INF/vistas/formulario-usuario.jsp";

    private static final String ACCION_LISTAR = "listar";
    private static final String ACCION_BUSCAR = "buscar";
    private static final String ACCION_NUEVO = "nuevo";
    private static final String ACCION_EDITAR = "editar";
    private static final String ACCION_REGISTRAR = "registrar";
    private static final String ACCION_ACTUALIZAR = "actualizar";
    private static final String ACCION_ELIMINAR = "eliminar";

    private static final String MODO_REGISTRO = "registro";
    private static final String MODO_EDICION = "edicion";

    /** Las violaciones de UNIQUE (cédula o correo repetidos) tienen SQLState 23xxx. */
    private static final String ESTADO_DUPLICADO = "23";

    private final transient UsuarioDAO usuarioDao = new UsuarioDAOImpl();

    // =================================================================
    // GET: consultas y formularios (no modifican la base de datos)
    // =================================================================
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = obtenerAccion(request, ACCION_LISTAR);

        if (ACCION_NUEVO.equals(accion)) {
            mostrarFormulario(request, response, new Usuario(), MODO_REGISTRO, List.of());
            return;
        }
        if (!haySesion(request)) {
            redirigirAlLogin(request, response);
            return;
        }

        try {
            switch (accion) {
                case ACCION_BUSCAR -> buscarUsuario(request, response);
                case ACCION_EDITAR -> mostrarFormularioEdicion(request, response);
                default -> listarUsuarios(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Error al consultar la base de datos", e);
        }
    }

    // =================================================================
    // POST: operaciones que modifican la base de datos
    // =================================================================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = obtenerAccion(request, "");

        if (!ACCION_REGISTRAR.equals(accion) && !haySesion(request)) {
            redirigirAlLogin(request, response);
            return;
        }

        try {
            switch (accion) {
                case ACCION_REGISTRAR -> registrarUsuario(request, response);
                case ACCION_ACTUALIZAR -> actualizarUsuario(request, response);
                case ACCION_ELIMINAR -> eliminarUsuario(request, response);
                default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (SQLException e) {
            throw new ServletException("No fue posible guardar los cambios en la base de datos", e);
        }
    }

    // ----------------------------- Read ------------------------------
    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        request.setAttribute("usuarios", usuarioDao.listarUsuarios());
        request.getRequestDispatcher(VISTA_LISTADO).forward(request, response);
    }

    private void buscarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        String textoId = request.getParameter("id");
        Integer idUsuario = convertirEntero(textoId);
        request.setAttribute("busqueda", textoId == null ? "" : textoId.trim());

        if (idUsuario == null) {
            request.setAttribute("error", "Escriba un número de id válido para buscar.");
            request.setAttribute("usuarios", List.of());
        } else {
            Optional<Usuario> usuarioEncontrado = usuarioDao.buscarUsuarioPorId(idUsuario);
            if (usuarioEncontrado.isPresent()) {
                request.setAttribute("usuarios", List.of(usuarioEncontrado.get()));
            } else {
                request.setAttribute("error", "No existe un usuario con id " + idUsuario + ".");
                request.setAttribute("usuarios", List.of());
            }
        }
        request.getRequestDispatcher(VISTA_LISTADO).forward(request, response);
    }

    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        Integer idUsuario = convertirEntero(request.getParameter("id"));
        Optional<Usuario> usuarioEncontrado = (idUsuario == null)
                ? Optional.empty() : usuarioDao.buscarUsuarioPorId(idUsuario);

        if (usuarioEncontrado.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/usuarios?error=noexiste");
            return;
        }
        mostrarFormulario(request, response, usuarioEncontrado.get(), MODO_EDICION, List.of());
    }

    // ---------------------------- Create -----------------------------
    private void registrarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        Usuario nuevoUsuario = leerUsuarioDelFormulario(request);
        String contrasena = valorLimpio(request.getParameter("contrasena"));
        List<String> errores = validarUsuario(nuevoUsuario, contrasena, true);

        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, nuevoUsuario, MODO_REGISTRO, errores);
            return;
        }

        nuevoUsuario.setContrasena(SeguridadUtil.cifrarContrasena(contrasena));
        try {
            usuarioDao.insertarUsuario(nuevoUsuario);
        } catch (SQLException e) {
            if (esDuplicado(e)) {
                mostrarFormulario(request, response, nuevoUsuario, MODO_REGISTRO,
                        List.of("La cédula o el correo ya están registrados."));
                return;
            }
            throw e;
        }

        // Si quien registra ya tiene sesión, vuelve al listado; si no, al inicio de sesión
        String destino = haySesion(request) ? "/usuarios?ok=registrado" : "/index.jsp?ok=registrado";
        response.sendRedirect(request.getContextPath() + destino);
    }

    // ---------------------------- Update -----------------------------
    private void actualizarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {

        Integer idUsuario = convertirEntero(request.getParameter("idUsuario"));
        Optional<Usuario> usuarioGuardado = (idUsuario == null)
                ? Optional.empty() : usuarioDao.buscarUsuarioPorId(idUsuario);

        if (usuarioGuardado.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/usuarios?error=noexiste");
            return;
        }

        Usuario usuario = leerUsuarioDelFormulario(request);
        usuario.setIdUsuario(idUsuario);
        String nuevaContrasena = valorLimpio(request.getParameter("contrasena"));
        List<String> errores = validarUsuario(usuario, nuevaContrasena, false);

        if (!errores.isEmpty()) {
            mostrarFormulario(request, response, usuario, MODO_EDICION, errores);
            return;
        }

        // Si no escribió contraseña nueva, se conserva el hash anterior
        usuario.setContrasena(nuevaContrasena.isEmpty()
                ? usuarioGuardado.get().getContrasena()
                : SeguridadUtil.cifrarContrasena(nuevaContrasena));

        try {
            usuarioDao.actualizarUsuario(usuario);
        } catch (SQLException e) {
            if (esDuplicado(e)) {
                mostrarFormulario(request, response, usuario, MODO_EDICION,
                        List.of("La cédula o el correo ya pertenecen a otro usuario."));
                return;
            }
            throw e;
        }

        actualizarUsuarioEnSesion(request, usuario);
        response.sendRedirect(request.getContextPath() + "/usuarios?ok=actualizado");
    }

    // ---------------------------- Delete -----------------------------
    private void eliminarUsuario(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {

        Integer idUsuario = convertirEntero(request.getParameter("idUsuario"));
        boolean eliminado = idUsuario != null && usuarioDao.eliminarUsuario(idUsuario);

        if (!eliminado) {
            response.sendRedirect(request.getContextPath() + "/usuarios?error=noexiste");
            return;
        }
        // Si el usuario eliminó su propia cuenta, se cierra la sesión
        if (idUsuario.equals(obtenerIdUsuarioSesion(request))) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/index.jsp?ok=cuentaeliminada");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/usuarios?ok=eliminado");
    }

    // =================================================================
    // Métodos de apoyo
    // =================================================================

    /** Construye un Usuario con los parámetros enviados por el formulario. */
    private Usuario leerUsuarioDelFormulario(HttpServletRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombres(valorLimpio(request.getParameter("nombres")));
        usuario.setApellidos(valorLimpio(request.getParameter("apellidos")));
        usuario.setCedula(valorLimpio(request.getParameter("cedula")));
        usuario.setCorreo(valorLimpio(request.getParameter("correo")).toLowerCase());
        usuario.setFechaNacimiento(ValidacionUtil.convertirFecha(valorLimpio(request.getParameter("fechaNacimiento"))));
        return usuario;
    }

    /**
     * Revisa los datos en el servidor, aunque el navegador ya haya validado:
     * la validación del navegador se puede saltar, la del servidor no.
     */
    private List<String> validarUsuario(Usuario usuario, String contrasena, boolean contrasenaObligatoria) {
        List<String> errores = new ArrayList<>();
        if (!ValidacionUtil.esTextoValido(usuario.getNombres())) {
            errores.add("Escriba los nombres.");
        }
        if (!ValidacionUtil.esTextoValido(usuario.getApellidos())) {
            errores.add("Escriba los apellidos.");
        }
        if (!ValidacionUtil.esCedulaValida(usuario.getCedula())) {
            errores.add("La cédula debe tener solo números (6 a 15 dígitos).");
        }
        if (!ValidacionUtil.esCorreoValido(usuario.getCorreo())) {
            errores.add("Escriba un correo válido, por ejemplo nombre@correo.com.");
        }
        if (usuario.getFechaNacimiento() == null) {
            errores.add("Escriba una fecha de nacimiento válida que no sea futura.");
        }
        boolean escribioContrasena = !contrasena.isEmpty();
        if ((contrasenaObligatoria || escribioContrasena) && !ValidacionUtil.esContrasenaValida(contrasena)) {
            errores.add("La contraseña debe tener al menos 6 caracteres.");
        }
        return errores;
    }

    private void mostrarFormulario(HttpServletRequest request, HttpServletResponse response,
                                   Usuario usuario, String modo, List<String> errores)
            throws ServletException, IOException {
        request.setAttribute("usuario", usuario);
        request.setAttribute("modo", modo);
        request.setAttribute("errores", errores);
        request.getRequestDispatcher(VISTA_FORMULARIO).forward(request, response);
    }

    private String obtenerAccion(HttpServletRequest request, String accionPorDefecto) {
        String accion = request.getParameter("accion");
        return (accion == null || accion.isBlank()) ? accionPorDefecto : accion.trim();
    }

    private boolean haySesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        return sesion != null && sesion.getAttribute(LoginServlet.ATRIBUTO_USUARIO_SESION) != null;
    }

    private Integer obtenerIdUsuarioSesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        Usuario usuarioSesion = (Usuario) sesion.getAttribute(LoginServlet.ATRIBUTO_USUARIO_SESION);
        return usuarioSesion == null ? null : usuarioSesion.getIdUsuario();
    }

    /** Si el usuario editó sus propios datos, se refleja en la sesión. */
    private void actualizarUsuarioEnSesion(HttpServletRequest request, Usuario usuario) {
        Integer idUsuarioSesion = obtenerIdUsuarioSesion(request);
        if (idUsuarioSesion != null && idUsuarioSesion == usuario.getIdUsuario()) {
            Usuario copiaSesion = new Usuario(usuario.getNombres(), usuario.getApellidos(), usuario.getCedula(),
                    usuario.getCorreo(), null, usuario.getFechaNacimiento());
            copiaSesion.setIdUsuario(usuario.getIdUsuario());
            request.getSession().setAttribute(LoginServlet.ATRIBUTO_USUARIO_SESION, copiaSesion);
        }
    }

    private void redirigirAlLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/index.jsp?error=sesion");
    }

    private boolean esDuplicado(SQLException e) {
        return e.getSQLState() != null && e.getSQLState().startsWith(ESTADO_DUPLICADO);
    }

    private Integer convertirEntero(String texto) {
        try {
            return texto == null ? null : Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String valorLimpio(String valor) {
        return valor == null ? "" : valor.trim();
    }

    /** Fecha máxima permitida en el campo de fecha de nacimiento (hoy). */
    public static String obtenerFechaMaxima() {
        return LocalDate.now().toString();
    }
}
