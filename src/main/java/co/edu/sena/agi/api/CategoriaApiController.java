package co.edu.sena.agi.api;

import co.edu.sena.agi.dto.CategoriaDto;
import co.edu.sena.agi.servicio.CategoriaService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de categorías: la aplicación móvil la usa para los botones de filtro.
 * <ul>
 *   <li>GET /api/categorias : lista de categorías en JSON.</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaApiController {

    private final CategoriaService categoriaService;

    /**
     * @param categoriaService servicio de categorías
     */
    public CategoriaApiController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    /**
     * @return categorías en orden alfabético
     */
    @GetMapping
    public List<CategoriaDto> listarCategorias() {
        return categoriaService.listarCategoriasParaApi();
    }
}
