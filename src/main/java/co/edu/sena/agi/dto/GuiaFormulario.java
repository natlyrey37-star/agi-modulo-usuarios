package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Guia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario para crear o editar una guía.
 * La categoría llega como su id, que es el valor de la lista desplegable.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public class GuiaFormulario {

    private Integer idGuia;

    @NotBlank(message = "Escriba el título de la guía.")
    @Size(max = 100, message = "El título no puede superar 100 caracteres.")
    private String titulo;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres.")
    private String descripcion;

    @NotNull(message = "Seleccione una categoría.")
    private Integer idCategoria;

    /** Constructor vacío que usa Spring al recibir el formulario. */
    public GuiaFormulario() {
    }

    /**
     * Crea el formulario de edición con los datos actuales de la guía.
     *
     * @param guia guía leída de la base de datos
     * @return formulario lleno
     */
    public static GuiaFormulario desdeGuia(Guia guia) {
        GuiaFormulario formulario = new GuiaFormulario();
        formulario.setIdGuia(guia.getIdGuia());
        formulario.setTitulo(guia.getTitulo());
        formulario.setDescripcion(guia.getDescripcion());
        formulario.setIdCategoria(guia.getCategoria().getIdCategoria());
        return formulario;
    }

    public Integer getIdGuia() {
        return idGuia;
    }

    public void setIdGuia(Integer idGuia) {
        this.idGuia = idGuia;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }
}
