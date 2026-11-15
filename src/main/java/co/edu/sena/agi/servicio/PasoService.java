package co.edu.sena.agi.servicio;

import co.edu.sena.agi.dto.PasoFormulario;
import co.edu.sena.agi.excepcion.DatoDuplicadoException;
import co.edu.sena.agi.excepcion.RecursoNoEncontradoException;
import co.edu.sena.agi.modelo.Guia;
import co.edu.sena.agi.modelo.Paso;
import co.edu.sena.agi.repositorio.PasoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reglas de negocio de los pasos de cada guía.
 * Regla propia del módulo: dentro de una misma guía no se repite el número de paso.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Service
@Transactional(readOnly = true)
public class PasoService {

    private final PasoRepository pasoRepository;
    private final GuiaService guiaService;

    /**
     * @param pasoRepository acceso a la tabla paso
     * @param guiaService    consulta de la guía a la que pertenece el paso
     */
    public PasoService(PasoRepository pasoRepository, GuiaService guiaService) {
        this.pasoRepository = pasoRepository;
        this.guiaService = guiaService;
    }

    /**
     * Busca un paso y verifica que pertenezca a la guía de la URL.
     *
     * @param idGuia id de la guía
     * @param idPaso id del paso
     * @return paso encontrado
     */
    public Paso buscarPaso(Integer idGuia, Integer idPaso) {
        return pasoRepository.findByIdPasoAndGuiaIdGuia(idPaso, idGuia)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "El paso solicitado no existe en esta guía."));
    }

    /**
     * Propone el número del siguiente paso: el mayor número actual más uno.
     *
     * @param guia guía con sus pasos
     * @return número sugerido para el formulario
     */
    public int sugerirNumeroPaso(Guia guia) {
        return guia.getPasos().stream()
                .mapToInt(Paso::getNumeroPaso)
                .max()
                .orElse(0) + 1;
    }

    /**
     * @return cantidad total de pasos registrados (para el panel)
     */
    public long contarPasos() {
        return pasoRepository.count();
    }

    /**
     * Agrega un paso a una guía.
     *
     * @param idGuia     id de la guía
     * @param formulario datos ya validados
     * @return paso guardado
     * @throws DatoDuplicadoException si la guía ya tiene un paso con ese número
     */
    @Transactional
    public Paso registrarPaso(Integer idGuia, PasoFormulario formulario) {
        Guia guia = guiaService.buscarGuiaPorId(idGuia);
        if (pasoRepository.existsByGuiaIdGuiaAndNumeroPaso(idGuia, formulario.getNumeroPaso())) {
            throw numeroRepetido(formulario.getNumeroPaso());
        }

        Paso paso = new Paso();
        copiarDatos(formulario, paso);
        guia.agregarPaso(paso);
        return pasoRepository.save(paso);
    }

    /**
     * Actualiza un paso existente.
     *
     * @param idGuia     id de la guía
     * @param idPaso     id del paso
     * @param formulario datos ya validados
     * @return paso actualizado
     */
    @Transactional
    public Paso actualizarPaso(Integer idGuia, Integer idPaso, PasoFormulario formulario) {
        Paso paso = buscarPaso(idGuia, idPaso);
        if (pasoRepository.existsByGuiaIdGuiaAndNumeroPasoAndIdPasoNot(idGuia, formulario.getNumeroPaso(), idPaso)) {
            throw numeroRepetido(formulario.getNumeroPaso());
        }

        copiarDatos(formulario, paso);
        return pasoRepository.save(paso);
    }

    /**
     * Elimina un paso de la guía.
     *
     * @param idGuia id de la guía
     * @param idPaso id del paso
     */
    @Transactional
    public void eliminarPaso(Integer idGuia, Integer idPaso) {
        Paso paso = buscarPaso(idGuia, idPaso);
        // Se retira también de la lista de la guía para que la colección quede al día
        paso.getGuia().getPasos().remove(paso);
        pasoRepository.delete(paso);
    }

    /** Pasa los datos del formulario a la entidad, sin espacios sobrantes. */
    private void copiarDatos(PasoFormulario formulario, Paso paso) {
        paso.setNumeroPaso(formulario.getNumeroPaso());
        paso.setTituloPaso(formulario.getTituloPaso().trim());
        paso.setInstruccion(formulario.getInstruccion().trim());
    }

    /** Crea el error que se muestra junto al campo número del paso. */
    private DatoDuplicadoException numeroRepetido(Integer numeroPaso) {
        return new DatoDuplicadoException("numeroPaso",
                "Esta guía ya tiene un paso con el número " + numeroPaso + ".");
    }
}
