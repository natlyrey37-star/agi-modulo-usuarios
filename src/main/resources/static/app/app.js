/* =============================================================
   AGI - Cliente móvil
   Consume la API REST del módulo Spring Boot (/agi/api) y muestra
   las guías paso a paso. No usa librerías externas: solo fetch y DOM.
   ============================================================= */

// Ruta de la API relativa a esta página (/agi/app/ -> /agi/api)
const URL_API = '../api';

// Estado de la aplicación
let categorias = [];
let categoriaSeleccionada = null;
let guiaActual = null;
let indicePasoActual = 0;

// Elementos de la página que se usan varias veces
const pantallaCatalogo = document.getElementById('pantalla-catalogo');
const pantallaGuia = document.getElementById('pantalla-guia');
const botonVolver = document.getElementById('boton-volver');
const botonAnterior = document.getElementById('boton-anterior');
const botonSiguiente = document.getElementById('boton-siguiente');
const botonTerminar = document.getElementById('boton-terminar');
const navegacionPasos = document.getElementById('navegacion-pasos');
const mensajeEstado = document.getElementById('mensaje-estado');

/**
 * Hace una petición GET a la API y devuelve el JSON de la respuesta.
 * @param {string} ruta - ruta después de /api, por ejemplo '/guias'
 * @returns {Promise<any>} datos recibidos
 */
async function consultarApi(ruta) {
  const respuesta = await fetch(URL_API + ruta, { headers: { Accept: 'application/json' } });
  if (!respuesta.ok) {
    throw new Error('La API respondió con el código ' + respuesta.status);
  }
  return respuesta.json();
}

/**
 * Crea un elemento HTML con su clase y su texto.
 * Se usa textContent (no innerHTML) para que ningún dato se interprete como código.
 */
function crearElemento(etiqueta, clase, texto) {
  const elemento = document.createElement(etiqueta);
  if (clase) {
    elemento.className = clase;
  }
  if (texto) {
    elemento.textContent = texto;
  }
  return elemento;
}

/** Pone en mayúscula la primera letra (las categorías se guardan en minúscula). */
function capitalizar(texto) {
  return texto ? texto.charAt(0).toUpperCase() + texto.slice(1) : '';
}

/** Muestra un mensaje debajo de la pantalla; si esError es true se pinta en rojo. */
function mostrarMensaje(texto, esError) {
  mensajeEstado.textContent = texto;
  mensajeEstado.classList.toggle('mensaje-estado-error', Boolean(esError));
}

/** Cambia entre la pantalla del catálogo y la de la guía. */
function mostrarPantalla(nombrePantalla) {
  const esCatalogo = nombrePantalla === 'catalogo';
  pantallaCatalogo.hidden = !esCatalogo;
  pantallaGuia.hidden = esCatalogo;
  botonVolver.hidden = esCatalogo;
  window.scrollTo(0, 0);
}

// -------------------------------------------------------------
// Catálogo de guías
// -------------------------------------------------------------

/** Dibuja los botones de categoría: "Todas" y uno por cada categoría de la API. */
function dibujarCategorias() {
  const contenedor = document.getElementById('filtro-categorias');
  contenedor.replaceChildren();

  const opciones = [{ idCategoria: null, nombreCategoria: 'todas' }, ...categorias];
  opciones.forEach((categoria) => {
    const boton = crearElemento('button', 'boton-categoria', capitalizar(categoria.nombreCategoria));
    boton.type = 'button';
    boton.classList.toggle('boton-categoria-activa', categoria.idCategoria === categoriaSeleccionada);
    boton.addEventListener('click', () => seleccionarCategoria(categoria.idCategoria));
    contenedor.appendChild(boton);
  });
}

/** Marca la categoría elegida y vuelve a consultar las guías. */
async function seleccionarCategoria(idCategoria) {
  categoriaSeleccionada = idCategoria;
  dibujarCategorias();
  try {
    await cargarGuias();
  } catch (error) {
    mostrarMensaje('No fue posible cargar las guías. Intente de nuevo.', true);
  }
}

