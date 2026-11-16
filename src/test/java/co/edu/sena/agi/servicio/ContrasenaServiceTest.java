package co.edu.sena.agi.servicio;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas del cifrado y la verificación de contraseñas.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("Cifrado de contraseñas")
class ContrasenaServiceTest {

    /** SHA-256 de "Clave2026", como lo guardaba la versión 2.0 del módulo. */
    private static final String HASH_SHA256_CLAVE_2026 =
            "cbcf908f64ee2fc641087d35e42f4581584615de96670bdab2bef1a522bbe148";

    private final ContrasenaService contrasenaService = new ContrasenaService();

    @Test
    @DisplayName("La contraseña nunca se guarda como texto plano")
    void cifrarNoDevuelveElTextoOriginal() {
        String hash = contrasenaService.cifrarContrasena("Clave2026");
        assertNotEquals("Clave2026", hash);
        assertTrue(hash.startsWith("$2"), "El hash debe tener el formato de BCrypt");
    }

    @Test
    @DisplayName("Dos cifrados de la misma contraseña son distintos (BCrypt usa sal)")
    void cifrarDosVecesDaHashesDistintos() {
        assertNotEquals(contrasenaService.cifrarContrasena("Clave2026"),
                contrasenaService.cifrarContrasena("Clave2026"));
    }

    @Test
    @DisplayName("Verifica la contraseña correcta y rechaza la incorrecta")
    void verificarContrasena() {
        String hash = contrasenaService.cifrarContrasena("Clave2026");
        assertTrue(contrasenaService.verificarContrasena("Clave2026", hash));
        assertFalse(contrasenaService.verificarContrasena("clave2026", hash));
        assertFalse(contrasenaService.verificarContrasena("", hash));
    }

    @Test
    @DisplayName("Los valores nulos nunca se consideran una contraseña válida")
    void verificarConNulosDevuelveFalso() {
        assertFalse(contrasenaService.verificarContrasena(null, "hash"));
        assertFalse(contrasenaService.verificarContrasena("Clave2026", null));
    }

    @Test
    @DisplayName("Reconoce y verifica los hashes SHA-256 de la versión 2.0")
    void reconoceHashDeLaVersionAnterior() {
        assertTrue(contrasenaService.esHashAnterior(HASH_SHA256_CLAVE_2026));
        assertTrue(contrasenaService.verificarContrasena("Clave2026", HASH_SHA256_CLAVE_2026));
        assertFalse(contrasenaService.esHashAnterior(contrasenaService.cifrarContrasena("Clave2026")));
    }
}
