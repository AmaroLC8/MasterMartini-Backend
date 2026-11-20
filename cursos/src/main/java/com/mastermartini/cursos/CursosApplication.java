package com.mastermartini.cursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del microservicio de cursos (puerto 9092).
// @SpringBootApplication: activa la configuración automática de Spring Boot y busca los componentes
// (@RestController, @Service, @Component...) dentro de este paquete y sus subpaquetes.
@SpringBootApplication
public class CursosApplication {

	// Inicia el servidor web embebido y levanta la aplicación.
	public static void main(String[] args) {
		SpringApplication.run(CursosApplication.class, args);
	}

}
