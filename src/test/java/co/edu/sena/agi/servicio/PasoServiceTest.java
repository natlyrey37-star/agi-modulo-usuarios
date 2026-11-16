package co.edu.sena.agi.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.sena.agi.dto.PasoFormulario;
import co.edu.sena.agi.excepcion.DatoDuplicadoException;
import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import co.edu.sena.agi.modelo.Guia;
import co.edu.sena.agi.modelo.Paso;
import co.edu.sena.agi.repositorio.PasoRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de negocio de los pasos de una guía.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("Reglas de negocio de los pasos")
class PasoServiceTest {

    private static final int ID_GUIA = 4;

    private PasoRepository pasoRepository;
    private GuiaService guiaService;
    private PasoService pasoService;
    private Guia guia;

    @BeforeEach
    void prepararServicio() {
        pasoRepository = mock(PasoRepository.class);
        guiaService = mock(GuiaService.class);
        pasoService = new PasoService(pasoRepository, guiaService);

        guia = new Guia();
        guia.setIdGuia(ID_GUIA);
        guia.setTitulo("Enviar un mensaje por WhatsApp");
        when(guiaService.buscarGuiaPorId(ID_GUIA)).thenReturn(guia);
        when(pasoRepository.save(any(Paso.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    private PasoFormulario crearFormulario(int numeroPaso) {
        PasoFormulario formulario = new PasoFormulario();
        formulario.setNumeroPaso(numeroPaso);
        formulario.setTituloPaso("  Abra WhatsApp ");
        formulario.setInstruccion("Toque el ícono verde una vez.");
        return formulario;
    }

    private Paso crearPaso(int numeroPaso) {
        Paso paso = new Paso();
        paso.setNumeroPaso(numeroPaso);
        paso.setTituloPaso("Paso " + numeroPaso);
        paso.setInstruccion("Instrucción " + numeroPaso);
        return paso;
    }

    @Test
    @DisplayName("Agregar: el paso queda unido a su guía y sin espacios sobrantes")
    void registrarPasoLoAgregaALaGuia() {
        Paso paso = pasoService.registrarPaso(ID_GUIA, crearFormulario(1));

        assertSame(guia, paso.getGuia());
        assertTrue(guia.getPasos().contains(paso));
        assertEquals("Abra WhatsApp", paso.getTituloPaso());
    }

    @Test
    @DisplayName("Agregar: no permite repetir el número de paso dentro de la misma guía")
    void registrarPasoConNumeroRepetidoLanzaExcepcion() {
        when(pasoRepository.existsByGuiaIdGuiaAndNumeroPaso(ID_GUIA, 2)).thenReturn(true);

        DatoDuplicadoException excepcion = assertThrows(DatoDuplicadoException.class,
                () -> pasoService.registrarPaso(ID_GUIA, crearFormulario(2)));

        assertEquals("numeroPaso", excepcion.getCampo());
        assertEquals("Esta guía ya tiene un paso con el número 2.", excepcion.getMessage());
        verify(pasoRepository, never()).save(any(Paso.class));
    }

    @Test
    @DisplayName("Editar: no permite cambiar el número por uno que ya usa otro paso")
    void actualizarPasoConNumeroDeOtroPasoLanzaExcepcion() {
        Paso paso = crearPaso(3);
        paso.setIdPaso(15);
        guia.agregarPaso(paso);
        when(pasoRepository.findByIdPasoAndGuiaIdGuia(15, ID_GUIA)).thenReturn(Optional.of(paso));
        when(pasoRepository.existsByGuiaIdGuiaAndNumeroPasoAndIdPasoNot(ID_GUIA, 1, 15)).thenReturn(true);

        assertThrows(DatoDuplicadoException.class,
                () -> pasoService.actualizarPaso(ID_GUIA, 15, crearFormulario(1)));
        assertEquals(3, paso.getNumeroPaso());
    }

    @Test
    @DisplayName("Buscar: un paso que no pertenece a la guía no se encuentra")
    void buscarPasoDeOtraGuiaLanzaExcepcion() {
        when(pasoRepository.findByIdPasoAndGuiaIdGuia(1, ID_GUIA)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> pasoService.buscarPaso(ID_GUIA, 1));
    }

    @Test
    @DisplayName("Sugerencia: propone el número siguiente al mayor de la guía")
    void sugerirNumeroPasoDevuelveElSiguiente() {
        assertEquals(1, pasoService.sugerirNumeroPaso(guia));

        guia.agregarPaso(crearPaso(1));
        guia.agregarPaso(crearPaso(2));
        guia.agregarPaso(crearPaso(5));
        assertEquals(6, pasoService.sugerirNumeroPaso(guia));
    }
}
