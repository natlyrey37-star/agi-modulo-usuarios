package co.edu.sena.agi.servicio;

import co.edu.sena.agi.dto.UsuarioFormulario;
import co.edu.sena.agi.excepcion.DatoDuplicadoException;
import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import co.edu.sena.agi.modelo.Usuario;
import co.edu.sena.agi.repositorio.UsuarioRepository;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio del módulo de usuarios: registro, consulta, actualización,
 * eliminación e inicio de sesión.
 * <p>
 * Los controladores no acceden al repositorio directamente; pasan por este
 * servicio, que valida los datos únicos y cifra la contraseña antes de guardar.
 * {@code @Transactional} hace que cada operación se ejecute en una transacción.
 * </p>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContrasenaService contrasenaService;

    /**
     * Spring entrega las dependencias por el constructor (inyección de dependencias).
     *
     * @param usuarioRepository acceso a la tabla usuario
     * @param contrasenaService cifrado de contraseñas
     */
    public UsuarioService(UsuarioRepository usuarioRepository, ContrasenaService contrasenaService) {
        this.usuarioRepository = usuarioRepository;
        this.contrasenaService = contrasenaService;
    }

    /**
     * @return todos los usuarios ordenados por id
     */
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAllByOrderByIdUsuarioAsc();
    }

    /**
     * Busca un usuario por su id sin lanzar error si no existe (para la búsqueda del listado).
     *
     * @param idUsuario id que escribió la persona
     * @return el usuario, o vacío si no existe
     */
    public Optional<Usuario> buscarUsuarioOpcional(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    /**
     * Busca un usuario que debe existir (para editar o eliminar).
     *
     * @param idUsuario id del usuario
     * @return el usuario encontrado
     * @throws RecursoNoEncontradoException si no hay un usuario con ese id
     */
    public Usuario buscarUsuarioPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un usuario con el id " + idUsuario + "."));
    }

    /**
     * @return cantidad de usuarios registrados (para el panel)
     */
    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    /**
     * Registra un usuario nuevo con la contraseña cifrada en BCrypt.
     *
     * @param formulario datos ya validados del formulario
     * @return usuario guardado, con el id que asignó MySQL
     * @throws DatoDuplicadoException si la cédula o el correo ya existen
     */
    @Transactional
    public Usuario registrarUsuario(UsuarioFormulario formulario) {
        validarDatosUnicos(formulario, null);

        Usuario usuario = new Usuario();
        copiarDatos(formulario, usuario);
        usuario.setContrasena(contrasenaService.cifrarContrasena(formulario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza los datos de un usuario. Si la contraseña viene vacía se conserva la actual.
     *
     * @param idUsuario  id del usuario que se edita
     * @param formulario datos ya validados del formulario
     * @return usuario actualizado
     */
    @Transactional
    public Usuario actualizarUsuario(Integer idUsuario, UsuarioFormulario formulario) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        // Se valida antes de modificar la entidad, para no enviar cambios a medias
        validarDatosUnicos(formulario, idUsuario);

        copiarDatos(formulario, usuario);
        if (formulario.tieneContrasena()) {
            usuario.setContrasena(contrasenaService.cifrarContrasena(formulario.getContrasena()));
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario. Sus registros de progreso se borran en cascada (ON DELETE CASCADE).
     *
     * @param idUsuario id del usuario
     */
    @Transactional
    public void eliminarUsuario(Integer idUsuario) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);
        usuarioRepository.delete(usuario);
    }

    /**
     * Verifica el correo y la contraseña para iniciar sesión.
     *
     * @param correo     correo escrito en el formulario
     * @param contrasena contraseña escrita en el formulario
     * @return el usuario si las credenciales son correctas; vacío en caso contrario
     */
    @Transactional
    public Optional<Usuario> autenticarUsuario(String correo, String contrasena) {
        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCorreo(normalizarCorreo(correo));
        if (usuarioEncontrado.isEmpty()
                || !contrasenaService.verificarContrasena(contrasena, usuarioEncontrado.get().getContrasena())) {
            return Optional.empty();
        }

        Usuario usuario = usuarioEncontrado.get();
        // Las cuentas de la versión 2.0 tenían SHA-256: al entrar se actualizan a BCrypt
        if (contrasenaService.esHashAnterior(usuario.getContrasena())) {
            usuario.setContrasena(contrasenaService.cifrarContrasena(contrasena));
            usuarioRepository.save(usuario);
        }
        return Optional.of(usuario);
    }

    /** Revisa que la cédula y el correo no pertenezcan a otra cuenta. */
    private void validarDatosUnicos(UsuarioFormulario formulario, Integer idUsuarioEditado) {
        String cedula = formulario.getCedula().trim();
        String correo = normalizarCorreo(formulario.getCorreo());
        boolean esRegistro = idUsuarioEditado == null;

        boolean cedulaRepetida = esRegistro
                ? usuarioRepository.existsByCedula(cedula)
                : usuarioRepository.existsByCedulaAndIdUsuarioNot(cedula, idUsuarioEditado);
        if (cedulaRepetida) {
            throw new DatoDuplicadoException("cedula", "Ya existe un usuario registrado con esa cédula.");
        }

        boolean correoRepetido = esRegistro
                ? usuarioRepository.existsByCorreo(correo)
                : usuarioRepository.existsByCorreoAndIdUsuarioNot(correo, idUsuarioEditado);
        if (correoRepetido) {
            throw new DatoDuplicadoException("correo", "Ya existe un usuario registrado con ese correo.");
        }
    }

    /** Pasa los datos del formulario a la entidad, sin espacios sobrantes. */
    private void copiarDatos(UsuarioFormulario formulario, Usuario usuario) {
        usuario.setNombres(formulario.getNombres().trim());
        usuario.setApellidos(formulario.getApellidos().trim());
        usuario.setCedula(formulario.getCedula().trim());
        usuario.setCorreo(normalizarCorreo(formulario.getCorreo()));
        usuario.setFechaNacimiento(formulario.getFechaNacimiento());
    }

    /** El correo se guarda siempre en minúsculas para que el inicio de sesión no dependa de ellas. */
    private String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }
}
