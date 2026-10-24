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
import java.util.Optional;

/**
 * Controla el inicio y el cierre de sesión de AGI.
 * <ul>
 *   <li>GET /login: muestra el formulario; con accion=salir cierra la sesión.</li>
 *   <li>POST /login: valida el correo y la contraseña contra la tabla usuario.</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Nombre del atributo de sesión que guarda al usuario autenticado. */
    public static final String ATRIBUTO_USUARIO_SESION = "usuarioSesion";

    private static final String VISTA_LOGIN = "/index.jsp";
    private static final String ACCION_SALIR = "salir";
    private static final int MINUTOS_SESION = 30;

    private final transient UsuarioDAO usuarioDao = new UsuarioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (ACCION_SALIR.equals(request.getParameter("accion"))) {
            HttpSession sesion = request.getSession(false);
            if (sesion != null) {
                sesion.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/index.jsp?ok=salida");
            return;
        }
        request.getRequestDispatcher(VISTA_LOGIN).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String correo = valorLimpio(request.getParameter("correo")).toLowerCase();
        String contrasena = valorLimpio(request.getParameter("contrasena"));

        if (!ValidacionUtil.esTextoValido(correo) || !ValidacionUtil.esTextoValido(contrasena)) {
            mostrarError(request, response, correo, "Escriba su correo y su contraseña.");
            return;
        }

        try {
            Optional<Usuario> usuarioEncontrado = usuarioDao.buscarUsuarioPorCorreo(correo);
            if (usuarioEncontrado.isPresent()
                    && SeguridadUtil.verificarContrasena(contrasena, usuarioEncontrado.get().getContrasena())) {

                Usuario usuario = usuarioEncontrado.get();
                usuario.setContrasena(null); // el hash no se guarda en la sesión

                // Se cambia el id de sesión al autenticar para evitar fijación de sesión
                HttpSession sesion = request.getSession();
                request.changeSessionId();
                sesion.setAttribute(ATRIBUTO_USUARIO_SESION, usuario);
                sesion.setMaxInactiveInterval(MINUTOS_SESION * 60);

                response.sendRedirect(request.getContextPath() + "/usuarios?ok=bienvenida");
            } else {
                mostrarError(request, response, correo, "El correo o la contraseña no son correctos.");
            }
        } catch (SQLException e) {
            throw new ServletException("No fue posible consultar la base de datos", e);
        }
    }

    /** Vuelve al formulario con el mensaje de error y el correo ya escrito. */
    private void mostrarError(HttpServletRequest request, HttpServletResponse response,
                              String correo, String mensaje) throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.setAttribute("correoEscrito", correo);
        request.getRequestDispatcher(VISTA_LOGIN).forward(request, response);
    }

    private String valorLimpio(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
