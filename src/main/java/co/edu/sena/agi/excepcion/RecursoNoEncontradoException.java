package co.edu.sena.agi.excepcion;

/**
 * Se lanza cuando se pide un usuario, una guía o un paso que no existe.
 * La web la convierte en la página de error 404 y la API en una respuesta JSON 404.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje texto que se le muestra a la persona
     */
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
