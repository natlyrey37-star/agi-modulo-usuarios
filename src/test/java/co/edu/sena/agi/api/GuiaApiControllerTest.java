package co.edu.sena.agi.api;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.edu.sena.agi.dto.GuiaDetalleDto;
import co.edu.sena.agi.dto.GuiaResumenDto;
import co.edu.sena.agi.dto.PasoDto;
import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import co.edu.sena.agi.servicio.GuiaService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Pruebas de la API REST de guías que consume la aplicación móvil.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("API REST de guías")
class GuiaApiControllerTest {

    private GuiaService guiaService;
    private MockMvc mockMvc;

    @BeforeEach
    void prepararMockMvc() {
        guiaService = mock(GuiaService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new GuiaApiController(guiaService))
                .setControllerAdvice(new ManejadorErroresApi())
                .build();
    }

    @Test
    @DisplayName("GET /api/guias?categoria=1 devuelve solo las guías de esa categoría")
    void listarGuiasPorCategoria() throws Exception {
        when(guiaService.listarResumenGuias(1)).thenReturn(List.of(
                new GuiaResumenDto(1, "Hacer una llamada", "Aprende a llamar.", "comunicación", 4),
                new GuiaResumenDto(4, "Enviar un mensaje por WhatsApp", "Aprende a escribir.", "comunicación", 3)));

        mockMvc.perform(get("/api/guias").param("categoria", "1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].titulo").value("Enviar un mensaje por WhatsApp"))
                .andExpect(jsonPath("$[1].totalPasos").value(3));
    }

    @Test
    @DisplayName("GET /api/guias/4 devuelve la guía con sus pasos en orden")
    void obtenerGuiaConPasos() throws Exception {
        when(guiaService.obtenerDetalleGuia(4)).thenReturn(new GuiaDetalleDto(4, "Enviar un mensaje por WhatsApp",
                "Aprende a escribir.", "comunicación", List.of(
                        new PasoDto(1, "Abra WhatsApp", "Toque el ícono verde."),
                        new PasoDto(2, "Elija el contacto", "Toque el nombre de la persona."))));

        mockMvc.perform(get("/api/guias/4").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idGuia").value(4))
                .andExpect(jsonPath("$.pasos", hasSize(2)))
                .andExpect(jsonPath("$.pasos[0].numeroPaso").value(1))
                .andExpect(jsonPath("$.pasos[1].tituloPaso").value("Elija el contacto"));
    }

    @Test
    @DisplayName("GET /api/guias/999 responde 404 con el detalle del error en JSON")
    void guiaInexistenteResponde404() throws Exception {
        when(guiaService.obtenerDetalleGuia(999))
                .thenThrow(new RecursoNoEncontradoException("No existe una guía con el id 999."));

        mockMvc.perform(get("/api/guias/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Recurso no encontrado"))
                .andExpect(jsonPath("$.detail").value("No existe una guía con el id 999."));
    }

    @Test
    @DisplayName("GET /api/guias/abc responde 400 porque el id debe ser un número")
    void idNoNumericoResponde400() throws Exception {
        mockMvc.perform(get("/api/guias/abc").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("El parámetro 'idGuia' debe ser un número entero."));
    }
}
