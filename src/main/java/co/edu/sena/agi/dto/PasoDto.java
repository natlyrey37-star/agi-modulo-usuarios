package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Paso;

/**
 * Paso de una guía tal como lo entrega la API REST.
 *
 * @param numeroPaso  número de orden dentro de la guía
 * @param tituloPaso  título corto del paso
 * @param instruccion instrucción detallada
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public record PasoDto(Integer numeroPaso, String tituloPaso, String instruccion) {

    /**
     * Convierte la entidad en el objeto que se envía como JSON.
     *
     * @param paso entidad leída de la base de datos
     * @return datos del paso para la API
     */
    public static PasoDto desdeEntidad(Paso paso) {
        return new PasoDto(paso.getNumeroPaso(), paso.getTituloPaso(), paso.getInstruccion());
    }
}
