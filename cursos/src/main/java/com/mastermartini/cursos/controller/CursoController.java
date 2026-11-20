package com.mastermartini.cursos.controller;

import com.mastermartini.cursos.dto.CursoRequest;
import com.mastermartini.cursos.dto.CursoResponse;
import com.mastermartini.cursos.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controlador REST que expone los endpoints de los cursos bajo /api/cursos.
// Solo recibe la petición y delega la lógica en CursoService.
// @RestController: indica que la clase atiende peticiones HTTP y que lo que retornan los métodos se convierte a JSON.
// @RequestMapping: define la ruta base que comparten todos los endpoints de esta clase.
// @CrossOrigin: permite que clientes de otros orígenes (por ejemplo, un navegador) llamen a la API.
@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {

    private final CursoService cursoService;

    // Spring entrega el servicio por el constructor (inyección de dependencias).
    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // Lista los cursos. Filtros opcionales y combinables: /api/cursos?categoria=Panadería&gratis=true
    // @GetMapping: atiende peticiones GET.
    // @RequestParam(required = false): lee un parámetro de la URL; si no viene, queda en null.
    @GetMapping
    public ResponseEntity<List<CursoResponse>> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean gratis) {
        return ResponseEntity.ok(cursoService.listar(categoria, gratis));
    }

    // Devuelve el curso completo (con módulos y lecciones). 404 si no existe.
    // @PathVariable: toma el valor de {id} desde la URL.
    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.obtenerPorId(id));
    }

    // Crea un curso (solo INSTRUCTOR o ADMINISTRADOR) y responde 201 CREATED.
    // @PostMapping: atiende peticiones POST (crear).
    // @Valid: valida el cuerpo con las reglas de CursoRequest (y de sus módulos y lecciones); si falla responde 400.
    // @RequestBody: convierte el JSON recibido en el objeto CursoRequest.
    // @RequestHeader: lee el header X-Usuario-Id, que indica quién hace la petición (para revisar permisos).
    @PostMapping
    public ResponseEntity<CursoResponse> crear(
            @Valid @RequestBody CursoRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoService.crear(req, solicitanteId));
    }

    // Edita un curso (solo el INSTRUCTOR dueño o un ADMINISTRADOR) y responde 200 OK.
    // @PutMapping: atiende peticiones PUT (modificar un recurso existente).
    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(cursoService.actualizar(id, req, solicitanteId));
    }

    // Borra un curso (solo ADMINISTRADOR) y responde 204 NO CONTENT (sin cuerpo).
    // @DeleteMapping: atiende peticiones DELETE (eliminar un recurso).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        cursoService.eliminar(id, solicitanteId);
        return ResponseEntity.noContent().build();
    }
}
