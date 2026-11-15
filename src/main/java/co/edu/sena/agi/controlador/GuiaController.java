package co.edu.sena.agi.controlador;

import co.edu.sena.agi.dto.GuiaFormulario;
import co.edu.sena.agi.dto.PasoFormulario;
import co.edu.sena.agi.modelo.Guia;
import co.edu.sena.agi.servicio.CategoriaService;
import co.edu.sena.agi.servicio.GuiaService;
import co.edu.sena.agi.servicio.PasoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web del módulo de guías (CRUD de la tabla guia).
 * <ul>
 *   <li>GET  /guias                : listado, con filtro opcional ?categoria=N.</li>
 *   <li>GET  /guias/nueva          : formulario de registro.</li>
 *   <li>POST /guias                : registra la guía.</li>
 *   <li>GET  /guias/{id}           : detalle de la guía con sus pasos.</li>
 *   <li>GET  /guias/{id}/editar    : formulario de edición.</li>
 *   <li>POST /guias/{id}           : actualiza la guía.</li>
 *   <li>POST /guias/{id}/eliminar  : elimina la guía y sus pasos.</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Controller
@RequestMapping("/guias")
public class GuiaController {

    private static final String VISTA_LISTA = "guias/lista";
    private static final String VISTA_FORMULARIO = "guias/formulario";
    private static final String VISTA_DETALLE = "guias/detalle";

    private final GuiaService guiaService;
    private final CategoriaService categoriaService;
    private final PasoService pasoService;

    /**
     * @param guiaService      servicio de guías
     * @param categoriaService servicio de categorías (lista desplegable)
     * @param pasoService      servicio de pasos (número sugerido)
     */
    public GuiaController(GuiaService guiaService, CategoriaService categoriaService, PasoService pasoService) {
        this.guiaService = guiaService;
        this.categoriaService = categoriaService;
        this.pasoService = pasoService;
    }

    /** Lista las guías; el filtro por categoría llega por GET en la URL. */
    @GetMapping
    public String listarGuias(@RequestParam(name = "categoria", required = false) Integer idCategoria, Model model) {
        model.addAttribute("guias", guiaService.listarGuias(idCategoria));
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("categoriaSeleccionada", idCategoria);
        return VISTA_LISTA;
    }

    /** Formulario vacío para una guía nueva. */
    @GetMapping("/nueva")
    public String mostrarFormularioNueva(Model model) {
        model.addAttribute("guiaFormulario", new GuiaFormulario());
        prepararFormulario(model, null);
        return VISTA_FORMULARIO;
    }

    /** Detalle de la guía con la lista de pasos y el formulario para agregar uno. */
    @GetMapping("/{idGuia}")
    public String mostrarDetalle(@PathVariable("idGuia") Integer idGuia, Model model) {
        Guia guia = guiaService.buscarGuiaPorId(idGuia);
        PasoFormulario pasoFormulario = new PasoFormulario();
        pasoFormulario.setNumeroPaso(pasoService.sugerirNumeroPaso(guia));

        model.addAttribute("guia", guia);
        model.addAttribute("pasoFormulario", pasoFormulario);
        return VISTA_DETALLE;
    }

    /** Formulario de edición con los datos actuales. */
    @GetMapping("/{idGuia}/editar")
    public String mostrarFormularioEdicion(@PathVariable("idGuia") Integer idGuia, Model model) {
        Guia guia = guiaService.buscarGuiaPorId(idGuia);
        model.addAttribute("guiaFormulario", GuiaFormulario.desdeGuia(guia));
        prepararFormulario(model, idGuia);
        return VISTA_FORMULARIO;
    }

    /** Registra la guía y lleva a su detalle para agregar los pasos. */
    @PostMapping
    public String registrarGuia(@Valid @ModelAttribute("guiaFormulario") GuiaFormulario formulario,
                                BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (resultado.hasErrors()) {
            prepararFormulario(model, null);
            return VISTA_FORMULARIO;
        }
        Guia guia = guiaService.registrarGuia(formulario);
        redirect.addFlashAttribute("mensajeExito", "La guía quedó registrada. Ahora agregue sus pasos.");
        return "redirect:/guias/" + guia.getIdGuia();
    }

    /** Actualiza los datos generales de la guía. */
    @PostMapping("/{idGuia}")
    public String actualizarGuia(@PathVariable("idGuia") Integer idGuia,
                                 @Valid @ModelAttribute("guiaFormulario") GuiaFormulario formulario,
                                 BindingResult resultado, Model model, RedirectAttributes redirect) {
        formulario.setIdGuia(idGuia);
        if (resultado.hasErrors()) {
            prepararFormulario(model, idGuia);
            return VISTA_FORMULARIO;
        }
        guiaService.actualizarGuia(idGuia, formulario);
        redirect.addFlashAttribute("mensajeExito", "La guía se actualizó correctamente.");
        return "redirect:/guias/" + idGuia;
    }

    /** Elimina la guía con todos sus pasos. */
    @PostMapping("/{idGuia}/eliminar")
    public String eliminarGuia(@PathVariable("idGuia") Integer idGuia, RedirectAttributes redirect) {
        guiaService.eliminarGuia(idGuia);
        redirect.addFlashAttribute("mensajeExito", "La guía y sus pasos fueron eliminados.");
        return "redirect:/guias";
    }

    /** Agrega a la vista el título, la lista de categorías y la URL a la que se envía el formulario. */
    private void prepararFormulario(Model model, Integer idGuia) {
        boolean esEdicion = idGuia != null;
        model.addAttribute("esEdicion", esEdicion);
        model.addAttribute("tituloPagina", esEdicion ? "Editar guía" : "Nueva guía");
        model.addAttribute("accionFormulario", esEdicion ? "/guias/" + idGuia : "/guias");
        model.addAttribute("categorias", categoriaService.listarCategorias());
    }
}
