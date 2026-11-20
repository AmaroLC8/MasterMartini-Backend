package com.mastermartini.interacciones.controller;

import com.mastermartini.interacciones.dto.PreguntaRequest;
import com.mastermartini.interacciones.dto.PreguntaResponse;
import com.mastermartini.interacciones.dto.RespuestaRequest;
import com.mastermartini.interacciones.service.PreguntaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST de las preguntas al instructor.
// Usa dos rutas: /api/cursos/{cursoId}/preguntas (preguntas de un curso) y /api/preguntas/... (una pregunta).
// Solo recibe la petición y delega la lógica en PreguntaService.
// @RestController: indica que la clase atiende peticiones HTTP y que lo que retornan los métodos se convierte a JSON.
// @RequestMapping("/api"): ruta base común; cada método completa el resto de la ruta.
// @CrossOrigin: permite que clientes de otros orígenes (por ejemplo, un navegador) llamen a la API.
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PreguntaController {

    private final PreguntaService preguntaService;

    // Spring entrega el servicio por el constructor (inyección de dependencias).
    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    // Lista las preguntas de un curso, de la más nueva a la más antigua. Público (sin header).
    // @GetMapping: atiende peticiones GET.
    // @PathVariable: toma el valor de {cursoId} desde la URL.
    @GetMapping("/cursos/{cursoId}/preguntas")
    public ResponseEntity<List<PreguntaResponse>> listarPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(preguntaService.listarPorCurso(cursoId));
    }

    // Hace una pregunta en un curso, body {"texto": "..."} (solo inscritos), y responde 201 CREATED.
    // @PostMapping: atiende peticiones POST (crear).
    // @Valid: valida el cuerpo con las reglas del DTO; si falla responde 400.
    // @RequestBody: convierte el JSON recibido en el objeto PreguntaRequest.
    // @RequestHeader: lee el header X-Usuario-Id, que indica quién hace la petición.
    @PostMapping("/cursos/{cursoId}/preguntas")
    public ResponseEntity<PreguntaResponse> preguntar(
            @PathVariable Long cursoId,
            @Valid @RequestBody PreguntaRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(preguntaService.preguntar(cursoId, req, solicitanteId));
    }

    // Lista las preguntas sin respuesta (INSTRUCTOR: las de sus cursos; ADMINISTRADOR: todas).
    @GetMapping("/preguntas/pendientes")
    public ResponseEntity<List<PreguntaResponse>> listarPendientes(
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(preguntaService.listarPendientes(solicitanteId));
    }

    // Responde una pregunta, body {"respuesta": "..."} (el instructor del curso o un ADMINISTRADOR).
    // @PutMapping: atiende peticiones PUT (modificar un recurso existente).
    @PutMapping("/preguntas/{id}/respuesta")
    public ResponseEntity<PreguntaResponse> responder(
            @PathVariable Long id,
            @Valid @RequestBody RespuestaRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(preguntaService.responder(id, req, solicitanteId));
    }

    // Borra una pregunta (su autor o un ADMINISTRADOR) y responde 204 NO CONTENT (sin cuerpo).
    // @DeleteMapping: atiende peticiones DELETE (eliminar un recurso).
    @DeleteMapping("/preguntas/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        preguntaService.eliminar(id, solicitanteId);
        return ResponseEntity.noContent().build();
    }
}
