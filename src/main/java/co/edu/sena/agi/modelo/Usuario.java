package co.edu.sena.agi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * Entidad JPA que representa a una persona registrada en AGI.
 * Cada atributo se mapea con una columna de la tabla {@code usuario}.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    /** Clave primaria; MySQL la asigna con AUTO_INCREMENT. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "nombres", nullable = false, length = 60)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 60)
    private String apellidos;

    @Column(name = "cedula", nullable = false, length = 15, unique = true)
    private String cedula;

    @Column(name = "correo", nullable = false, length = 100, unique = true)
    private String correo;

    /** Hash BCrypt de la contraseña; nunca se guarda el texto original. */
    @Column(name = "contrasena", nullable = false, length = 255)
    private String contrasena;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /** Constructor vacío que exige JPA para crear la entidad al leer la base de datos. */
    public Usuario() {
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

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Devuelve el nombre completo del usuario.
     *
     * @return nombres y apellidos separados por un espacio
     */
    public String obtenerNombreCompleto() {
        return nombres + " " + apellidos;
    }
}
