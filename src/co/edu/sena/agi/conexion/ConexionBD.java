package co.edu.sena.agi.conexion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Administra la conexión JDBC con la base de datos "agi" en MySQL.
 * Los datos de acceso se leen del archivo config/db.properties para no
 * dejar usuario ni clave escritos dentro del código fuente.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public final class ConexionBD {

    /** Ruta del archivo de configuración, relativa a la carpeta del proyecto. */
    private static final String RUTA_CONFIGURACION = "config/db.properties";

    private static String urlConexion;
    private static String usuarioBd;
    private static String claveBd;

    /** Clase de utilidad: no se instancia. */
    private ConexionBD() {
    }

    /**
     * Abre una nueva conexión con la base de datos.
     * Quien la recibe debe cerrarla (se recomienda try-with-resources).
     *
     * @return conexión activa con la base de datos agi
     * @throws SQLException si el servidor no responde o las credenciales fallan
     */
    public static Connection obtenerConexion() throws SQLException {
        if (urlConexion == null) {
            cargarConfiguracion();
        }
        return DriverManager.getConnection(urlConexion, usuarioBd, claveBd);
    }

    /**
     * Lee la URL, el usuario y la clave desde el archivo de propiedades.
     */
    private static void cargarConfiguracion() throws SQLException {
        Properties propiedades = new Properties();
        try (InputStream archivo = new FileInputStream(RUTA_CONFIGURACION)) {
            propiedades.load(archivo);
        } catch (IOException e) {
            throw new SQLException("No se encontró " + RUTA_CONFIGURACION
                    + ". Copie config/db.properties.ejemplo con ese nombre y escriba su clave.", e);
        }
        urlConexion = propiedades.getProperty("db.url");
        usuarioBd = propiedades.getProperty("db.usuario");
        claveBd = propiedades.getProperty("db.clave");
    }

    /**
     * Prueba rápida de la conexión: indica si el servidor responde.
     *
     * @return true si la conexión se abre correctamente
     */
    public static boolean probarConexion() {
        try (Connection conexion = obtenerConexion()) {
            return conexion.isValid(2);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}
