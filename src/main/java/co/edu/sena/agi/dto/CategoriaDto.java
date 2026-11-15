package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Categoria;

/**
 * Categoría tal como la entrega la API REST en formato JSON.
 *
 * @param idCategoria     id de la categoría
 * @param nombreCategoria nombre que se muestra en la aplicación
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public record CategoriaDto(Integer idCategoria, String nombreCategoria) {

    /**
     * Convierte la entidad en el objeto que se envía como JSON.
     *
     * @param categoria entidad leída de la base de datos
     * @return datos de la categoría para la API
     */
    public static CategoriaDto desdeEntidad(Categoria categoria) {
        return new CategoriaDto(categoria.getIdCategoria(), categoria.getNombreCategoria());
    }
}
