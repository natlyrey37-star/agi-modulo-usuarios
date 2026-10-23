package co.edu.sena.agi.modelo;

import java.time.LocalDate;

/**
 * Representa a una persona registrada en AGI.
 * Cada atributo corresponde a una columna de la tabla usuario.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public class Usuario {

    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String cedula;
    private String correo;
    private String contrasena;
    private LocalDate fechaNacimiento;

    /** Constructor vacío, usado al leer registros desde la base de datos. */
    public Usuario() {
    }

    /** Constructor para registrar un usuario nuevo (sin id, lo asigna MySQL). */
    public Usuario(String nombres, String apellidos, String cedula,
                   String correo, String contrasena, LocalDate fechaNacimiento) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cedula = cedula;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
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

    /** Devuelve el nombre completo del usuario. */
    public String obtenerNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /** La contraseña no se muestra por seguridad. */
    @Override
    public String toString() {
        return String.format("%-4d %-28s %-12s %-30s %s",
                idUsuario, obtenerNombreCompleto(), cedula, correo, fechaNacimiento);
    }
}