/** Consulta las guías (todas o las de la categoría elegida) y dibuja una tarjeta por cada una. */
async function cargarGuias() {
  const ruta = categoriaSeleccionada === null ? '/guias' : '/guias?categoria=' + categoriaSeleccionada;
  const guias = await consultarApi(ruta);
  const contenedor = document.getElementById('lista-guias');
  contenedor.replaceChildren();

  if (guias.length === 0) {
    mostrarMensaje('No hay guías en esta categoría.', false);
    return;
  }
  mostrarMensaje('', false);

  guias.forEach((guia) => {
    const tarjeta = crearElemento('article', 'tarjeta-guia');
    tarjeta.appendChild(crearElemento('p', 'tarjeta-guia-categoria', capitalizar(guia.categoria)));
    tarjeta.appendChild(crearElemento('h2', 'tarjeta-guia-titulo', guia.titulo));
    tarjeta.appendChild(crearElemento('p', 'tarjeta-guia-descripcion', guia.descripcion || ''));

    const textoBoton = guia.totalPasos === 1 ? 'Empezar (1 paso)' : 'Empezar (' + guia.totalPasos + ' pasos)';
    const botonEmpezar = crearElemento('button', 'boton-app', textoBoton);
    botonEmpezar.type = 'button';
    botonEmpezar.disabled = guia.totalPasos === 0;
    botonEmpezar.addEventListener('click', () => abrirGuia(guia.idGuia));
    tarjeta.appendChild(botonEmpezar);

    contenedor.appendChild(tarjeta);
  });
}

// -------------------------------------------------------------
// Recorrido de una guía
// -------------------------------------------------------------

/** Trae la guía con sus pasos y muestra el primero. */
async function abrirGuia(idGuia) {
  try {
    guiaActual = await consultarApi('/guias/' + idGuia);
    indicePasoActual = 0;
    document.getElementById('guia-titulo').textContent = guiaActual.titulo;
    mostrarMensaje('', false);
    mostrarPantalla('guia');
    mostrarPaso();
  } catch (error) {
    mostrarMensaje('No fue posible abrir la guía. Intente de nuevo.', true);
  }
}

/** Muestra el paso actual, actualiza la barra de progreso y los botones. */
function mostrarPaso() {
  const totalPasos = guiaActual.pasos.length;
  const paso = guiaActual.pasos[indicePasoActual];
  const esUltimoPaso = indicePasoActual === totalPasos - 1;

  document.getElementById('indicador-paso').textContent = 'Paso ' + (indicePasoActual + 1) + ' de ' + totalPasos;
  document.getElementById('paso-titulo').textContent = paso.tituloPaso;
  document.getElementById('paso-instruccion').textContent = paso.instruccion;
  document.getElementById('barra-progreso').style.width = ((indicePasoActual + 1) / totalPasos) * 100 + '%';

  botonAnterior.disabled = indicePasoActual === 0;
  botonSiguiente.textContent = esUltimoPaso ? 'Terminar' : 'Siguiente';
  navegacionPasos.hidden = false;
  botonTerminar.hidden = true;
}

/** Avanza al siguiente paso o, si era el último, felicita a la persona. */
function avanzarPaso() {
  if (indicePasoActual < guiaActual.pasos.length - 1) {
    indicePasoActual++;
    mostrarPaso();
  } else {
    mostrarFelicitacion();
  }
}

/** Regresa al paso anterior. */
function retrocederPaso() {
  if (indicePasoActual > 0) {
    indicePasoActual--;
    mostrarPaso();
  }
}

/** Pantalla final de la guía. */
function mostrarFelicitacion() {
  document.getElementById('indicador-paso').textContent = 'Guía terminada';
  document.getElementById('barra-progreso').style.width = '100%';
  document.getElementById('paso-titulo').textContent = '¡Lo logró!';
  document.getElementById('paso-instruccion').textContent =
    'Completó la guía "' + guiaActual.titulo + '". Puede repetirla cuando quiera.';
  navegacionPasos.hidden = true;
  botonTerminar.hidden = false;
}

/** Vuelve al catálogo. */
function volverAlCatalogo() {
  guiaActual = null;
  mostrarPantalla('catalogo');
}

/** Arranque: conecta los botones y carga el catálogo desde la API. */
async function iniciarAplicacion() {
  botonSiguiente.addEventListener('click', avanzarPaso);
  botonAnterior.addEventListener('click', retrocederPaso);
  botonVolver.addEventListener('click', volverAlCatalogo);
  botonTerminar.addEventListener('click', volverAlCatalogo);

  try {
    categorias = await consultarApi('/categorias');
    dibujarCategorias();
    await cargarGuias();
  } catch (error) {
    mostrarMensaje('No fue posible conectarse con el servidor de AGI.', true);
  }
}

document.addEventListener('DOMContentLoaded', iniciarAplicacion);
