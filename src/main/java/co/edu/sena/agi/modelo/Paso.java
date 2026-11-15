package co.edu.sena.agi.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidad JPA de la tabla {@code paso}: una instrucción ordenada dentro de una guía.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Entity
@Table(name = "paso")
public class Paso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paso")
    private Integer idPaso;

    /** Número de orden del paso; la base de datos exige que sea mayor que cero. */
    @Column(name = "numero_paso", nullable = false)
    private Integer numeroPaso;

    @Column(name = "titulo_paso", nullable = false, length = 100)
    private String tituloPaso;

    @Column(name = "instruccion", nullable = false, length = 255)
    private String instruccion;

    /** Guía a la que pertenece el paso (clave foránea id_guia). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_guia", nullable = false)
    private Guia guia;

    /** Constructor vacío que exige JPA. */
    public Paso() {
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

    public Guia getGuia() {
        return guia;
    }

    public void setGuia(Guia guia) {
        this.guia = guia;
    }
}
