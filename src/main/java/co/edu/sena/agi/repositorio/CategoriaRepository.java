package co.edu.sena.agi.repositorio;

import co.edu.sena.agi.modelo.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data JPA para la tabla {@code categoria}.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    /** Lista las categorías en orden alfabético para los menús de selección. */
    List<Categoria> findAllByOrderByNombreCategoriaAsc();
}
