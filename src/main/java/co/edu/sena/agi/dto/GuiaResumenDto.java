package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Guia;

/**
 * Datos resumidos de una guía para el catálogo de la aplicación móvil.
 *
 * @param idGuia      id de la guía
 * @param titulo      título de la guía
 * @param descripcion descripción breve
 * @param categoria   nombre de la categoría
 * @param totalPasos  cantidad de pasos que tiene la guía
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public record GuiaResumenDto(Integer idGuia, String titulo, String descripcion,
                             String categoria, int totalPasos) {

    /**
     * Convierte la entidad en el objeto que se envía como JSON.
     *
     * @param guia entidad leída de la base de datos
     * @return resumen de la guía para la API
     */
    public static GuiaResumenDto desdeEntidad(Guia guia) {
        return new GuiaResumenDto(guia.getIdGuia(), guia.getTitulo(), guia.getDescripcion(),
                guia.getCategoria().getNombreCategoria(), guia.getPasos().size());
    }
}
