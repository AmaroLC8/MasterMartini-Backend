package com.mastermartini.interacciones.controller;

import com.mastermartini.interacciones.dto.InscripcionRequest;
import com.mastermartini.interacciones.dto.InscripcionResponse;
import com.mastermartini.interacciones.dto.ProgresoLeccionRequest;
import com.mastermartini.interacciones.service.InscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST de las inscripciones a cursos y el progreso por lección, bajo /api/inscripciones.
// Todos los endpoints necesitan el header X-Usuario-Id (quién hace la petición).
// Solo recibe la petición y delega la lógica en InscripcionService.
// @RestController: indica que la clase atiende peticiones HTTP y que lo que retornan los métodos se convierte a JSON.
// @RequestMapping: define la ruta base que comparten todos los endpoints de esta clase.
// @CrossOrigin: permite que clientes de otros orígenes (por ejemplo, un navegador) llamen a la API.
@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin(origins = "*")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    // Spring entrega el servicio por el constructor (inyección de dependencias).
    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    // Inscribe al usuario del header en un curso, body {"cursoId": 4}, y responde 201 CREATED.
    // @PostMapping: atiende peticiones POST (crear).
    // @Valid: valida el cuerpo con las reglas del DTO; si falla responde 400.
    // @RequestBody: convierte el JSON recibido en el objeto InscripcionRequest.
    // @RequestHeader: lee el header X-Usuario-Id, que indica quién hace la petición.
    @PostMapping
    public ResponseEntity<InscripcionResponse> inscribir(
            @Valid @RequestBody InscripcionRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inscripcionService.inscribir(req, solicitanteId));
    }

    // Lista las inscripciones de un usuario (solo el mismo usuario o un ADMINISTRADOR).
    // @GetMapping: atiende peticiones GET.
    // @PathVariable: toma el valor de {usuarioId} desde la URL.
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<InscripcionResponse>> listarPorUsuario(
            @PathVariable Long usuarioId,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(inscripcionService.listarPorUsuario(usuarioId, solicitanteId));
    }

    // Marca o desmarca una lección, body {"modulo": 0, "leccion": 2, "completada": true} (solo el dueño).
    // @PutMapping: atiende peticiones PUT (modificar un recurso existente).
    @PutMapping("/{id}/lecciones")
    public ResponseEntity<InscripcionResponse> marcarLeccion(
            @PathVariable Long id,
            @Valid @RequestBody ProgresoLeccionRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(inscripcionService.marcarLeccion(id, req, solicitanteId));
    }

    // Anula una inscripción (el dueño o un ADMINISTRADOR) y responde 204 NO CONTENT (sin cuerpo).
    // @DeleteMapping: atiende peticiones DELETE (eliminar un recurso).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        inscripcionService.eliminar(id, solicitanteId);
        return ResponseEntity.noContent().build();
    }
}
