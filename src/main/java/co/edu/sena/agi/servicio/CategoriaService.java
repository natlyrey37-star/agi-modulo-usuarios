package co.edu.sena.agi.servicio;

import co.edu.sena.agi.dto.CategoriaDto;
import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import co.edu.sena.agi.modelo.Categoria;
import co.edu.sena.agi.repositorio.CategoriaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta de las categorías de guías (catálogo cargado por el script SQL).
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Service
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    /**
     * @param categoriaRepository acceso a la tabla categoria
     */
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /**
     * @return categorías en orden alfabético
     */
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAllByOrderByNombreCategoriaAsc();
    }

    /**
     * @return categorías convertidas al formato de la API
     */
    public List<CategoriaDto> listarCategoriasParaApi() {
        return listarCategorias().stream()
                .map(CategoriaDto::desdeEntidad)
                .toList();
    }

    /**
     * Busca una categoría que debe existir (la que se selecciona en el formulario de guía).
     *
     * @param idCategoria id de la categoría
     * @return categoría encontrada
     */
    public Categoria buscarCategoriaPorId(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new RecursoNoEncontradoException("La categoría seleccionada no existe."));
    }

    /**
     * @return cantidad de categorías (para el panel)
     */
    public long contarCategorias() {
        return categoriaRepository.count();
    }
}
