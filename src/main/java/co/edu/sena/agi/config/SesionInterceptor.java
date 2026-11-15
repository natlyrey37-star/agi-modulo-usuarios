package co.edu.sena.agi.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Protege las páginas de administración: si no hay una sesión iniciada,
 * redirige al inicio de sesión antes de que se ejecute el controlador.
 * Cumple la función que en la versión 2.0 hacía el método haySesion() de cada servlet.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public class SesionInterceptor implements HandlerInterceptor {

    /** Nombre del atributo de sesión que guarda al usuario autenticado. */
    public static final String ATRIBUTO_USUARIO_SESION = "usuarioSesion";

    /**
     * Se ejecuta antes de cada petición a una ruta protegida.
     *
     * @return true para continuar; false si se redirigió al inicio de sesión
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        HttpSession sesion = request.getSession(false);
        if (sesion != null && sesion.getAttribute(ATRIBUTO_USUARIO_SESION) != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login?sesion");
        return false;
    }
}
