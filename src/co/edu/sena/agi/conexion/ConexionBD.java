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
 * Los datos de acceso se leen de un archivo db.properties para no
 * dejar usuario ni clave escritos dentro del código fuente:
 * <ul>
 *   <li>En la aplicación web se busca en el classpath (WEB-INF/classes/db.properties).</li>
 *   <li>En la aplicación de consola se lee desde config/db.properties.</li>
 * </ul>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 2.0
 */
public final class ConexionBD {

    /** Nombre del archivo cuando se busca dentro del classpath (aplicación web). */
    private static final String RECURSO_CONFIGURACION = "db.properties";

    /** Ruta del archivo de configuración, relativa a la carpeta del proyecto (consola). */
    private static final String RUTA_CONFIGURACION = "config/db.properties";

    /** Driver de MySQL Connector/J; se carga de forma explícita para Tomcat. */
    private static final String DRIVER_POR_DEFECTO = "com.mysql.cj.jdbc.Driver";

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
    public static synchronized Connection obtenerConexion() throws SQLException {
        if (urlConexion == null) {
            cargarConfiguracion();
        }
        return DriverManager.getConnection(urlConexion, usuarioBd, claveBd);
    }

    /**
     * Lee la URL, el usuario y la clave desde db.properties y registra el driver.
     */
    private static void cargarConfiguracion() throws SQLException {
        Properties propiedades = new Properties();
        try (InputStream archivo = abrirArchivoConfiguracion()) {
            propiedades.load(archivo);
        } catch (IOException e) {
            throw new SQLException("No se encontró db.properties. Copie config/db.properties.ejemplo "
                    + "como config/db.properties y escriba su clave.", e);
        }
        cargarDriver(propiedades.getProperty("db.driver", DRIVER_POR_DEFECTO));
        urlConexion = propiedades.getProperty("db.url");
        usuarioBd = propiedades.getProperty("db.usuario");
        claveBd = propiedades.getProperty("db.clave");
    }

    /** Busca primero en el classpath (web) y luego en la carpeta config (consola). */
    private static InputStream abrirArchivoConfiguracion() throws IOException {
        InputStream recurso = ConexionBD.class.getClassLoader().getResourceAsStream(RECURSO_CONFIGURACION);
        if (recurso != null) {
            return recurso;
        }
        return new FileInputStream(RUTA_CONFIGURACION);
    }

    /**
     * Registra la clase del driver. En Tomcat el driver está en WEB-INF/lib
     * y no siempre se registra solo, por eso se carga explícitamente.
     */
    private static void cargarDriver(String claseDriver) {
        try {
            Class.forName(claseDriver);
        } catch (ClassNotFoundException e) {
            // Si no está, DriverManager intentará encontrar un driver por su cuenta
            System.out.println("Aviso: no se encontró el driver " + claseDriver);
        }
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
