package com.mastermartini.interacciones.service;

import com.mastermartini.interacciones.client.CursosClient;
import com.mastermartini.interacciones.client.CursosClient.CursoRemoto;
import com.mastermartini.interacciones.client.UsuariosClient;
import com.mastermartini.interacciones.client.UsuariosClient.UsuarioRemoto;
import com.mastermartini.interacciones.dto.InscripcionRequest;
import com.mastermartini.interacciones.dto.InscripcionResponse;
import com.mastermartini.interacciones.dto.ProgresoLeccionRequest;
import com.mastermartini.interacciones.exception.ApiException;
import com.mastermartini.interacciones.model.Inscripcion;
import com.mastermartini.interacciones.repository.InscripcionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Lógica de negocio de las inscripciones: inscribirse, ver mis cursos, marcar progreso y anular.
// Consulta a los microservicios cursos y usuarios para validar el curso, el Plan Pro y los permisos.
// @Service: marca la clase como componente de lógica de negocio; Spring crea una sola instancia
// y la inyecta donde se necesite (por ejemplo, en InscripcionController).
@Service
public class InscripcionService {

    private static final String SIN_PERMISO = "No tienes permiso para realizar esta acción";

    private final InscripcionRepository inscripcionRepository;
    private final UsuariosClient usuariosClient;
    private final CursosClient cursosClient;

    // Spring entrega el repositorio y los clientes por el constructor (inyección de dependencias).
    public InscripcionService(InscripcionRepository inscripcionRepository,
                              UsuariosClient usuariosClient, CursosClient cursosClient) {
        this.inscripcionRepository = inscripcionRepository;
        this.usuariosClient = usuariosClient;
        this.cursosClient = cursosClient;
    }

    // Inscribe al usuario en un curso.
    // 404 si el curso no existe; 403 si el curso es del Plan Pro y el usuario no lo tiene; 409 si ya está inscrito.
    // @Transactional: todo el método corre en una sola transacción (todo o nada).
    @Transactional
    public InscripcionResponse inscribir(InscripcionRequest req, Long solicitanteId) {
        CursoRemoto curso = cursosClient.obtenerCurso(req.cursoId());
        UsuarioRemoto usuario = usuariosClient.obtenerUsuario(solicitanteId);

        if (!curso.esGratis() && !usuario.planPro()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Necesitas el Plan Pro para este curso");
        }
        if (inscripcionRepository.existsByUsuarioIdAndCursoId(solicitanteId, curso.id())) {
            throw new ApiException(HttpStatus.CONFLICT, "Ya estás inscrito en este curso");
        }

        Inscripcion nueva = new Inscripcion(
                solicitanteId,
                curso.id(),
                curso.titulo(), // Se guarda el título para mostrarlo sin volver a llamar a cursos
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return InscripcionResponse.desdeEntidad(inscripcionRepository.save(nueva));
    }

    // Lista las inscripciones de un usuario; solo el mismo usuario o un ADMINISTRADOR.
    // @Transactional(readOnly = true): transacción de solo lectura; permite leer la lista LAZY
    // de lecciones completadas mientras se arma el DTO.
    @Transactional(readOnly = true)
    public List<InscripcionResponse> listarPorUsuario(Long usuarioId, Long solicitanteId) {
        if (!usuarioId.equals(solicitanteId)) {
            verificarAdministrador(solicitanteId);
        }
        return inscripcionRepository.findByUsuarioIdOrderByFechaInscripcionDesc(usuarioId).stream()
                .map(InscripcionResponse::desdeEntidad)
                .toList();
    }

    // Marca o desmarca una lección como completada; solo el dueño de la inscripción.
    // La lección se guarda como "modulo-leccion", ej. {"modulo": 0, "leccion": 2} -> "0-2".
    @Transactional
    public InscripcionResponse marcarLeccion(Long id, ProgresoLeccionRequest req, Long solicitanteId) {
        Inscripcion inscripcion = buscarInscripcion(id);
        if (!inscripcion.getUsuarioId().equals(solicitanteId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }

        String clave = req.modulo() + "-" + req.leccion();
        List<String> completadas = inscripcion.getLeccionesCompletadas();
        if (req.completada()) {
            if (!completadas.contains(clave)) { // No se repite si ya estaba marcada
                completadas.add(clave);
            }
        } else {
            completadas.remove(clave);
        }
        return InscripcionResponse.desdeEntidad(inscripcionRepository.save(inscripcion));
    }

    // Anula una inscripción (con su progreso); solo el dueño o un ADMINISTRADOR.
    @Transactional
    public void eliminar(Long id, Long solicitanteId) {
        Inscripcion inscripcion = buscarInscripcion(id);
        if (!inscripcion.getUsuarioId().equals(solicitanteId)) {
            verificarAdministrador(solicitanteId);
        }
        inscripcionRepository.delete(inscripcion);
    }

    // Busca una inscripción por id o responde 404 si no existe.
    private Inscripcion buscarInscripcion(Long id) {
        return inscripcionRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));
    }

    // Pregunta a usuarios el rol de quien hace la petición; si no es ADMINISTRADOR responde 403.
    private void verificarAdministrador(Long solicitanteId) {
        if (!usuariosClient.obtenerUsuario(solicitanteId).esAdministrador()) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }
    }
}
