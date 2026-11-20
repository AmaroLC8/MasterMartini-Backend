package com.mastermartini.interacciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada del microservicio de interacciones (puerto 9093):
// inscripciones a cursos, progreso por lección y preguntas al instructor.
// @SpringBootApplication: activa la configuración automática de Spring Boot y busca los componentes
// (@RestController, @Service, @Component...) dentro de este paquete y sus subpaquetes.
@SpringBootApplication
public class InteraccionesApplication {

	// Inicia el servidor web embebido y levanta la aplicación.
	public static void main(String[] args) {
		SpringApplication.run(InteraccionesApplication.class, args);
	}

}
