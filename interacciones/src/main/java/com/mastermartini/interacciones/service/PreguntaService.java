package com.mastermartini.interacciones.service;

import com.mastermartini.interacciones.client.CursosClient;
import com.mastermartini.interacciones.client.CursosClient.CursoRemoto;
import com.mastermartini.interacciones.client.UsuariosClient;
import com.mastermartini.interacciones.client.UsuariosClient.UsuarioRemoto;
import com.mastermartini.interacciones.dto.PreguntaRequest;
import com.mastermartini.interacciones.dto.PreguntaResponse;
import com.mastermartini.interacciones.dto.RespuestaRequest;
import com.mastermartini.interacciones.exception.ApiException;
import com.mastermartini.interacciones.model.Pregunta;
import com.mastermartini.interacciones.repository.InscripcionRepository;
import com.mastermartini.interacciones.repository.PreguntaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Lógica de negocio de las preguntas al instructor: ver, preguntar, ver pendientes, responder y borrar.
// Consulta a usuarios (nombre y rol) y a cursos (título e instructor del curso).
// @Service: marca la clase como componente de lógica de negocio; Spring crea una sola instancia
// y la inyecta donde se necesite (por ejemplo, en PreguntaController).
@Service
public class PreguntaService {

    private static final String SIN_PERMISO = "No tienes permiso para realizar esta acción";

    private final PreguntaRepository preguntaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final UsuariosClient usuariosClient;
    private final CursosClient cursosClient;

    // Spring entrega los repositorios y los clientes por el constructor (inyección de dependencias).
    public PreguntaService(PreguntaRepository preguntaRepository, InscripcionRepository inscripcionRepository,
                           UsuariosClient usuariosClient, CursosClient cursosClient) {
        this.preguntaRepository = preguntaRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.usuariosClient = usuariosClient;
        this.cursosClient = cursosClient;
    }

    // Lista las preguntas de un curso, de la más nueva a la más antigua. Público.
    // @Transactional(readOnly = true): transacción de solo lectura.
    @Transactional(readOnly = true)
    public List<PreguntaResponse> listarPorCurso(Long cursoId) {
        return preguntaRepository.findByCursoIdOrderByFechaPreguntaDesc(cursoId).stream()
                .map(PreguntaResponse::desdeEntidad)
                .toList();
    }

    // Crea una pregunta en un curso; solo si quien pregunta está inscrito en ese curso (si no, 403).
    // El nombre del autor se pide a usuarios y el título del curso a cursos, y se guardan en la pregunta.
    // @Transactional: todo el método corre en una sola transacción (todo o nada).
    @Transactional
    public PreguntaResponse preguntar(Long cursoId, PreguntaRequest req, Long solicitanteId) {
        if (!inscripcionRepository.existsByUsuarioIdAndCursoId(solicitanteId, cursoId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Debes inscribirte en el curso para hacer preguntas");
        }
        UsuarioRemoto autor = usuariosClient.obtenerUsuario(solicitanteId);
        CursoRemoto curso = cursosClient.obtenerCurso(cursoId);

        Pregunta nueva = new Pregunta(
                cursoId,
                curso.titulo(),
                solicitanteId,
                autor.nombre(),
                req.texto().trim(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return PreguntaResponse.desdeEntidad(preguntaRepository.save(nueva));
    }

    // Lista las preguntas sin respuesta, de la más antigua a la más nueva.
    // ADMINISTRADOR: todas. INSTRUCTOR: solo las de los cursos donde él es el instructor. Otro rol: 403.
    @Transactional(readOnly = true)
    public List<PreguntaResponse> listarPendientes(Long solicitanteId) {
        UsuarioRemoto usuario = usuariosClient.obtenerUsuario(solicitanteId);

        List<Pregunta> pendientes;
        if (usuario.esAdministrador()) {
            pendientes = preguntaRepository.findByRespuestaIsNullOrderByFechaPreguntaAsc();
        } else if ("INSTRUCTOR".equals(usuario.rol())) {
            // Se pide a cursos la lista completa y se dejan solo los ids de los cursos de este instructor.
            List<Long> misCursos = cursosClient.listarCursos().stream()
                    .filter(c -> solicitanteId.equals(c.instructorId()))
                    .map(CursoRemoto::id)
                    .toList();
            pendientes = misCursos.isEmpty()
                    ? List.of()
                    : preguntaRepository.findByRespuestaIsNullAndCursoIdInOrderByFechaPreguntaAsc(misCursos);
        } else {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }
        return pendientes.stream().map(PreguntaResponse::desdeEntidad).toList();
    }

    // Responde una pregunta; solo el INSTRUCTOR de ese curso o un ADMINISTRADOR.
    // Guarda la respuesta, el nombre de quien respondió y la fecha. Si ya tenía respuesta, la reemplaza.
    @Transactional
    public PreguntaResponse responder(Long id, RespuestaRequest req, Long solicitanteId) {
        Pregunta pregunta = buscarPregunta(id);
        UsuarioRemoto usuario = usuariosClient.obtenerUsuario(solicitanteId);

        if (!usuario.esAdministrador()) {
            boolean esInstructorDelCurso = "INSTRUCTOR".equals(usuario.rol())
                    && solicitanteId.equals(cursosClient.obtenerCurso(pregunta.getCursoId()).instructorId());
            if (!esInstructorDelCurso) {
                throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
            }
        }

        pregunta.setRespuesta(req.respuesta().trim());
        pregunta.setRespondidaPor(usuario.nombre());
        pregunta.setFechaRespuesta(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return PreguntaResponse.desdeEntidad(preguntaRepository.save(pregunta));
    }

    // Borra una pregunta; solo su autor o un ADMINISTRADOR.
    @Transactional
    public void eliminar(Long id, Long solicitanteId) {
        Pregunta pregunta = buscarPregunta(id);
        if (!pregunta.getUsuarioId().equals(solicitanteId)
                && !usuariosClient.obtenerUsuario(solicitanteId).esAdministrador()) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }
        preguntaRepository.delete(pregunta);
    }

    // Busca una pregunta por id o responde 404 si no existe.
    private Pregunta buscarPregunta(Long id) {
        return preguntaRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
    }
}
