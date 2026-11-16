package co.edu.sena.agi.controlador;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import co.edu.sena.agi.config.SesionInterceptor;
import co.edu.sena.agi.dto.UsuarioSesion;
import co.edu.sena.agi.modelo.Usuario;
import co.edu.sena.agi.servicio.CategoriaService;
import co.edu.sena.agi.servicio.GuiaService;
import co.edu.sena.agi.servicio.PasoService;
import co.edu.sena.agi.servicio.UsuarioService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Pruebas del inicio de sesión y de la protección de las rutas de administración.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("Inicio de sesión y rutas protegidas")
class InicioControllerTest {

    private UsuarioService usuarioService;
    private MockMvc mockMvc;

    @BeforeEach
    void prepararMockMvc() {
        usuarioService = mock(UsuarioService.class);
        InicioController controlador = new InicioController(usuarioService, mock(GuiaService.class),
                mock(PasoService.class), mock(CategoriaService.class));

        mockMvc = MockMvcBuilders.standaloneSetup(controlador)
                .setControllerAdvice(new DatosComunesAdvice())
                .addMappedInterceptors(new String[] {"/panel"}, new SesionInterceptor())
                .setViewResolvers(VistaVacia.crearResolutor())
                .build();
    }

    @Test
    @DisplayName("Sin correo ni contraseña muestra el aviso y no consulta la base de datos")
    void loginVacioMuestraAviso() throws Exception {
        mockMvc.perform(post("/login").param("correo", "").param("contrasena", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("mensajeError", "Escriba su correo y su contraseña."));
    }

    @Test
    @DisplayName("Con credenciales incorrectas muestra el error y conserva el correo escrito")
    void loginIncorrectoMuestraError() throws Exception {
        when(usuarioService.autenticarUsuario("admin@agi.com", "ClaveErrada1")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login").param("correo", "admin@agi.com").param("contrasena", "ClaveErrada1"))
                .andExpect(view().name("login"))
                .andExpect(model().attribute("mensajeError", "El correo o la contraseña no son correctos."))
                .andExpect(model().attribute("correoEscrito", "admin@agi.com"));
    }

    @Test
    @DisplayName("Con credenciales correctas guarda al usuario en la sesión y lleva al panel")
    void loginCorrectoCreaLaSesion() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3);
        usuario.setNombres("María José");
        usuario.setCorreo("maria.gomez@correo.com");
        when(usuarioService.autenticarUsuario("maria.gomez@correo.com", "Clave2026"))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(post("/login").param("correo", "maria.gomez@correo.com").param("contrasena", "Clave2026"))
                .andExpect(redirectedUrl("/panel"))
                .andExpect(request().sessionAttribute(SesionInterceptor.ATRIBUTO_USUARIO_SESION,
                        notNullValue()));
    }

    @Test
    @DisplayName("El panel no se puede abrir sin haber iniciado sesión")
    void panelSinSesionRedirigeAlLogin() throws Exception {
        mockMvc.perform(get("/panel"))
                .andExpect(redirectedUrl("/login?sesion"));
    }

    @Test
    @DisplayName("El panel se abre cuando hay una sesión iniciada")
    void panelConSesionSeMuestra() throws Exception {
        UsuarioSesion usuarioSesion = new UsuarioSesion(3, "María José", "maria.gomez@correo.com");

        mockMvc.perform(get("/panel").sessionAttr(SesionInterceptor.ATRIBUTO_USUARIO_SESION, usuarioSesion))
                .andExpect(status().isOk())
                .andExpect(view().name("panel"));
    }
}
