package co.edu.sena.agi.servicio;

import co.edu.sena.agi.dto.GuiaDetalleDto;
import co.edu.sena.agi.dto.GuiaFormulario;
import co.edu.sena.agi.dto.GuiaResumenDto;
import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import co.edu.sena.agi.modelo.Guia;
import co.edu.sena.agi.repositorio.GuiaRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio del módulo de guías: CRUD de la tabla guia y consultas
 * que usa la API REST de la aplicación móvil.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Service
@Transactional(readOnly = true)
public class GuiaService {

    private final GuiaRepository guiaRepository;
    private final CategoriaService categoriaService;

    /**
     * @param guiaRepository   acceso a la tabla guia
     * @param categoriaService consulta de categorías
     */
    public GuiaService(GuiaRepository guiaRepository, CategoriaService categoriaService) {
        this.guiaRepository = guiaRepository;
        this.categoriaService = categoriaService;
    }

    /**
     * Lista las guías, todas o solo las de una categoría.
     *
     * @param idCategoria id de la categoría, o null para traer todas
     * @return guías ordenadas por id
     */
    public List<Guia> listarGuias(Integer idCategoria) {
        if (idCategoria == null) {
            return guiaRepository.findAllByOrderByIdGuiaAsc();
        }
        return guiaRepository.findByCategoriaIdCategoriaOrderByIdGuiaAsc(idCategoria);
    }

    /**
     * Busca una guía que debe existir.
     *
     * @param idGuia id de la guía
     * @return guía encontrada
     * @throws RecursoNoEncontradoException si no existe
     */
    public Guia buscarGuiaPorId(Integer idGuia) {
        return guiaRepository.findById(idGuia)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una guía con el id " + idGuia + "."));
    }

    /**
     * @return cantidad de guías registradas (para el panel)
     */
    public long contarGuias() {
        return guiaRepository.count();
    }

    /**
     * Registra una guía nueva. Los pasos se agregan después desde el detalle de la guía.
     *
     * @param formulario datos ya validados
     * @return guía guardada con su id
     */
    @Transactional
    public Guia registrarGuia(GuiaFormulario formulario) {
        Guia guia = new Guia();
        copiarDatos(formulario, guia);
        return guiaRepository.save(guia);
    }

    /**
     * Actualiza el título, la descripción y la categoría de una guía.
     *
     * @param idGuia     id de la guía
     * @param formulario datos ya validados
     * @return guía actualizada
     */
    @Transactional
    public Guia actualizarGuia(Integer idGuia, GuiaFormulario formulario) {
        Guia guia = buscarGuiaPorId(idGuia);
        copiarDatos(formulario, guia);
        return guiaRepository.save(guia);
    }

    /**
     * Elimina una guía junto con sus pasos (cascada definida en la entidad y en la base de datos).
     *
     * @param idGuia id de la guía
     */
    @Transactional
    public void eliminarGuia(Integer idGuia) {
        Guia guia = buscarGuiaPorId(idGuia);
        guiaRepository.delete(guia);
    }

    /**
     * Resumen de las guías para el catálogo de la aplicación móvil.
     *
     * @param idCategoria id de la categoría, o null para todas
     * @return lista de guías en formato de la API
     */
    public List<GuiaResumenDto> listarResumenGuias(Integer idCategoria) {
        return listarGuias(idCategoria).stream()
                .map(GuiaResumenDto::desdeEntidad)
                .toList();
    }

    /**
     * Guía completa con sus pasos para la aplicación móvil.
     * La conversión se hace dentro de la transacción para poder leer los pasos.
     *
     * @param idGuia id de la guía
     * @return guía con pasos en formato de la API
     */
    public GuiaDetalleDto obtenerDetalleGuia(Integer idGuia) {
        return GuiaDetalleDto.desdeEntidad(buscarGuiaPorId(idGuia));
    }

    /** Pasa los datos del formulario a la entidad. La descripción vacía se guarda como NULL. */
    private void copiarDatos(GuiaFormulario formulario, Guia guia) {
        guia.setTitulo(formulario.getTitulo().trim());
        String descripcion = formulario.getDescripcion();
        guia.setDescripcion(descripcion == null || descripcion.isBlank() ? null : descripcion.trim());
        guia.setCategoria(categoriaService.buscarCategoriaPorId(formulario.getIdCategoria()));
    }
}
