package co.edu.sena.agi.api;

import co.edu.sena.agi.dto.GuiaDetalleDto;
import co.edu.sena.agi.dto.GuiaResumenDto;
import co.edu.sena.agi.servicio.GuiaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de guías que consume la aplicación móvil de AGI.
 * {@code @RestController} convierte automáticamente las respuestas en JSON.
 * <ul>
 *   <li>GET /api/guias                 : catálogo de guías.</li>
 *   <li>GET /api/guias?categoria=N     : guías de una categoría.</li>
 *   <li>GET /api/guias/{id}            : una guía con sus pasos en orden.</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@RestController
@RequestMapping("/api/guias")
public class GuiaApiController {

    private final GuiaService guiaService;

    /**
     * @param guiaService servicio de guías
     */
    public GuiaApiController(GuiaService guiaService) {
        this.guiaService = guiaService;
    }

    /**
     * Catálogo de guías.
     *
     * @param idCategoria filtro opcional por categoría
     * @return lista de guías en JSON
     */
    @GetMapping
    public List<GuiaResumenDto> listarGuias(
            @RequestParam(name = "categoria", required = false) Integer idCategoria) {
        return guiaService.listarResumenGuias(idCategoria);
    }

    /**
     * Guía completa con sus pasos. Si no existe responde 404 (ver ManejadorErroresApi).
     *
     * @param idGuia id de la guía
     * @return guía con pasos en JSON
     */
    @GetMapping("/{idGuia}")
    public GuiaDetalleDto obtenerGuia(@PathVariable("idGuia") Integer idGuia) {
        return guiaService.obtenerDetalleGuia(idGuia);
    }
}
