package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Datos que llegan del formulario de registro y edición de usuarios.
 * <p>
 * Se separa de la entidad {@link Usuario} porque el formulario trae campos que no
 * se guardan tal cual (la confirmación de la contraseña) y porque al editar la
 * contraseña es opcional. Las anotaciones de Bean Validation definen las reglas
 * que Spring revisa antes de llegar al servicio.
 * </p>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public class UsuarioFormulario {

    /** Letras (con tildes y ñ), espacios, apóstrofo, punto y guion. */
    private static final String PATRON_NOMBRE = "^[\\p{L} '.-]*$";

    private Integer idUsuario;

    @NotBlank(message = "Escriba los nombres.")
    @Size(max = 60, message = "Los nombres no pueden superar 60 caracteres.")
    @Pattern(regexp = PATRON_NOMBRE, message = "Los nombres solo pueden tener letras y espacios.")
    private String nombres;

    @NotBlank(message = "Escriba los apellidos.")
    @Size(max = 60, message = "Los apellidos no pueden superar 60 caracteres.")
    @Pattern(regexp = PATRON_NOMBRE, message = "Los apellidos solo pueden tener letras y espacios.")
    private String apellidos;

    @NotBlank(message = "Escriba la cédula.")
    @Pattern(regexp = "^\\d{6,15}$", message = "La cédula debe tener solo números, entre 6 y 15 dígitos.")
    private String cedula;

    @NotBlank(message = "Escriba el correo electrónico.")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres.")
    @Pattern(regexp = "^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$",
            message = "Escriba un correo válido, por ejemplo nombre@correo.com.")
    private String correo;

    /** Obligatoria al registrar; al editar, vacía significa "conservar la actual". */
    @Size(min = 6, max = 50, message = "La contraseña debe tener entre 6 y 50 caracteres.")
    private String contrasena;

    private String confirmarContrasena;

    @NotNull(message = "Seleccione la fecha de nacimiento.")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    /** Constructor vacío que usa Spring para llenar el objeto con los datos del formulario. */
    public UsuarioFormulario() {
    }

    /**
     * Crea el formulario de edición a partir de un usuario guardado.
     * La contraseña no se copia: el campo aparece vacío.
     *
     * @param usuario usuario leído de la base de datos
     * @return formulario con los datos actuales
     */
    public static UsuarioFormulario desdeUsuario(Usuario usuario) {
        UsuarioFormulario formulario = new UsuarioFormulario();
        formulario.setIdUsuario(usuario.getIdUsuario());
        formulario.setNombres(usuario.getNombres());
        formulario.setApellidos(usuario.getApellidos());
        formulario.setCedula(usuario.getCedula());
        formulario.setCorreo(usuario.getCorreo());
        formulario.setFechaNacimiento(usuario.getFechaNacimiento());
        return formulario;
    }

    /**
     * Indica si la persona escribió una contraseña en el formulario.
     *
     * @return true si el campo contraseña tiene texto
     */
    public boolean tieneContrasena() {
        return contrasena != null && !contrasena.isEmpty();
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
