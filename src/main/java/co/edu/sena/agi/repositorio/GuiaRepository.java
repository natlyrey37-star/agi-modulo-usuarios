package co.edu.sena.agi.repositorio;

import co.edu.sena.agi.modelo.Guia;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data JPA para la tabla {@code guia}.
 * <p>
 * {@code @EntityGraph} indica que la categoría se traiga en la misma consulta
 * (JOIN), para no hacer una consulta adicional por cada guía del listado.
 * </p>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public interface GuiaRepository extends JpaRepository<Guia, Integer> {

    /** Todas las guías con su categoría, ordenadas por id. */
    @EntityGraph(attributePaths = "categoria")
    List<Guia> findAllByOrderByIdGuiaAsc();

    /** Guías de una sola categoría (filtro del listado y de la API). */
    @EntityGraph(attributePaths = "categoria")
    List<Guia> findByCategoriaIdCategoriaOrderByIdGuiaAsc(Integer idCategoria);
}
