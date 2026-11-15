package co.edu.sena.agi.servicio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.regex.Pattern;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Cifra y verifica las contraseñas de los usuarios.
 * <p>
 * Desde la versión 3.0 las contraseñas se guardan con BCrypt, como lo define el
 * modelo de datos de AGI. Las cuentas creadas con la versión 2.0 tienen un hash
 * SHA-256; este servicio las sigue reconociendo para que la persona pueda entrar
 * y, al hacerlo, su hash se actualiza a BCrypt.
 * </p>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Service
public class ContrasenaService {

    /** Un hash SHA-256 en hexadecimal tiene exactamente 64 caracteres. */
    private static final Pattern PATRON_HASH_SHA256 = Pattern.compile("^[0-9a-f]{64}$");

    private static final String ALGORITMO_ANTERIOR = "SHA-256";

    private final PasswordEncoder codificadorBcrypt = new BCryptPasswordEncoder();

    /**
     * Genera el hash BCrypt de una contraseña.
     *
     * @param contrasena texto que escribió la persona
     * @return hash que se guarda en la columna contrasena
     */
    public String cifrarContrasena(String contrasena) {
        return codificadorBcrypt.encode(contrasena);
    }

    /**
     * Compara la contraseña escrita con el hash guardado.
     *
     * @param contrasena   texto que escribió la persona
     * @param hashGuardado valor de la columna contrasena
     * @return true si la contraseña es correcta
     */
    public boolean verificarContrasena(String contrasena, String hashGuardado) {
        if (contrasena == null || hashGuardado == null) {
            return false;
        }
        if (esHashAnterior(hashGuardado)) {
            return calcularSha256(contrasena).equals(hashGuardado);
        }
        return codificadorBcrypt.matches(contrasena, hashGuardado);
    }

    /**
     * Indica si el hash fue generado por la versión 2.0 (SHA-256).
     *
     * @param hashGuardado valor de la columna contrasena
     * @return true si hay que actualizarlo a BCrypt
     */
    public boolean esHashAnterior(String hashGuardado) {
        return hashGuardado != null && PATRON_HASH_SHA256.matcher(hashGuardado).matches();
    }

    /** Calcula el SHA-256 en hexadecimal, igual que lo hacía SeguridadUtil en la versión 2.0. */
    private String calcularSha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITMO_ANTERIOR);
            byte[] bytesHash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytesHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("El algoritmo " + ALGORITMO_ANTERIOR + " no está disponible", e);
        }
    }
}
