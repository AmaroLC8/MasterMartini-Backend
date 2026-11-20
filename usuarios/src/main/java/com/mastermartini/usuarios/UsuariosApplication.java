package com.mastermartini.usuarios;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del microservicio de usuarios (puerto 9091).
// @SpringBootApplication: activa la configuración automática de Spring Boot y busca los componentes
// (@RestController, @Service, @Component, @Configuration...) dentro de este paquete y sus subpaquetes.
@SpringBootApplication
public class UsuariosApplication {

	// Inicia el servidor web embebido y levanta la aplicación.
	public static void main(String[] args) {
		SpringApplication.run(UsuariosApplication.class, args);
	}

}
