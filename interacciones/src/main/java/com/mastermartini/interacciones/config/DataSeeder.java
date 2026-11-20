package com.mastermartini.interacciones.config;

import com.mastermartini.interacciones.model.Inscripcion;
import com.mastermartini.interacciones.model.Pregunta;
import com.mastermartini.interacciones.repository.InscripcionRepository;
import com.mastermartini.interacciones.repository.PreguntaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

// DATOS DE PRUEBA.
// Carga una inscripción y dos preguntas de ejemplo si las tablas están vacías:
// - El cliente Carlos Mendoza (usuarioId 1) inscrito en el curso 1, con las lecciones "0-0" y "0-1" completadas.
// - Dos preguntas suyas en el curso 1: una ya respondida por la instructora y otra pendiente.
// El título del curso y el nombre del autor se escriben directamente (sin llamar a cursos ni a usuarios),
// para que el seeder funcione aunque esos microservicios estén apagados.
// @Component: registra la clase en Spring para que la cree e inyecte sus dependencias.
// Al implementar CommandLineRunner, Spring ejecuta el método run() una vez, apenas arranca la aplicación.
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Long CLIENTE_ID = 1L;   // cliente@example.com en el microservicio usuarios
    private static final Long CURSO_ID = 1L;     // primer curso del microservicio cursos
    private static final String CURSO_TITULO = "Chocolatería desde cero: templado y bombones";
    private static final String AUTOR_NOMBRE = "Carlos Mendoza";

    private final InscripcionRepository inscripcionRepository;
    private final PreguntaRepository preguntaRepository;

    // Spring entrega los repositorios por el constructor (inyección de dependencias).
    public DataSeeder(InscripcionRepository inscripcionRepository, PreguntaRepository preguntaRepository) {
        this.inscripcionRepository = inscripcionRepository;
        this.preguntaRepository = preguntaRepository;
    }

    // Inserta los datos de prueba; cada tabla se revisa por separado para no duplicar al reiniciar.
    // @Override: indica que este método implementa el run() de CommandLineRunner.
    @Override
    public void run(String... args) {
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        if (inscripcionRepository.count() == 0) {
            Inscripcion inscripcion = new Inscripcion(CLIENTE_ID, CURSO_ID, CURSO_TITULO, ahora.minusDays(7));
            // Completó las dos primeras lecciones del módulo 1 ("Fundamentos del chocolate").
            inscripcion.getLeccionesCompletadas().addAll(List.of("0-0", "0-1"));
            inscripcionRepository.save(inscripcion);
        }

        if (preguntaRepository.count() == 0) {
            // Pregunta ya respondida por la instructora del curso.
            Pregunta respondida = new Pregunta(CURSO_ID, CURSO_TITULO, CLIENTE_ID, AUTOR_NOMBRE,
                    "Hice el método de la tabla, pero al día siguiente los bombones salieron opacos y con "
                            + "vetas blancas. ¿Qué pude haber hecho mal?",
                    ahora.minusDays(5));
            respondida.setRespuesta("Hola Carlos: las vetas blancas casi siempre indican que el chocolate perdió "
                    + "el templado. Revisa que al volver a juntarlo en el bol no pase de 31 a 32 °C (si es "
                    + "chocolate amargo) y haz siempre la prueba del papel antes de moldear. Además, guarda los "
                    + "bombones entre 15 y 18 °C, lejos de la humedad: si van al refrigerador también pueden "
                    + "salir manchados.");
            respondida.setRespondidaPor("Chef Elena Quiroga");
            respondida.setFechaRespuesta(ahora.minusDays(4));

            // Pregunta todavía sin respuesta (aparece en "pendientes").
            Pregunta pendiente = new Pregunta(CURSO_ID, CURSO_TITULO, CLIENTE_ID, AUTOR_NOMBRE,
                    "Mientras moldeo los bombones el chocolate templado se me empieza a espesar. ¿Puedo "
                            + "recalentarlo unos segundos en el microondas sin perder el templado?",
                    ahora.minusDays(1));

            preguntaRepository.saveAll(List.of(respondida, pendiente));
        }
    }
}
