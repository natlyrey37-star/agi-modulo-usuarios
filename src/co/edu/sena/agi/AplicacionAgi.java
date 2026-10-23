package co.edu.sena.agi;

import co.edu.sena.agi.conexion.ConexionBD;
import co.edu.sena.agi.vista.MenuUsuario;

/**
 * Punto de entrada del módulo de gestión de usuarios del proyecto
 * AGI - Asistente Guía Interactivo.
 * Verifica la conexión JDBC y abre el menú del CRUD de usuarios.
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 1.0
 */
public class AplicacionAgi {

    public static void main(String[] args) {
        System.out.println("AGI - Asistente Guía Interactivo");
        System.out.println("Módulo de gestión de usuarios (JDBC + MySQL)");

        if (!ConexionBD.probarConexion()) {
            System.out.println("Revise que MySQL esté encendido y que config/db.properties tenga la clave correcta.");
            return;
        }
        System.out.println("Conexión exitosa con la base de datos agi.");

        new MenuUsuario().iniciar();
    }
}
