package co.edu.sena.agi.dto;

import co.edu.sena.agi.modelo.Paso;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Datos del formulario para agregar o editar un paso de una guía.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
public class PasoFormulario {

    /** Número máximo de pasos que se permite en una guía. */
    public static final int NUMERO_MAXIMO_PASO = 50;

    private Integer idPaso;

    @NotNull(message = "Escriba el número del paso.")
    @Min(value = 1, message = "El número del paso debe ser mayor que cero.")
    @Max(value = NUMERO_MAXIMO_PASO, message = "El número del paso no puede ser mayor que 50.")
    private Integer numeroPaso;

    @NotBlank(message = "Escriba el título del paso.")
    @Size(max = 100, message = "El título del paso no puede superar 100 caracteres.")
    private String tituloPaso;

    @NotBlank(message = "Escriba la instrucción del paso.")
    @Size(max = 255, message = "La instrucción no puede superar 255 caracteres.")
    private String instruccion;

    /** Constructor vacío que usa Spring al recibir el formulario. */
    public PasoFormulario() {
    }

    /**
     * Crea el formulario de edición con los datos actuales del paso.
     *
     * @param paso paso leído de la base de datos
     * @return formulario lleno
     */
    public static PasoFormulario desdePaso(Paso paso) {
        PasoFormulario formulario = new PasoFormulario();
        formulario.setIdPaso(paso.getIdPaso());
        formulario.setNumeroPaso(paso.getNumeroPaso());
        formulario.setTituloPaso(paso.getTituloPaso());
        formulario.setInstruccion(paso.getInstruccion());
        return formulario;
    }

    public Integer getIdPaso() {
        return idPaso;
    }

    public void setIdPaso(Integer idPaso) {
        this.idPaso = idPaso;
    }

    public Integer getNumeroPaso() {
        return numeroPaso;
    }

    public void setNumeroPaso(Integer numeroPaso) {
        this.numeroPaso = numeroPaso;
    }

    public String getTituloPaso() {
        return tituloPaso;
    }

    public void setTituloPaso(String tituloPaso) {
        this.tituloPaso = tituloPaso;
    }

    public String getInstruccion() {
        return instruccion;
    }

    public void setInstruccion(String instruccion) {
        this.instruccion = instruccion;
    }
}
