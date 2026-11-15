package co.edu.sena.agi.controlador;

import co.edu.sena.agi.config.SesionInterceptor;
import co.edu.sena.agi.dto.UsuarioFormulario;
import co.edu.sena.agi.dto.UsuarioSesion;
import co.edu.sena.agi.excepcion.DatoDuplicadoException;
import co.edu.sena.agi.modelo.Usuario;
import co.edu.sena.agi.servicio.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web del módulo de gestión de usuarios (CRUD).
 * <ul>
 *   <li>GET  /usuarios                 : listado y búsqueda por id (?id=N).</li>
 *   <li>GET  /usuarios/nuevo           : formulario de registro (administración).</li>
 *   <li>POST /usuarios                 : registra el usuario.</li>
 *   <li>GET  /usuarios/{id}/editar     : formulario de edición.</li>
 *   <li>POST /usuarios/{id}            : actualiza el usuario.</li>
 *   <li>POST /usuarios/{id}/eliminar   : elimina el usuario.</li>
 *   <li>GET y POST /registro           : creación pública de cuenta (sin sesión).</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Controller
public class UsuarioController {

    private static final String VISTA_LISTA = "usuarios/lista";
    private static final String VISTA_FORMULARIO = "usuarios/formulario";

    /** Modos del formulario: registro público, registro por administración y edición. */
    private static final String MODO_REGISTRO = "registro";
    private static final String MODO_NUEVO = "nuevo";
    private static final String MODO_EDICION = "edicion";

    private static final LocalDate FECHA_MINIMA_NACIMIENTO = LocalDate.of(1900, 1, 1);

    private final UsuarioService usuarioService;

    /**
     * @param usuarioService servicio con las reglas de negocio de usuarios
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // =================================================================
    // Consultas (GET): no modifican la base de datos
    // =================================================================

    /** Lista todos los usuarios o, si llega el parámetro id, solo el buscado. */
    @GetMapping("/usuarios")
    public String listarUsuarios(@RequestParam(name = "id", required = false) Integer idUsuario, Model model) {
        if (idUsuario == null) {
            model.addAttribute("usuarios", usuarioService.listarUsuarios());
            return VISTA_LISTA;
        }

        Optional<Usuario> usuarioEncontrado = usuarioService.buscarUsuarioOpcional(idUsuario);
        model.addAttribute("usuarios", usuarioEncontrado.stream().toList());
        model.addAttribute("busqueda", idUsuario);
        if (usuarioEncontrado.isEmpty()) {
            model.addAttribute("mensajeError", "No existe un usuario con el id " + idUsuario + ".");
        }
        return VISTA_LISTA;
    }

    /** Formulario vacío para que la administración registre un usuario. */
    @GetMapping("/usuarios/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("usuarioFormulario", new UsuarioFormulario());
        prepararFormulario(model, MODO_NUEVO, null);
        return VISTA_FORMULARIO;
    }

