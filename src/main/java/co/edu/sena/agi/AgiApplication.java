package co.edu.sena.agi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de AGI - Asistente Guía Interactivo.
 * <p>
 * La anotación {@code @SpringBootApplication} activa la configuración automática
 * de Spring Boot y el escaneo de componentes del paquete {@code co.edu.sena.agi}.
 * Al ejecutar este método se inicia el servidor Tomcat embebido, por lo que la
 * aplicación funciona de forma independiente (stand-alone), sin instalar Tomcat aparte.
 * </p>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@SpringBootApplication
public class AgiApplication {

    /**
     * Inicia la aplicación.
     *
     * @param args argumentos de la línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(AgiApplication.class, args);
    }
}
