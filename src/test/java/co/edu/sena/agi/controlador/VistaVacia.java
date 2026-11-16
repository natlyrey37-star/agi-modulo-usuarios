package co.edu.sena.agi.controlador;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Vista que no dibuja nada. En las pruebas de los controladores solo interesa
 * qué vista se eligió y qué datos se le pasaron, no el HTML de Thymeleaf.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
class VistaVacia extends AbstractView {

    private static final String PREFIJO_REDIRECCION = "redirect:";

    /**
     * Resolutor de vistas para MockMvc: las redirecciones ("redirect:/login") se
     * resuelven de verdad y cualquier otra vista se cambia por una vista vacía.
     *
     * @return resolutor que se registra con setViewResolvers(...)
     */
    static ViewResolver crearResolutor() {
        return (String nombreVista, Locale idioma) -> {
            if (nombreVista.startsWith(PREFIJO_REDIRECCION)) {
                return new RedirectView(nombreVista.substring(PREFIJO_REDIRECCION.length()), true);
            }
            return new VistaVacia();
        };
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) {
        // No se genera contenido: la prueba revisa el nombre de la vista y el modelo
    }
}
