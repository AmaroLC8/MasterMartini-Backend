package com.mastermartini.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// Configuración del cifrado de contraseñas. Solo se usa el módulo "crypto" de Spring Security
// (no hay login con sesiones ni tokens: los permisos se revisan con el header X-Usuario-Id).
// @Configuration: indica que la clase define objetos (beans) que Spring debe crear al iniciar.
@Configuration
public class SecurityConfig {

    // Provee el codificador BCrypt para encriptar y verificar contraseñas.
    // @Bean: el objeto que retorna este método queda registrado en Spring y se puede inyectar
    // en otras clases (UsuarioService y DataSeeder lo reciben por constructor).
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
