package com.mastermartini.interacciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Datos para hacer una pregunta al instructor (POST /api/cursos/{cursoId}/preguntas), ej: {"texto": "..."}.
// El curso viene en la URL y el autor en el header X-Usuario-Id.
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
// @Size(max = 500): el texto puede tener como máximo 500 caracteres (igual que la columna en MySQL).
public record PreguntaRequest(
        @NotBlank(message = "La pregunta es obligatoria")
        @Size(max = 500, message = "La pregunta puede tener como máximo 500 caracteres")
        String texto
) {}
