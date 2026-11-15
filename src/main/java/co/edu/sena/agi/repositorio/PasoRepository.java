package co.edu.sena.agi.repositorio;

import co.edu.sena.agi.modelo.Paso;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data JPA para la tabla {@code paso}.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public interface PasoRepository extends JpaRepository<Paso, Integer> {

    /** Busca un paso verificando que pertenezca a la guía indicada. */
    Optional<Paso> findByIdPasoAndGuiaIdGuia(Integer idPaso, Integer idGuia);

    /** Indica si la guía ya tiene un paso con ese número (al agregar). */
    boolean existsByGuiaIdGuiaAndNumeroPaso(Integer idGuia, Integer numeroPaso);

    /** Indica si otro paso de la misma guía ya tiene ese número (al editar). */
    boolean existsByGuiaIdGuiaAndNumeroPasoAndIdPasoNot(Integer idGuia, Integer numeroPaso, Integer idPaso);
}
