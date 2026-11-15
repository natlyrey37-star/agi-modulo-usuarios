package co.edu.sena.agi.excepcion;

/**
 * Se lanza cuando un dato que debe ser único ya existe: la cédula o el correo
 * de un usuario, o el número de un paso dentro de la misma guía.
 * Guarda el nombre del campo para mostrar el error junto a él en el formulario.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public class DatoDuplicadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** Nombre del campo del formulario que tiene el valor repetido. */
    private final String campo;

    /**
     * @param campo   nombre del campo del formulario (por ejemplo, "cedula")
     * @param mensaje texto que se le muestra a la persona
     */
    public DatoDuplicadoException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }
}
