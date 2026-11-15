package co.edu.sena.agi.controlador;

import co.edu.sena.agi.dto.PasoFormulario;
import co.edu.sena.agi.excepcion.DatoDuplicadoException;
import co.edu.sena.agi.modelo.Paso;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador web de los pasos de una guía. Todas las rutas cuelgan de la guía:
 * <ul>
 *   <li>POST /guias/{idGuia}/pasos                     : agrega un paso.</li>
 *   <li>GET  /guias/{idGuia}/pasos/{idPaso}/editar     : formulario de edición.</li>
 *   <li>POST /guias/{idGuia}/pasos/{idPaso}            : actualiza el paso.</li>
 *   <li>POST /guias/{idGuia}/pasos/{idPaso}/eliminar   : elimina el paso.</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Controller
@RequestMapping("/guias/{idGuia}/pasos")
public class PasoController {

    private static final String VISTA_DETALLE_GUIA = "guias/detalle";
    private static final String VISTA_FORMULARIO = "pasos/formulario";

    private final PasoService pasoService;
    private final GuiaService guiaService;

    /**
     * @param pasoService servicio de pasos
     * @param guiaService servicio de guías (para volver a mostrar el detalle)
     */
    public PasoController(PasoService pasoService, GuiaService guiaService) {
        this.pasoService = pasoService;
        this.guiaService = guiaService;
    }

    /** Agrega un paso. Si hay errores, vuelve al detalle de la guía mostrándolos. */
    @PostMapping
    public String registrarPaso(@PathVariable("idGuia") Integer idGuia,
                                @Valid @ModelAttribute("pasoFormulario") PasoFormulario formulario,
                                BindingResult resultado, Model model, RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Paso paso = pasoService.registrarPaso(idGuia, formulario);
                redirect.addFlashAttribute("mensajeExito", "Se agregó el paso " + paso.getNumeroPaso() + ".");
                return "redirect:/guias/" + idGuia;
            } catch (DatoDuplicadoException excepcion) {
                resultado.rejectValue(excepcion.getCampo(), "duplicado", excepcion.getMessage());
            }
        }
        model.addAttribute("guia", guiaService.buscarGuiaPorId(idGuia));
        return VISTA_DETALLE_GUIA;
    }

    /** Formulario para editar un paso. */
    @GetMapping("/{idPaso}/editar")
    public String mostrarFormularioEdicion(@PathVariable("idGuia") Integer idGuia,
                                           @PathVariable("idPaso") Integer idPaso, Model model) {
        Paso paso = pasoService.buscarPaso(idGuia, idPaso);
        model.addAttribute("guia", paso.getGuia());
        model.addAttribute("pasoFormulario", PasoFormulario.desdePaso(paso));
        return VISTA_FORMULARIO;
    }

    /** Actualiza el paso y vuelve al detalle de la guía. */
    @PostMapping("/{idPaso}")
    public String actualizarPaso(@PathVariable("idGuia") Integer idGuia,
                                 @PathVariable("idPaso") Integer idPaso,
                                 @Valid @ModelAttribute("pasoFormulario") PasoFormulario formulario,
                                 BindingResult resultado, Model model, RedirectAttributes redirect) {
        formulario.setIdPaso(idPaso);
        if (!resultado.hasErrors()) {
            try {
                pasoService.actualizarPaso(idGuia, idPaso, formulario);
                redirect.addFlashAttribute("mensajeExito", "El paso se actualizó correctamente.");
                return "redirect:/guias/" + idGuia;
            } catch (DatoDuplicadoException excepcion) {
                resultado.rejectValue(excepcion.getCampo(), "duplicado", excepcion.getMessage());
            }
        }
        model.addAttribute("guia", guiaService.buscarGuiaPorId(idGuia));
        return VISTA_FORMULARIO;
    }

    /** Elimina el paso. */
    @PostMapping("/{idPaso}/eliminar")
    public String eliminarPaso(@PathVariable("idGuia") Integer idGuia,
                               @PathVariable("idPaso") Integer idPaso, RedirectAttributes redirect) {
        pasoService.eliminarPaso(idGuia, idPaso);
        redirect.addFlashAttribute("mensajeExito", "El paso fue eliminado.");
        return "redirect:/guias/" + idGuia;
    }
}
