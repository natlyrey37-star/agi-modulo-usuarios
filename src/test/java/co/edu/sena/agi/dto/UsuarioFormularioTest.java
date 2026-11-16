package co.edu.sena.agi.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de validación del formulario de usuario (Bean Validation).
 * Cubren textos, números, fechas, caracteres especiales y longitudes, tal como
 * lo pide la evidencia GA7-220501096-AA3-EV02.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("Validaciones del formulario de usuario")
class UsuarioFormularioTest {

    private static ValidatorFactory fabricaValidadores;
    private static Validator validador;

    private UsuarioFormulario formulario;

    @BeforeAll
    static void crearValidador() {
        fabricaValidadores = Validation.buildDefaultValidatorFactory();
        validador = fabricaValidadores.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        fabricaValidadores.close();
    }

    /** Antes de cada prueba se arma un formulario correcto; cada prueba daña un solo campo. */
    @BeforeEach
    void crearFormularioValido() {
        formulario = new UsuarioFormulario();
        formulario.setNombres("María José");
        formulario.setApellidos("Gómez Rincón");
        formulario.setCedula("1012345678");
        formulario.setCorreo("maria.gomez@correo.com");
        formulario.setContrasena("Clave2026");
        formulario.setConfirmarContrasena("Clave2026");
        formulario.setFechaNacimiento(LocalDate.of(1958, 3, 14));
    }

    /** Devuelve los nombres de los campos que tienen errores. */
    private Set<String> camposConError() {
        Set<ConstraintViolation<UsuarioFormulario>> errores = validador.validate(formulario);
        return errores.stream()
                .map(error -> error.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    @Test
    @DisplayName("Un formulario con datos correctos no tiene errores")
    void formularioValidoNoTieneErrores() {
        assertTrue(camposConError().isEmpty());
    }

    @Test
    @DisplayName("Acepta tildes, eñe, apóstrofo y guion en nombres y apellidos")
    void aceptaCaracteresPropiosDelEspanol() {
        formulario.setNombres("Luis Ángel Muñoz");
        formulario.setApellidos("O'Connor-Díaz");
        assertTrue(camposConError().isEmpty());
    }

    @Test
    @DisplayName("Rechaza números y símbolos en los nombres")
    void rechazaNumerosYSimbolosEnNombres() {
        formulario.setNombres("Ana123");
        formulario.setApellidos("Pérez#@");
        Set<String> campos = camposConError();
        assertTrue(campos.contains("nombres"));
        assertTrue(campos.contains("apellidos"));
    }

    @Test
    @DisplayName("Rechaza nombres vacíos o con solo espacios")
    void rechazaNombresVacios() {
        formulario.setNombres("   ");
        assertTrue(camposConError().contains("nombres"));
    }

    @Test
    @DisplayName("Rechaza nombres de más de 60 caracteres")
    void rechazaNombresDemasiadoLargos() {
        formulario.setNombres("A".repeat(61));
        assertTrue(camposConError().contains("nombres"));
    }

    @Test
    @DisplayName("Acepta nombres de exactamente 60 caracteres (límite)")
    void aceptaNombresEnElLimite() {
        formulario.setNombres("A".repeat(60));
        assertTrue(camposConError().isEmpty());
    }

    @Test
    @DisplayName("Rechaza cédulas con letras")
    void rechazaCedulaConLetras() {
        formulario.setCedula("12AB34");
        assertTrue(camposConError().contains("cedula"));
    }

    @Test
    @DisplayName("Rechaza cédulas de menos de 6 o más de 15 dígitos")
    void rechazaCedulaFueraDeRango() {
        formulario.setCedula("12345");
        assertTrue(camposConError().contains("cedula"));

        formulario.setCedula("1234567890123456");
        assertTrue(camposConError().contains("cedula"));
    }

    @Test
    @DisplayName("Acepta cédulas de 6 y de 15 dígitos (límites)")
    void aceptaCedulaEnLosLimites() {
        formulario.setCedula("123456");
        assertTrue(camposConError().isEmpty());

        formulario.setCedula("123456789012345");
        assertTrue(camposConError().isEmpty());
    }

    @Test
    @DisplayName("Rechaza correos sin @ o sin dominio")
    void rechazaCorreoInvalido() {
        formulario.setCorreo("ana.correo.com");
        assertTrue(camposConError().contains("correo"));

        formulario.setCorreo("ana@correo");
        assertTrue(camposConError().contains("correo"));
    }

    @Test
    @DisplayName("Rechaza fechas de nacimiento futuras o de hoy")
    void rechazaFechaFutura() {
        formulario.setFechaNacimiento(LocalDate.now().plusDays(1));
        assertTrue(camposConError().contains("fechaNacimiento"));

        formulario.setFechaNacimiento(LocalDate.now());
        assertTrue(camposConError().contains("fechaNacimiento"));
    }

    @Test
    @DisplayName("Exige la fecha de nacimiento")
    void exigeFechaNacimiento() {
        formulario.setFechaNacimiento(null);
        assertTrue(camposConError().contains("fechaNacimiento"));
    }

    @Test
    @DisplayName("Rechaza contraseñas de menos de 6 o más de 50 caracteres")
    void rechazaContrasenaFueraDeRango() {
        formulario.setContrasena("abc12");
        assertTrue(camposConError().contains("contrasena"));

        formulario.setContrasena("x".repeat(51));
        assertTrue(camposConError().contains("contrasena"));
    }

    @Test
    @DisplayName("El mensaje de error de la cédula es claro para la persona")
    void mensajeDeCedulaEsClaro() {
        formulario.setCedula("abc");
        String mensaje = validador.validateProperty(formulario, "cedula").iterator().next().getMessage();
        assertEquals("La cédula debe tener solo números, entre 6 y 15 dígitos.", mensaje);
    }
}
