package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Guia;
import java.util.List;

/**
 * Guía completa, con sus pasos en orden, para recorrerla en la aplicación móvil.
 *
 * @param idGuia      id de la guía
 * @param titulo      título de la guía
 * @param descripcion descripción breve
 * @param categoria   nombre de la categoría
 * @param pasos       pasos ordenados por número
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public record GuiaDetalleDto(Integer idGuia, String titulo, String descripcion,
                             String categoria, List<PasoDto> pasos) {

    /**
     * Convierte la entidad y sus pasos en el objeto que se envía como JSON.
     *
     * @param guia entidad leída de la base de datos
     * @return guía completa para la API
     */
    public static GuiaDetalleDto desdeEntidad(Guia guia) {
        List<PasoDto> pasos = guia.getPasos().stream()
                .map(PasoDto::desdeEntidad)
                .toList();
        return new GuiaDetalleDto(guia.getIdGuia(), guia.getTitulo(), guia.getDescripcion(),
                guia.getCategoria().getNombreCategoria(), pasos);
    }
}
