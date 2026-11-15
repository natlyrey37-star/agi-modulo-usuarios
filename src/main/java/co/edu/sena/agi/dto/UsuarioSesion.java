package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Usuario;
import java.io.Serializable;

/**
 * Datos mínimos del usuario que se guardan en la sesión HTTP al iniciar sesión.
 * No incluye la contraseña ni el hash: solo lo necesario para saludar a la persona
 * y saber quién está conectado.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public class UsuarioSesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer idUsuario;
    private final String nombres;
    private final String correo;

    /**
     * @param idUsuario id del usuario autenticado
     * @param nombres   nombres, para el saludo del encabezado
     * @param correo    correo con el que inició sesión
     */
    public UsuarioSesion(Integer idUsuario, String nombres, String correo) {
        this.idUsuario = idUsuario;
        this.nombres = nombres;
        this.correo = correo;
    }

    /**
     * Crea el objeto de sesión a partir de la entidad.
     *
     * @param usuario usuario que inició sesión
     * @return datos para guardar en la sesión
     */
    public static UsuarioSesion desdeUsuario(Usuario usuario) {
        return new UsuarioSesion(usuario.getIdUsuario(), usuario.getNombres(), usuario.getCorreo());
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public String getCorreo() {
        return correo;
    }
}