    /** Formulario público para crear una cuenta. */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuarioFormulario", new UsuarioFormulario());
        prepararFormulario(model, MODO_REGISTRO, null);
        return VISTA_FORMULARIO;
    }

    /** Formulario de edición con los datos actuales del usuario. */
    @GetMapping("/usuarios/{idUsuario}/editar")
    public String mostrarFormularioEdicion(@PathVariable("idUsuario") Integer idUsuario, Model model) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(idUsuario);
        model.addAttribute("usuarioFormulario", UsuarioFormulario.desdeUsuario(usuario));
        prepararFormulario(model, MODO_EDICION, idUsuario);
        return VISTA_FORMULARIO;
    }

    // =================================================================
    // Cambios (POST): insertan, actualizan o eliminan registros
    // =================================================================

    /** Registra un usuario desde la administración. */
    @PostMapping("/usuarios")
    public String registrarUsuario(@Valid @ModelAttribute("usuarioFormulario") UsuarioFormulario formulario,
                                   BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (!guardarUsuarioNuevo(formulario, resultado)) {
            prepararFormulario(model, MODO_NUEVO, null);
            return VISTA_FORMULARIO;
        }
        redirect.addFlashAttribute("mensajeExito", "El usuario quedó registrado correctamente.");
        return "redirect:/usuarios";
    }

    /** Crea la cuenta desde el formulario público y lleva al inicio de sesión. */
    @PostMapping("/registro")
    public String registrarCuenta(@Valid @ModelAttribute("usuarioFormulario") UsuarioFormulario formulario,
                                  BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (!guardarUsuarioNuevo(formulario, resultado)) {
            prepararFormulario(model, MODO_REGISTRO, null);
            return VISTA_FORMULARIO;
        }
        redirect.addFlashAttribute("mensajeExito", "Su cuenta quedó registrada. Ya puede iniciar sesión.");
        return "redirect:/login";
    }

    /** Actualiza un usuario; si es la persona conectada, también actualiza el saludo. */
    @PostMapping("/usuarios/{idUsuario}")
    public String actualizarUsuario(@PathVariable("idUsuario") Integer idUsuario,
                                    @Valid @ModelAttribute("usuarioFormulario") UsuarioFormulario formulario,
                                    BindingResult resultado, Model model,
                                    HttpServletRequest request, RedirectAttributes redirect) {
        formulario.setIdUsuario(idUsuario);
        validarReglasAdicionales(formulario, resultado, false);
        if (!resultado.hasErrors()) {
            try {
                Usuario usuario = usuarioService.actualizarUsuario(idUsuario, formulario);
                actualizarSesionSiEsPropia(request, usuario);
                redirect.addFlashAttribute("mensajeExito", "Los datos del usuario se actualizaron correctamente.");
                return "redirect:/usuarios";
            } catch (DatoDuplicadoException excepcion) {
                resultado.rejectValue(excepcion.getCampo(), "duplicado", excepcion.getMessage());
            }
        }
        prepararFormulario(model, MODO_EDICION, idUsuario);
        return VISTA_FORMULARIO;
    }

    /** Elimina un usuario. Si la persona elimina su propia cuenta, se cierra la sesión. */
    @PostMapping("/usuarios/{idUsuario}/eliminar")
    public String eliminarUsuario(@PathVariable("idUsuario") Integer idUsuario,
                                  HttpServletRequest request, RedirectAttributes redirect) {
        usuarioService.eliminarUsuario(idUsuario);

        HttpSession sesion = request.getSession(false);
        UsuarioSesion usuarioSesion = sesion == null ? null
                : (UsuarioSesion) sesion.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO_SESION);
        if (usuarioSesion != null && idUsuario.equals(usuarioSesion.getIdUsuario())) {
            sesion.invalidate();
            return "redirect:/login?cuentaEliminada";
        }
        redirect.addFlashAttribute("mensajeExito", "El usuario fue eliminado.");
        return "redirect:/usuarios";
    }

    // =================================================================
    // Métodos de apoyo
    // =================================================================

    /**
     * Valida y guarda un usuario nuevo.
     *
     * @return true si quedó guardado; false si hay errores para mostrar en el formulario
     */
    private boolean guardarUsuarioNuevo(UsuarioFormulario formulario, BindingResult resultado) {
        validarReglasAdicionales(formulario, resultado, true);
        if (resultado.hasErrors()) {
            return false;
        }
        try {
            usuarioService.registrarUsuario(formulario);
            return true;
        } catch (DatoDuplicadoException excepcion) {
            resultado.rejectValue(excepcion.getCampo(), "duplicado", excepcion.getMessage());
            return false;
        }
    }

    /** Reglas que dependen de varios campos o del modo del formulario. */
    private void validarReglasAdicionales(UsuarioFormulario formulario, BindingResult resultado,
                                          boolean esRegistro) {
        if (esRegistro && !formulario.tieneContrasena()) {
            resultado.rejectValue("contrasena", "requerida", "Escriba una contraseña.");
        }
        if (formulario.tieneContrasena() && !formulario.getContrasena().equals(formulario.getConfirmarContrasena())) {
            resultado.rejectValue("confirmarContrasena", "diferente", "Las contraseñas no coinciden.");
        }
        LocalDate fechaNacimiento = formulario.getFechaNacimiento();
        if (fechaNacimiento != null && fechaNacimiento.isBefore(FECHA_MINIMA_NACIMIENTO)) {
            resultado.rejectValue("fechaNacimiento", "minima", "La fecha de nacimiento no puede ser anterior a 1900.");
        }
    }

    /** Agrega a la vista el modo del formulario, su título, la URL a la que se envía y la fecha máxima. */
    private void prepararFormulario(Model model, String modo, Integer idUsuario) {
        String accionFormulario = switch (modo) {
            case MODO_REGISTRO -> "/registro";
            case MODO_EDICION -> "/usuarios/" + idUsuario;
            default -> "/usuarios";
        };
        String tituloPagina = switch (modo) {
            case MODO_REGISTRO -> "Crear cuenta";
            case MODO_EDICION -> "Editar usuario";
            default -> "Nuevo usuario";
        };
        model.addAttribute("modo", modo);
        model.addAttribute("tituloPagina", tituloPagina);
        model.addAttribute("accionFormulario", accionFormulario);
        model.addAttribute("fechaMaxima", LocalDate.now().minusDays(1));
    }

    /** Si la persona editó su propia cuenta, se actualizan los datos guardados en la sesión. */
    private void actualizarSesionSiEsPropia(HttpServletRequest request, Usuario usuario) {
        HttpSession sesion = request.getSession(false);
        if (sesion == null) {
            return;
        }
        UsuarioSesion usuarioSesion = (UsuarioSesion) sesion.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO_SESION);
        if (usuarioSesion != null && usuario.getIdUsuario().equals(usuarioSesion.getIdUsuario())) {
            sesion.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO_SESION, UsuarioSesion.desdeUsuario(usuario));
        }
    }
}
