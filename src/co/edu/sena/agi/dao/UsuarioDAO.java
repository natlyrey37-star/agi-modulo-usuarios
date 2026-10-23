package co.edu.sena.agi.dao;

import co.edu.sena.agi.modelo.Usuario;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Define las operaciones CRUD de la tabla usuario.
 * La implementación con JDBC está en {@link UsuarioDAOImpl}.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public interface UsuarioDAO {

    /** Create: guarda un usuario nuevo y devuelve el id generado. */
    int insertarUsuario(Usuario usuario) throws SQLException;

    /** Read: devuelve todos los usuarios ordenados por id. */
    List<Usuario> listarUsuarios() throws SQLException;

    /** Read: busca un usuario por su id. */
    Optional<Usuario> buscarUsuarioPorId(int idUsuario) throws SQLException;

    /** Read: busca un usuario por su correo (lo usará el inicio de sesión). */
    Optional<Usuario> buscarUsuarioPorCorreo(String correo) throws SQLException;

    /** Update: modifica los datos de un usuario existente. */
    boolean actualizarUsuario(Usuario usuario) throws SQLException;

    /** Delete: elimina un usuario por su id. */
    boolean eliminarUsuario(int idUsuario) throws SQLException;
}
