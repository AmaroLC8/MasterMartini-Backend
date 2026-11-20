package com.mastermartini.interacciones.dto;

import jakarta.validation.constraints.NotNull;

// Datos para inscribirse en un curso (POST /api/inscripciones), ej: {"cursoId": 4}.
// El usuario se toma del header X-Usuario-Id, no del cuerpo.
// Es un "record": una clase inmutable cuyos campos coinciden con las claves del JSON.
// @NotNull: el campo es obligatorio (no puede venir nulo).
public record InscripcionRequest(
        @NotNull(message = "El curso es obligatorio")
        Long cursoId
) {}
