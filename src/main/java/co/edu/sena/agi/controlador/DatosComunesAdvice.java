package co.edu.sena.agi.controlador;

import co.edu.sena.agi.config.SesionInterceptor;
import co.edu.sena.agi.dto.UsuarioSesion;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Comportamiento común a todos los controladores web de AGI.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@ControllerAdvice(basePackages = "co.edu.sena.agi.controlador")
public class DatosComunesAdvice {

    /**
     * Quita los espacios al inicio y al final de los textos de los formularios,
     * y convierte los campos vacíos en null para que las validaciones los detecten.
     *
     * @param binder objeto de Spring que pasa los datos del formulario al objeto Java
     */
    @InitBinder
    public void configurarFormularios(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /**
     * Deja en todas las vistas el usuario de la sesión, para el saludo del encabezado.
     *
     * @param request petición actual
     * @return usuario conectado, o null si no hay sesión
     */
    @ModelAttribute("usuarioSesion")
    public UsuarioSesion agregarUsuarioSesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return null;
        }
        return (UsuarioSesion) sesion.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO_SESION);
    }
}
