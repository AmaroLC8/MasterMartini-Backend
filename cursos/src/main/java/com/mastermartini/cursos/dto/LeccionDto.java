package com.mastermartini.cursos.dto;

import com.mastermartini.cursos.model.Leccion;
import jakarta.validation.constraints.NotBlank;

// Datos de una lección, tal como los lee y envía la app: {"titulo", "descripcion", "duracion", "videoUrl"}.
// Se usa tanto en las respuestas como al crear o editar un curso.
// Es un "record": una clase inmutable cuyos campos coinciden con las claves del JSON.
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
public record LeccionDto(
        @NotBlank(message = "El título de la lección es obligatorio")
        String titulo,

        @NotBlank(message = "La descripción de la lección es obligatoria")
        String descripcion,

        @NotBlank(message = "La duración de la lección es obligatoria")
        String duracion,

        // Opcional: null mientras no se suban los videos a AWS S3.
        String videoUrl
) {
    // Convierte la entidad Leccion en el DTO que se envía a la app.
    public static LeccionDto desdeEntidad(Leccion l) {
        return new LeccionDto(l.getTitulo(), l.getDescripcion(), l.getDuracion(), l.getVideoUrl());
    }

    // Convierte el DTO recibido en una entidad Leccion para guardarla.
    public Leccion aEntidad() {
        return new Leccion(titulo, descripcion, duracion, videoUrl);
    }
}
