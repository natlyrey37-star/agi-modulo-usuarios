package co.edu.sena.agi.repositorio;

import co.edu.sena.agi.modelo.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio de Spring Data JPA para la tabla {@code usuario}.
 * <p>
 * Reemplaza al UsuarioDAOImpl de la versión 2.0: el CRUD básico (save, findById,
 * findAll, delete) lo hereda de {@link JpaRepository}, y las consultas propias se
 * declaran con el nombre del método; Spring genera el SQL automáticamente.
 * </p>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /** SELECT ... FROM usuario ORDER BY id_usuario. */
    List<Usuario> findAllByOrderByIdUsuarioAsc();

    /** Busca la cuenta con la que se inicia sesión. */
    Optional<Usuario> findByCorreo(String correo);

    /** Indica si la cédula ya está registrada (al crear un usuario). */
    boolean existsByCedula(String cedula);

    /** Indica si el correo ya está registrado (al crear un usuario). */
    boolean existsByCorreo(String correo);

    /** Indica si otra cuenta, distinta de la que se edita, ya tiene la cédula. */
    boolean existsByCedulaAndIdUsuarioNot(String cedula, Integer idUsuario);

    /** Indica si otra cuenta, distinta de la que se edita, ya tiene el correo. */
    boolean existsByCorreoAndIdUsuarioNot(String correo, Integer idUsuario);
}
