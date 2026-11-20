package com.mastermartini.interacciones.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mastermartini.interacciones.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// COMUNICACIÓN ENTRE MICROSERVICIOS (interacciones -> usuarios).
// interacciones no tiene la tabla de usuarios: esa información vive en el microservicio usuarios
// (puerto 9091), con su propia base de datos. Por eso le PREGUNTA por HTTP los datos de una persona:
// su nombre (para firmar preguntas y respuestas), su rol (para los permisos) y si tiene Plan Pro
// (para inscribirse en cursos pagados).
//
// Flujo:  interacciones --(GET /api/usuarios/1, X-Usuario-Id: 1)--> usuarios
//         usuarios responde {"id": 1, "nombre": "Carlos Mendoza", "rol": "CLIENTE", "planPro": false, ...}
//
// @Component: registra la clase en Spring para poder inyectarla en los servicios.
@Component
public class UsuariosClient {

    private static final String SIN_PERMISO = "No tienes permiso para realizar esta acción";

    // RestClient es el cliente HTTP de Spring: arma y envía peticiones a otra API y convierte el JSON de respuesta.
    private final RestClient restClient;

    // @Value: lee la propiedad usuarios.url de application.properties (por defecto http://localhost:9091).
    // RestClient.create(urlBase) crea el cliente con esa dirección base, así cada llamada solo indica la ruta.
    public UsuariosClient(@Value("${usuarios.url}") String urlBase) {
        this.restClient = RestClient.create(urlBase);
    }

    // Pide al microservicio usuarios los datos de un usuario.
    // Se envía el header X-Usuario-Id con el mismo id porque usuarios permite que cada uno consulte sus propios datos.
    // - Si el usuario no existe (usuarios responde 404) -> 403 "No tienes permiso para realizar esta acción".
    // - Si usuarios está apagado o no responde       -> 503 "El servicio de usuarios no está disponible".
    public UsuarioRemoto obtenerUsuario(Long idUsuario) {
        try {
            UsuarioRemoto usuario = restClient.get()
                    .uri("/api/usuarios/{id}", idUsuario)          // GET {usuarios.url}/api/usuarios/{id}
                    .header("X-Usuario-Id", String.valueOf(idUsuario))
                    .retrieve()                                     // Envía la petición; lanza excepción si responde 4xx o 5xx
                    .body(UsuarioRemoto.class);                     // Convierte el JSON de respuesta en UsuarioRemoto

            if (usuario == null || usuario.rol() == null) {
                throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
            }
            return usuario;
        } catch (HttpClientErrorException ex) {
            // usuarios respondió un error 4xx: normalmente 404 porque el usuario no existe.
            // Sin un usuario válido no se da permiso.
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        } catch (RestClientException ex) {
            // Conexión rechazada, tiempo agotado o error 5xx: el microservicio usuarios no está disponible.
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "El servicio de usuarios no está disponible");
        }
    }

    // Solo los datos que interacciones necesita de la respuesta de usuarios.
    // rol: "CLIENTE", "INSTRUCTOR", "VENTAS" o "ADMINISTRADOR".
    // @JsonIgnoreProperties(ignoreUnknown = true): ignora los demás campos del JSON (correo, rut, teléfono...).
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UsuarioRemoto(Long id, String nombre, String rol, boolean planPro) {

        // true si el usuario es ADMINISTRADOR.
        public boolean esAdministrador() {
            return "ADMINISTRADOR".equals(rol);
        }
    }
}
