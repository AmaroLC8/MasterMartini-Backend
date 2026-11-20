package com.mastermartini.interacciones.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mastermartini.interacciones.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

// COMUNICACIÓN ENTRE MICROSERVICIOS (interacciones -> cursos).
// interacciones no guarda los cursos: viven en el microservicio cursos (puerto 9092).
// Le pregunta por HTTP el título (para guardarlo en la inscripción o pregunta), si es gratis
// (para exigir Plan Pro) y quién es su instructor (para saber quién puede responder preguntas).
//
// Flujo:  interacciones --(GET /api/cursos/4)--> cursos
//         cursos responde {"id": 4, "titulo": "Hojaldre y croissants de vitrina", "esGratis": false, "instructorId": 2, ...}
//
// Los endpoints GET de cursos son públicos, por eso no se envía el header X-Usuario-Id.
// @Component: registra la clase en Spring para poder inyectarla en los servicios.
@Component
public class CursosClient {

    private static final String NO_DISPONIBLE = "El servicio de cursos no está disponible";

    // RestClient es el cliente HTTP de Spring: arma y envía peticiones a otra API y convierte el JSON de respuesta.
    private final RestClient restClient;

    // @Value: lee la propiedad cursos.url de application.properties (por defecto http://localhost:9092).
    // RestClient.create(urlBase) crea el cliente con esa dirección base, así cada llamada solo indica la ruta.
    public CursosClient(@Value("${cursos.url}") String urlBase) {
        this.restClient = RestClient.create(urlBase);
    }

    // Pide al microservicio cursos los datos de un curso.
    // - Si el curso no existe (cursos responde 404) -> 404 "Curso no encontrado".
    // - Si cursos está apagado o no responde       -> 503 "El servicio de cursos no está disponible".
    public CursoRemoto obtenerCurso(Long idCurso) {
        try {
            CursoRemoto curso = restClient.get()
                    .uri("/api/cursos/{id}", idCurso)   // GET {cursos.url}/api/cursos/{id}
                    .retrieve()                          // Envía la petición; lanza excepción si responde 4xx o 5xx
                    .body(CursoRemoto.class);            // Convierte el JSON de respuesta en CursoRemoto

            if (curso == null) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Curso no encontrado");
            }
            return curso;
        } catch (HttpClientErrorException.NotFound ex) {
            // cursos respondió 404: el curso no existe.
            throw new ApiException(HttpStatus.NOT_FOUND, "Curso no encontrado");
        } catch (RestClientException ex) {
            // Conexión rechazada, tiempo agotado u otro error: el microservicio cursos no está disponible.
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, NO_DISPONIBLE);
        }
    }

    // Pide al microservicio cursos la lista de todos los cursos (se usa para saber cuáles son de un instructor).
    // Si cursos está apagado o no responde -> 503 "El servicio de cursos no está disponible".
    public List<CursoRemoto> listarCursos() {
        try {
            List<CursoRemoto> cursos = restClient.get()
                    .uri("/api/cursos")                  // GET {cursos.url}/api/cursos
                    .retrieve()
                    // ParameterizedTypeReference: indica que la respuesta es una LISTA de CursoRemoto
                    // (con List.class a secas Java no sabe el tipo de los elementos).
                    .body(new ParameterizedTypeReference<List<CursoRemoto>>() {});
            return (cursos == null) ? List.of() : cursos;
        } catch (RestClientException ex) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, NO_DISPONIBLE);
        }
    }

    // Solo los datos que interacciones necesita de la respuesta de cursos.
    // @JsonIgnoreProperties(ignoreUnknown = true): ignora los demás campos del JSON (módulos, lecciones, descripción...).
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CursoRemoto(Long id, String titulo, boolean esGratis, Long instructorId) {}
}
