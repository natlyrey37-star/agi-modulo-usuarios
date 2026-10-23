package co.edu.sena.agi.dao;

import co.edu.sena.agi.conexion.ConexionBD;
import co.edu.sena.agi.modelo.Usuario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementa el CRUD de la tabla usuario con JDBC.
 * Todas las consultas usan PreparedStatement para evitar inyección SQL,
 * y try-with-resources para cerrar conexión, sentencia y resultados.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_INSERTAR =
            "INSERT INTO usuario (nombres, apellidos, cedula, correo, "
            + "contrasena, fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_LISTAR =
            "SELECT id_usuario, nombres, apellidos, cedula, correo, contrasena, fecha_nacimiento "
            + "FROM usuario ORDER BY id_usuario";

    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_usuario, nombres, apellidos, cedula, correo, contrasena, fecha_nacimiento "
            + "FROM usuario WHERE id_usuario = ?";

    private static final String SQL_BUSCAR_POR_CORREO =
            "SELECT id_usuario, nombres, apellidos, cedula, correo, contrasena, fecha_nacimiento "
            + "FROM usuario WHERE correo = ?";

    private static final String SQL_ACTUALIZAR =
            "UPDATE usuario SET nombres = ?, apellidos = ?, cedula = ?, correo = ?, "
            + "contrasena = ?, fecha_nacimiento = ? WHERE id_usuario = ?";

    private static final String SQL_ELIMINAR =
            "DELETE FROM usuario WHERE id_usuario = ?";

    @Override
    public int insertarUsuario(Usuario usuario) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {

            asignarParametros(sentencia, usuario);
            sentencia.executeUpdate();

            // Recupera el id que MySQL asignó con AUTO_INCREMENT
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                if (llaves.next()) {
                    int idGenerado = llaves.getInt(1);
                    usuario.setIdUsuario(idGenerado);
                    return idGenerado;
                }
            }
        }
        return 0;
    }

    @Override
    public List<Usuario> listarUsuarios() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_LISTAR);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(mapearUsuario(resultado));
            }
        }
        return usuarios;
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorId(int idUsuario) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_ID)) {

            sentencia.setInt(1, idUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(mapearUsuario(resultado));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorCorreo(String correo) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_BUSCAR_POR_CORREO)) {

            sentencia.setString(1, correo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return Optional.of(mapearUsuario(resultado));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ACTUALIZAR)) {

            asignarParametros(sentencia, usuario);
            sentencia.setInt(7, usuario.getIdUsuario());
            return sentencia.executeUpdate() > 0;
        }
    }

    @Override
    public boolean eliminarUsuario(int idUsuario) throws SQLException {
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(SQL_ELIMINAR)) {

            sentencia.setInt(1, idUsuario);
            return sentencia.executeUpdate() > 0;
        }
    }

    /** Pasa los datos del usuario a los seis primeros parámetros (?) de la sentencia. */
    private void asignarParametros(PreparedStatement sentencia, Usuario usuario) throws SQLException {
        sentencia.setString(1, usuario.getNombres());
        sentencia.setString(2, usuario.getApellidos());
        sentencia.setString(3, usuario.getCedula());
        sentencia.setString(4, usuario.getCorreo());
        sentencia.setString(5, usuario.getContrasena());
        sentencia.setDate(6, Date.valueOf(usuario.getFechaNacimiento()));
    }

    /** Convierte la fila actual del ResultSet en un objeto Usuario. */
    private Usuario mapearUsuario(ResultSet resultado) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultado.getInt("id_usuario"));
        usuario.setNombres(resultado.getString("nombres"));
        usuario.setApellidos(resultado.getString("apellidos"));
        usuario.setCedula(resultado.getString("cedula"));
        usuario.setCorreo(resultado.getString("correo"));
        usuario.setContrasena(resultado.getString("contrasena"));
        usuario.setFechaNacimiento(resultado.getDate("fecha_nacimiento").toLocalDate());
        return usuario;
    }
}
