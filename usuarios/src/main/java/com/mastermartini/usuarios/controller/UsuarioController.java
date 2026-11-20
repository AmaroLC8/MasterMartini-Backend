package com.mastermartini.usuarios.controller;

import com.mastermartini.usuarios.dto.*;
import com.mastermartini.usuarios.model.Rol;
import com.mastermartini.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Controlador REST que expone los endpoints del microservicio de usuarios bajo /api/usuarios.
// Solo recibe la petición y delega la lógica en UsuarioService.
// @RestController: indica que la clase atiende peticiones HTTP y que lo que retornan los métodos se convierte a JSON.
// @RequestMapping: define la ruta base que comparten todos los endpoints de esta clase.
// @CrossOrigin: permite que clientes de otros orígenes (por ejemplo, un navegador) llamen a la API.
@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Spring entrega el servicio por el constructor (inyección de dependencias).
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Registra un nuevo usuario cliente y retorna sus datos con estado 201 CREATED.
    // @PostMapping: atiende peticiones POST en la ruta indicada.
    // @Valid: valida el cuerpo con las reglas del DTO (@NotBlank, @Email...); si falla responde 400.
    // @RequestBody: convierte el JSON recibido en el objeto RegistroRequest.
    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.registrar(req));
    }

    // Autentica un usuario por correo o RUT y retorna sus datos con estado 200 OK.
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(usuarioService.login(req));
    }

    // Solicita el envío de un código de recuperación de contraseña para el correo indicado.
    // Siempre responde lo mismo, exista o no el correo.
    @PostMapping("/recuperar")
    public ResponseEntity<Map<String, Object>> recuperar(@Valid @RequestBody RecuperarRequest req) {
        usuarioService.recuperar(req);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Si el correo existe, se envió un código de recuperación",
                "estado", 200
        ));
    }

    // Restablece la contraseña del usuario validando el código de recuperación.
    @PostMapping("/restablecer")
    public ResponseEntity<Map<String, Object>> restablecer(@Valid @RequestBody RestablecerRequest req) {
        usuarioService.restablecer(req);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Contraseña actualizada correctamente",
                "estado", 200
        ));
    }

    // Obtiene la información de un usuario específico por su ID.
    // @GetMapping: atiende peticiones GET; "{id}" es una parte variable de la ruta.
    // @PathVariable: toma el valor de {id} desde la URL.
    // @RequestHeader: lee el header X-Usuario-Id, que indica quién hace la petición (para revisar permisos).
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(usuarioService.obtenerPorId(id, solicitanteId));
    }

    // Actualiza los datos del perfil de un usuario existente.
    // @PutMapping: atiende peticiones PUT (modificar un recurso existente).
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPerfilRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(id, req, solicitanteId));
    }

    // Lista todos los usuarios o los filtra opcionalmente por rol (ej: /api/usuarios?rol=CLIENTE).
    // @RequestParam(required = false): lee el parámetro ?rol= de la URL; si no viene, queda en null.
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(
            @RequestParam(required = false) Rol rol,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(usuarioService.listar(rol, solicitanteId));
    }

    // Activa o desactiva el Plan Pro de un usuario.
    @PutMapping("/{id}/plan-pro")
    public ResponseEntity<UsuarioResponse> cambiarPlanPro(
            @PathVariable Long id,
            @Valid @RequestBody PlanProRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(usuarioService.cambiarPlanPro(id, req, solicitanteId));
    }

    // Cambia el rol asignado a un usuario (un administrador no puede cambiar su propio rol).
    @PutMapping("/{id}/rol")
    public ResponseEntity<UsuarioResponse> cambiarRol(
            @PathVariable Long id,
            @Valid @RequestBody CambiarRolRequest req,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        return ResponseEntity.ok(usuarioService.cambiarRol(id, req, solicitanteId));
    }

    // Elimina un usuario y responde 204 NO CONTENT (sin cuerpo). Solo ADMINISTRADOR,
    // y no puede eliminar su propia cuenta.
    // @DeleteMapping: atiende peticiones DELETE (eliminar un recurso).
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable Long id,
            @RequestHeader("X-Usuario-Id") Long solicitanteId) {
        usuarioService.eliminar(id, solicitanteId);
        return ResponseEntity.noContent().build();
    }
}
