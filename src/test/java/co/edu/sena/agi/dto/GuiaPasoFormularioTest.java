package co.edu.sena.agi.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de validación de los formularios de guía y de paso.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("Validaciones de los formularios de guía y paso")
class GuiaPasoFormularioTest {

    private static ValidatorFactory fabricaValidadores;
    private static Validator validador;

    @BeforeAll
    static void crearValidador() {
        fabricaValidadores = Validation.buildDefaultValidatorFactory();
        validador = fabricaValidadores.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        fabricaValidadores.close();
    }

    /** Devuelve los nombres de los campos con error de cualquier objeto validado. */
    private <T> Set<String> camposConError(T objeto) {
        return validador.validate(objeto).stream()
                .map(error -> error.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    private GuiaFormulario crearGuiaValida() {
        GuiaFormulario guia = new GuiaFormulario();
        guia.setTitulo("Enviar un mensaje por WhatsApp");
        guia.setDescripcion("Aprende a escribir y enviar un mensaje de texto a un contacto.");
        guia.setIdCategoria(1);
        return guia;
    }

    private PasoFormulario crearPasoValido() {
        PasoFormulario paso = new PasoFormulario();
        paso.setNumeroPaso(1);
        paso.setTituloPaso("Abra WhatsApp");
        paso.setInstruccion("Toque el ícono verde una vez.");
        return paso;
    }

    @Test
    @DisplayName("Una guía con datos correctos no tiene errores")
    void guiaValidaNoTieneErrores() {
        assertTrue(camposConError(crearGuiaValida()).isEmpty());
    }

    @Test
    @DisplayName("Acepta caracteres especiales en el título de la guía")
    void guiaAceptaCaracteresEspeciales() {
        GuiaFormulario guia = crearGuiaValida();
        guia.setTitulo("¿Cómo usar el GPS? (Mapas) & más: ñ, ü, €");
        assertTrue(camposConError(guia).isEmpty());
    }

    @Test
    @DisplayName("Exige el título y la categoría de la guía")
    void guiaExigeTituloYCategoria() {
        GuiaFormulario guia = crearGuiaValida();
        guia.setTitulo("");
        guia.setIdCategoria(null);
        Set<String> campos = camposConError(guia);
        assertTrue(campos.contains("titulo"));
        assertTrue(campos.contains("idCategoria"));
    }

    @Test
    @DisplayName("Rechaza títulos de más de 100 y descripciones de más de 255 caracteres")
    void guiaRechazaTextosDemasiadoLargos() {
        GuiaFormulario guia = crearGuiaValida();
        guia.setTitulo("T".repeat(101));
        guia.setDescripcion("D".repeat(256));
        Set<String> campos = camposConError(guia);
        assertTrue(campos.contains("titulo"));
        assertTrue(campos.contains("descripcion"));
    }

    @Test
    @DisplayName("La descripción de la guía es opcional")
    void guiaPermiteDescripcionVacia() {
        GuiaFormulario guia = crearGuiaValida();
        guia.setDescripcion(null);
        assertTrue(camposConError(guia).isEmpty());
    }

    @Test
    @DisplayName("Un paso con datos correctos no tiene errores")
    void pasoValidoNoTieneErrores() {
        assertTrue(camposConError(crearPasoValido()).isEmpty());
    }

    @Test
    @DisplayName("Rechaza números de paso menores que 1 o mayores que 50")
    void pasoRechazaNumeroFueraDeRango() {
        PasoFormulario paso = crearPasoValido();
        paso.setNumeroPaso(0);
        assertTrue(camposConError(paso).contains("numeroPaso"));

        paso.setNumeroPaso(-3);
        assertTrue(camposConError(paso).contains("numeroPaso"));

        paso.setNumeroPaso(51);
        assertTrue(camposConError(paso).contains("numeroPaso"));
    }

    @Test
    @DisplayName("Acepta los números de paso 1 y 50 (límites)")
    void pasoAceptaNumeroEnLosLimites() {
        PasoFormulario paso = crearPasoValido();
        paso.setNumeroPaso(1);
        assertTrue(camposConError(paso).isEmpty());

        paso.setNumeroPaso(PasoFormulario.NUMERO_MAXIMO_PASO);
        assertTrue(camposConError(paso).isEmpty());
    }

    @Test
    @DisplayName("Exige el número, el título y la instrucción del paso")
    void pasoExigeTodosLosCampos() {
        PasoFormulario paso = new PasoFormulario();
        Set<String> campos = camposConError(paso);
        assertTrue(campos.contains("numeroPaso"));
        assertTrue(campos.contains("tituloPaso"));
        assertTrue(campos.contains("instruccion"));
    }

    @Test
    @DisplayName("Rechaza títulos de paso de más de 100 e instrucciones de más de 255 caracteres")
    void pasoRechazaTextosDemasiadoLargos() {
        PasoFormulario paso = crearPasoValido();
        paso.setTituloPaso("T".repeat(101));
        paso.setInstruccion("I".repeat(256));
        Set<String> campos = camposConError(paso);
        assertTrue(campos.contains("tituloPaso"));
        assertTrue(campos.contains("instruccion"));
    }
}
