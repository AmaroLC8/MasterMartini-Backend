package com.mastermartini.interacciones.dto;

import com.mastermartini.interacciones.model.Inscripcion;

import java.time.LocalDateTime;
import java.util.List;

// Datos de una inscripción que se envían a la app en formato JSON:
// {id, usuarioId, cursoId, cursoTitulo, fechaInscripcion, leccionesCompletadas}.
// La fecha se envía como texto ISO, ej. "2026-11-20T10:30:00".
public record InscripcionResponse(
        Long id,
        Long usuarioId,
        Long cursoId,
        String cursoTitulo,
        LocalDateTime fechaInscripcion,
        List<String> leccionesCompletadas
) {
    // Convierte la entidad Inscripcion en la respuesta que ve la app.
    // La lista se copia a una lista normal: así se lee de la base de datos aquí mismo,
    // dentro de la transacción del servicio (la lista es LAZY).
    public static InscripcionResponse desdeEntidad(Inscripcion i) {
        return new InscripcionResponse(
                i.getId(),
                i.getUsuarioId(),
                i.getCursoId(),
                i.getCursoTitulo(),
                i.getFechaInscripcion(),
                List.copyOf(i.getLeccionesCompletadas())
        );
    }
}
