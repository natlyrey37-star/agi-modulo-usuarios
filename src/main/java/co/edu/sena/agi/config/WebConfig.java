package co.edu.sena.agi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC propia de AGI.
 * <p>
 * Rutas públicas: /login, /registro, /app (cliente móvil), /api (consultas) y /css.
 * Rutas protegidas: el panel y la administración de usuarios y guías.
 * </p>
 *
 * @author Andrea Nathaly Rey Castiblanco
 * @version 3.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Registra el interceptor de sesión sobre las rutas de administración.
     *
     * @param registry registro de interceptores de Spring MVC
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SesionInterceptor())
                .addPathPatterns("/panel", "/usuarios", "/usuarios/**", "/guias", "/guias/**");
    }
}
