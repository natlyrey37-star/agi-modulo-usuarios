package co.edu.sena.agi.controlador;

import co.edu.sena.agi.config.SesionInterceptor;
import co.edu.sena.agi.dto.UsuarioSesion;
import co.edu.sena.agi.modelo.Usuario;
import co.edu.sena.agi.servicio.CategoriaService;
import co.edu.sena.agi.servicio.GuiaService;
import co.edu.sena.agi.servicio.PasoService;
import co.edu.sena.agi.servicio.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controla el inicio de sesión, el cierre de sesión y el panel principal.
 * <ul>
 *   <li>GET  /login  : formulario de inicio de sesión.</li>
 *   <li>POST /login  : valida correo y contraseña contra la tabla usuario.</li>
 *   <li>POST /salir  : cierra la sesión.</li>
 *   <li>GET  /panel  : resumen del sistema (ruta protegida).</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Controller
public class InicioController {

    private static final String VISTA_LOGIN = "login";

    private final UsuarioService usuarioService;
    private final GuiaService guiaService;
    private final PasoService pasoService;
    private final CategoriaService categoriaService;

    /**
     * @param usuarioService   servicio de usuarios
     * @param guiaService      servicio de guías
     * @param pasoService      servicio de pasos
     * @param categoriaService servicio de categorías
     */
    public InicioController(UsuarioService usuarioService, GuiaService guiaService,
                            PasoService pasoService, CategoriaService categoriaService) {
        this.usuarioService = usuarioService;
        this.guiaService = guiaService;
        this.pasoService = pasoService;
        this.categoriaService = categoriaService;
    }

    /** La raíz de la aplicación lleva al panel si hay sesión, o al inicio de sesión si no. */
    @GetMapping("/")
    public String redirigirInicio(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        boolean haySesion = sesion != null && sesion.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO_SESION) != null;
        return haySesion ? "redirect:/panel" : "redirect:/login";
    }

    /** Muestra el formulario de inicio de sesión. */
    @GetMapping("/login")
    public String mostrarLogin() {
        return VISTA_LOGIN;
    }

    /**
     * Valida las credenciales y, si son correctas, guarda al usuario en la sesión.
     *
     * @param correo     correo escrito en el formulario
     * @param contrasena contraseña escrita en el formulario
     * @param request    petición, para crear la sesión
     * @param model      datos para la vista cuando hay error
     * @param redirect   mensajes que sobreviven a la redirección
     * @return redirección al panel o la misma vista con el error
     */
    @PostMapping("/login")
    public String iniciarSesion(@RequestParam(name = "correo", required = false) String correo,
                                @RequestParam(name = "contrasena", required = false) String contrasena,
                                HttpServletRequest request, Model model, RedirectAttributes redirect) {

        if (!StringUtils.hasText(correo) || !StringUtils.hasText(contrasena)) {
            return mostrarErrorLogin(model, correo, "Escriba su correo y su contraseña.");
        }

        Optional<Usuario> usuarioAutenticado = usuarioService.autenticarUsuario(correo, contrasena);
        if (usuarioAutenticado.isEmpty()) {
            return mostrarErrorLogin(model, correo, "El correo o la contraseña no son correctos.");
        }

        // Se cambia el id de la sesión al autenticar para evitar la fijación de sesión
        HttpSession sesion = request.getSession();
        request.changeSessionId();
        UsuarioSesion usuarioSesion = UsuarioSesion.desdeUsuario(usuarioAutenticado.get());
        sesion.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO_SESION, usuarioSesion);

        redirect.addFlashAttribute("mensajeExito",
                "Bienvenido(a), " + usuarioSesion.getNombres() + ". Inició sesión correctamente.");
        return "redirect:/panel";
    }

    /** Cierra la sesión (POST, porque cambia el estado del servidor). */
    @PostMapping("/salir")
    public String cerrarSesion(HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate();
        }
        return "redirect:/login?salida";
    }

    /** Panel principal con el conteo de registros de cada tabla. */
    @GetMapping("/panel")
    public String mostrarPanel(Model model) {
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());
        model.addAttribute("totalGuias", guiaService.contarGuias());
        model.addAttribute("totalPasos", pasoService.contarPasos());
        model.addAttribute("totalCategorias", categoriaService.contarCategorias());
        return "panel";
    }

    /** Vuelve al formulario de inicio de sesión con el mensaje y el correo ya escrito. */
    private String mostrarErrorLogin(Model model, String correo, String mensaje) {
        model.addAttribute("mensajeError", mensaje);
        model.addAttribute("correoEscrito", correo);
        return VISTA_LOGIN;
    }
}
