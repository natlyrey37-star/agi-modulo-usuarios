package co.edu.sena.agi.util;

/**
 * Funciones de apoyo para mostrar texto dentro de las páginas JSP.
 * Escapar los caracteres especiales evita que un dato escrito por el
 * usuario se interprete como código HTML (ataque XSS).
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public final class HtmlUtil {

    private HtmlUtil() {
    }

    /**
     * Reemplaza los caracteres especiales de HTML por sus entidades.
     *
     * @param texto valor a mostrar; si es null devuelve cadena vacía
     * @return texto seguro para insertar en la página
     */
    public static String escapar(Object texto) {
        if (texto == null) {
            return "";
        }
        String valor = texto.toString();
        StringBuilder resultado = new StringBuilder(valor.length());
        for (char caracter : valor.toCharArray()) {
            switch (caracter) {
                case '<' -> resultado.append("&lt;");
                case '>' -> resultado.append("&gt;");
                case '&' -> resultado.append("&amp;");
                case '"' -> resultado.append("&quot;");
                case '\'' -> resultado.append("&#39;");
                default -> resultado.append(caracter);
            }
        }
        return resultado.toString();
    }
}
