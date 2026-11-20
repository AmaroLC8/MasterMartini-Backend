package com.mastermartini.cursos.dto;

import com.mastermartini.cursos.model.Modulo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

// Datos de un módulo, tal como los lee y envía la app: {"titulo", "lecciones": [...]}.
// Se usa tanto en las respuestas como al crear o editar un curso.
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
// @NotEmpty: la lista no puede venir nula ni vacía (al menos 1 lección).
// @Valid: también valida cada lección de la lista con las reglas de LeccionDto.
public record ModuloDto(
        @NotBlank(message = "El título del módulo es obligatorio")
        String titulo,

        @NotEmpty(message = "Cada módulo debe tener al menos 1 lección")
        @Valid
        List<LeccionDto> lecciones
) {
    // Convierte la entidad Modulo (con sus lecciones) en el DTO que se envía a la app.
    public static ModuloDto desdeEntidad(Modulo m) {
        return new ModuloDto(
                m.getTitulo(),
                m.getLecciones().stream().map(LeccionDto::desdeEntidad).toList()
        );
    }

    // Convierte el DTO recibido en una entidad Modulo (con sus lecciones) para guardarla.
    public Modulo aEntidad() {
        return new Modulo(titulo, lecciones.stream().map(LeccionDto::aEntidad).toList());
    }
}
