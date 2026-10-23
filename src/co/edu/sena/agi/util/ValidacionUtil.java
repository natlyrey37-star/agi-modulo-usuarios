package co.edu.sena.agi.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Reglas de validación de los datos que escribe el usuario
 * antes de enviarlos a la base de datos.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public final class ValidacionUtil {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PATRON_CEDULA = Pattern.compile("^\\d{6,15}$");
    private static final int LONGITUD_MINIMA_CLAVE = 6;

    private ValidacionUtil() {
    }

    public static boolean esTextoValido(String texto) {
        return texto != null && !texto.isBlank();
    }

    public static boolean esCorreoValido(String correo) {
        return correo != null && PATRON_CORREO.matcher(correo).matches();
    }

    public static boolean esCedulaValida(String cedula) {
        return cedula != null && PATRON_CEDULA.matcher(cedula).matches();
    }

    public static boolean esContrasenaValida(String contrasena) {
        return contrasena != null && contrasena.length() >= LONGITUD_MINIMA_CLAVE;
    }

    /**
     * Convierte un texto con formato AAAA-MM-DD en fecha.
     * Devuelve null si el formato no es válido o la fecha es futura.
     */
    public static LocalDate convertirFecha(String texto) {
        try {
            LocalDate fecha = LocalDate.parse(texto);
            return fecha.isAfter(LocalDate.now()) ? null : fecha;
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
