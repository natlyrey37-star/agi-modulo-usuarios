package co.edu.sena.agi.controlador;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import co.edu.sena.agi.dto.UsuarioFormulario;
import co.edu.sena.agi.excepcion.DatoDuplicadoException;
import co.edu.sena.agi.servicio.UsuarioService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Pruebas del formulario público de registro (POST /registro) con MockMvc.
 * Se envían los datos como lo haría el navegador y se revisa la respuesta del controlador.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("Formulario de registro de usuarios (controlador web)")
class UsuarioControllerTest {

    private static final String VISTA_FORMULARIO = "usuarios/formulario";
    private static final String FORMULARIO = "usuarioFormulario";

    private UsuarioService usuarioService;
    private MockMvc mockMvc;

    @BeforeEach
    void prepararMockMvc() {
        usuarioService = mock(UsuarioService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UsuarioController(usuarioService))
                .setControllerAdvice(new DatosComunesAdvice())
                .setViewResolvers(VistaVacia.crearResolutor())
                .build();
    }

    /**
     * Petición POST /registro con todos los campos correctos, salvo los que se cambien.
     *
     * @param cambios pares campo, valor que reemplazan los datos correctos
     */
    private MockHttpServletRequestBuilder registro(String... cambios) {
        Map<String, String> datos = new LinkedHashMap<>();
        datos.put("nombres", "María José");
        datos.put("apellidos", "Gómez Rincón");
        datos.put("cedula", "1012345678");
        datos.put("fechaNacimiento", "1958-03-14");
        datos.put("correo", "maria.gomez@correo.com");
        datos.put("contrasena", "Clave2026");
        datos.put("confirmarContrasena", "Clave2026");
        for (int i = 0; i + 1 < cambios.length; i += 2) {
            datos.put(cambios[i], cambios[i + 1]);
        }
        MockHttpServletRequestBuilder peticion = post("/registro");
        for (Map.Entry<String, String> dato : datos.entrySet()) {
            peticion.param(dato.getKey(), dato.getValue());
        }
        return peticion;
    }

    @Test
    @DisplayName("Con datos correctos registra la cuenta y lleva al inicio de sesión")
    void registroValidoRedirigeAlLogin() throws Exception {
        mockMvc.perform(registro())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("mensajeExito"));

        verify(usuarioService).registrarUsuario(any(UsuarioFormulario.class));
    }

    @Test
    @DisplayName("Con el formulario vacío marca todos los campos obligatorios")
    void registroVacioMuestraErrores() throws Exception {
        mockMvc.perform(registro("nombres", "", "apellidos", "   ", "cedula", "", "fechaNacimiento", "",
                        "correo", "", "contrasena", "", "confirmarContrasena", ""))
                .andExpect(status().isOk())
                .andExpect(view().name(VISTA_FORMULARIO))
                .andExpect(model().attributeHasFieldErrors(FORMULARIO,
                        "nombres", "apellidos", "cedula", "fechaNacimiento", "correo", "contrasena"));

        verify(usuarioService, never()).registrarUsuario(any(UsuarioFormulario.class));
    }

    @Test
    @DisplayName("Rechaza contraseñas que no coinciden")
    void registroConContrasenasDistintas() throws Exception {
        mockMvc.perform(registro("confirmarContrasena", "Otra2026"))
                .andExpect(view().name(VISTA_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode(FORMULARIO, "confirmarContrasena", "diferente"));
    }

    @Test
    @DisplayName("Rechaza fechas de nacimiento anteriores a 1900")
    void registroConFechaAnteriorA1900() throws Exception {
        mockMvc.perform(registro("fechaNacimiento", "1899-12-31"))
                .andExpect(view().name(VISTA_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode(FORMULARIO, "fechaNacimiento", "minima"));
    }

    @Test
    @DisplayName("Rechaza una fecha escrita con un formato que no es fecha")
    void registroConFechaInvalida() throws Exception {
        mockMvc.perform(registro("fechaNacimiento", "31/02/abc"))
                .andExpect(view().name(VISTA_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode(FORMULARIO, "fechaNacimiento", "typeMismatch"));
    }

    @Test
    @DisplayName("Muestra el error junto a la cédula cuando ya está registrada")
    void registroConCedulaRepetida() throws Exception {
        when(usuarioService.registrarUsuario(any(UsuarioFormulario.class)))
                .thenThrow(new DatoDuplicadoException("cedula", "Ya existe un usuario registrado con esa cédula."));

        mockMvc.perform(registro())
                .andExpect(status().isOk())
                .andExpect(view().name(VISTA_FORMULARIO))
                .andExpect(model().attributeHasFieldErrorCode(FORMULARIO, "cedula", "duplicado"));
    }
}
