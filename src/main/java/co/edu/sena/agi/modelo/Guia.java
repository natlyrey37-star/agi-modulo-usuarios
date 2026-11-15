package co.edu.sena.agi.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA de la tabla {@code guia}: cada guía que muestra la aplicación.
 * <ul>
 *   <li>Relación N:1 con {@link Categoria} (columna id_categoria).</li>
 *   <li>Relación 1:N con {@link Paso}: si la guía se elimina, sus pasos también.</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Entity
@Table(name = "guia")
public class Guia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_guia")
    private Integer idGuia;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /** Categoría a la que pertenece la guía (clave foránea id_categoria). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    /** Pasos de la guía, siempre ordenados por su número. */
    @OneToMany(mappedBy = "guia", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numeroPaso ASC")
    private List<Paso> pasos = new ArrayList<>();

    /** Constructor vacío que exige JPA. */
    public Guia() {
    }

    /**
     * Agrega un paso a la guía y mantiene sincronizados los dos lados de la relación.
     *
     * @param paso paso que se va a agregar
     */
    public void agregarPaso(Paso paso) {
        pasos.add(paso);
        paso.setGuia(this);
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Paso> getPasos() {
        return pasos;
    }

    public void setPasos(List<Paso> pasos) {
        this.pasos = pasos;
    }
}
