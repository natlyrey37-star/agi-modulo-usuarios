package co.edu.sena.agi.controlador;

import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Convierte los errores de los controladores web en una página amable para la persona.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@ControllerAdvice(basePackages = "co.edu.sena.agi.controlador")
public class ManejadorErroresWeb {

    /**
     * Usuario, guía o paso que no existe: muestra la página de error con código 404.
     *
     * @param excepcion error con el mensaje para la persona
     * @param model     datos para la vista
     * @return vista error.html
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String manejarRecursoNoEncontrado(RecursoNoEncontradoException excepcion, Model model) {
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("mensaje", excepcion.getMessage());
        return "error";
    }
}
