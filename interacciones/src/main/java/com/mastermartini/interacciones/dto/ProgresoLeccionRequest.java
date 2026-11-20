package com.mastermartini.interacciones.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// Datos para marcar o desmarcar una lección como completada (PUT /api/inscripciones/{id}/lecciones),
// ej: {"modulo": 0, "leccion": 2, "completada": true}. Las posiciones empiezan en 0.
// Se usan Integer y Boolean (con mayúscula) para poder detectar cuando un campo no viene.
// @NotNull: el campo es obligatorio (no puede venir nulo).
// @Min(0): el número debe ser 0 o mayor.
public record ProgresoLeccionRequest(
        @NotNull(message = "El módulo es obligatorio")
        @Min(value = 0, message = "El módulo debe ser 0 o mayor")
        Integer modulo,

        @NotNull(message = "La lección es obligatoria")
        @Min(value = 0, message = "La lección debe ser 0 o mayor")
        Integer leccion,

        @NotNull(message = "Debes indicar si la lección queda completada (true/false)")
        Boolean completada
) {}
