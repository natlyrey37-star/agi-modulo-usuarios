package co.edu.sena.agi.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Funciones de apoyo para proteger la contraseña del usuario.
 * La contraseña nunca se guarda en texto plano: se almacena su hash SHA-256.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public final class SeguridadUtil {

    private static final String ALGORITMO_HASH = "SHA-256";

    private SeguridadUtil() {
    }

    /**
     * Genera el hash SHA-256 de una contraseña en formato hexadecimal.
     *
     * @param contrasena texto escrito por el usuario
     * @return cadena de 64 caracteres hexadecimales
     */
    public static String cifrarContrasena(String contrasena) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO_HASH);
            byte[] bytesHash = digest.digest(contrasena.getBytes(StandardCharsets.UTF_8));
            StringBuilder hashHexadecimal = new StringBuilder();
            for (byte valor : bytesHash) {
                hashHexadecimal.append(String.format("%02x", valor));
            }
            return hashHexadecimal.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("El algoritmo " + ALGORITMO_HASH + " no está disponible", e);
        }
    }

    /**
     * Compara una contraseña escrita con el hash guardado en la base de datos.
     */
    public static boolean verificarContrasena(String contrasena, String hashGuardado) {
        return cifrarContrasena(contrasena).equals(hashGuardado);
    }
}
