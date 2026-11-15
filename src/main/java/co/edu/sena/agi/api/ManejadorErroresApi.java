package co.edu.sena.agi.api;

import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Convierte los errores de la API en respuestas JSON con el formato estándar
 * ProblemDetail (RFC 9457), para que la aplicación móvil sepa qué pasó.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@RestControllerAdvice(basePackages = "co.edu.sena.agi.api")
public class ManejadorErroresApi {

    /**
     * La guía pedida no existe: responde 404.
     *
     * @param excepcion error con el mensaje
     * @return cuerpo JSON del error
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail manejarRecursoNoEncontrado(RecursoNoEncontradoException excepcion) {
        ProblemDetail detalle = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, excepcion.getMessage());
        detalle.setTitle("Recurso no encontrado");
        return detalle;
    }

    /**
     * Un parámetro no tiene el tipo esperado (por ejemplo, /api/guias/abc): responde 400.
     *
     * @param excepcion error de conversión de Spring
     * @return cuerpo JSON del error
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail manejarParametroInvalido(MethodArgumentTypeMismatchException excepcion) {
        ProblemDetail detalle = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "El parámetro '" + excepcion.getName() + "' debe ser un número entero.");
        detalle.setTitle("Parámetro inválido");
        return detalle;
    }
}
