package co.edu.sena.agi.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.edu.sena.agi.dto.UsuarioFormulario;
import co.edu.sena.agi.excepcion.DatoDuplicadoException;
import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import co.edu.sena.agi.modelo.Usuario;
import co.edu.sena.agi.repositorio.UsuarioRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las reglas de negocio del módulo de usuarios.
 * El repositorio se reemplaza por un objeto simulado (Mockito), así las pruebas
 * no dependen de que MySQL esté encendido.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.1
 */
@DisplayName("Reglas de negocio de usuarios")
class UsuarioServiceTest {

    private static final String CORREO = "maria.gomez@correo.com";
    private static final String CEDULA = "1012345678";
    private static final String CONTRASENA = "Clave2026";

    private UsuarioRepository usuarioRepository;
    private ContrasenaService contrasenaService;
    private UsuarioService usuarioService;

    @BeforeEach
    void prepararServicio() {
        usuarioRepository = mock(UsuarioRepository.class);
        contrasenaService = new ContrasenaService();
        usuarioService = new UsuarioService(usuarioRepository, contrasenaService);
        // save() devuelve el mismo objeto que recibe, como lo hace JPA
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    private UsuarioFormulario crearFormulario() {
        UsuarioFormulario formulario = new UsuarioFormulario();
        formulario.setNombres("  María José ");
        formulario.setApellidos("Gómez Rincón");
        formulario.setCedula(CEDULA);
        formulario.setCorreo("  Maria.Gomez@CORREO.com ");
        formulario.setContrasena(CONTRASENA);
        formulario.setConfirmarContrasena(CONTRASENA);
        formulario.setFechaNacimiento(LocalDate.of(1958, 3, 14));
        return formulario;
    }

    private Usuario crearUsuarioGuardado(String hashContrasena) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3);
        usuario.setNombres("María José");
        usuario.setApellidos("Gómez Rincón");
        usuario.setCedula(CEDULA);
        usuario.setCorreo(CORREO);
        usuario.setContrasena(hashContrasena);
        usuario.setFechaNacimiento(LocalDate.of(1958, 3, 14));
        return usuario;
    }

    @Test
    @DisplayName("Registrar: limpia los espacios, pasa el correo a minúsculas y cifra la contraseña")
    void registrarUsuarioNormalizaDatosYCifraContrasena() {
        Usuario guardado = usuarioService.registrarUsuario(crearFormulario());

        assertEquals("María José", guardado.getNombres());
        assertEquals(CORREO, guardado.getCorreo());
        assertNotEquals(CONTRASENA, guardado.getContrasena());
        assertTrue(contrasenaService.verificarContrasena(CONTRASENA, guardado.getContrasena()));
        verify(usuarioRepository).save(guardado);
    }

    @Test
    @DisplayName("Registrar: no permite una cédula repetida")
    void registrarUsuarioConCedulaRepetidaLanzaExcepcion() {
        when(usuarioRepository.existsByCedula(CEDULA)).thenReturn(true);

        DatoDuplicadoException excepcion = assertThrows(DatoDuplicadoException.class,
                () -> usuarioService.registrarUsuario(crearFormulario()));

        assertEquals("cedula", excepcion.getCampo());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Registrar: no permite un correo repetido, aunque cambien las mayúsculas")
    void registrarUsuarioConCorreoRepetidoLanzaExcepcion() {
        when(usuarioRepository.existsByCorreo(CORREO)).thenReturn(true);

        DatoDuplicadoException excepcion = assertThrows(DatoDuplicadoException.class,
                () -> usuarioService.registrarUsuario(crearFormulario()));

        assertEquals("correo", excepcion.getCampo());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Iniciar sesión: funciona con el correo en mayúsculas y con espacios")
    void autenticarIgnoraMayusculasDelCorreo() {
        Usuario usuario = crearUsuarioGuardado(contrasenaService.cifrarContrasena(CONTRASENA));
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.autenticarUsuario("  MARIA.GOMEZ@CORREO.COM ", CONTRASENA);

        assertTrue(resultado.isPresent());
        assertEquals(3, resultado.get().getIdUsuario());
    }

    @Test
    @DisplayName("Iniciar sesión: rechaza la contraseña incorrecta y el correo inexistente")
    void autenticarRechazaCredencialesIncorrectas() {
        Usuario usuario = crearUsuarioGuardado(contrasenaService.cifrarContrasena(CONTRASENA));
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByCorreo("nadie@correo.com")).thenReturn(Optional.empty());

        assertTrue(usuarioService.autenticarUsuario(CORREO, "ClaveErrada1").isEmpty());
        assertTrue(usuarioService.autenticarUsuario("nadie@correo.com", CONTRASENA).isEmpty());
    }

    @Test
    @DisplayName("Iniciar sesión: una cuenta con hash SHA-256 de la versión 2.0 pasa a BCrypt")
    void autenticarActualizaHashAnterior() {
        String hashSha256 = "cbcf908f64ee2fc641087d35e42f4581584615de96670bdab2bef1a522bbe148";
        Usuario usuario = crearUsuarioGuardado(hashSha256);
        when(usuarioRepository.findByCorreo(CORREO)).thenReturn(Optional.of(usuario));

        assertTrue(usuarioService.autenticarUsuario(CORREO, CONTRASENA).isPresent());

        assertTrue(usuario.getContrasena().startsWith("$2"));
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Actualizar: si la contraseña viene vacía se conserva la actual")
    void actualizarSinContrasenaConservaLaActual() {
        String hashActual = contrasenaService.cifrarContrasena(CONTRASENA);
        Usuario usuario = crearUsuarioGuardado(hashActual);
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));

        UsuarioFormulario formulario = crearFormulario();
        formulario.setApellidos("Gómez Rincón de Pérez");
        formulario.setContrasena(null);
        formulario.setConfirmarContrasena(null);

        Usuario actualizado = usuarioService.actualizarUsuario(3, formulario);

        assertEquals("Gómez Rincón de Pérez", actualizado.getApellidos());
        assertEquals(hashActual, actualizado.getContrasena());
    }

    @Test
    @DisplayName("Actualizar: no permite usar el correo de otra cuenta")
    void actualizarConCorreoDeOtraCuentaLanzaExcepcion() {
        Usuario usuario = crearUsuarioGuardado("hash");
        when(usuarioRepository.findById(3)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.existsByCorreoAndIdUsuarioNot(CORREO, 3)).thenReturn(true);

        DatoDuplicadoException excepcion = assertThrows(DatoDuplicadoException.class,
                () -> usuarioService.actualizarUsuario(3, crearFormulario()));

        assertEquals("correo", excepcion.getCampo());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Eliminar: un id que no existe produce el error de recurso no encontrado")
    void eliminarUsuarioInexistenteLanzaExcepcion() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.eliminarUsuario(999));
        verify(usuarioRepository, never()).delete(any(Usuario.class));
    }
}
