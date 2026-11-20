package com.mastermartini.interacciones.dto;

import jakarta.validation.constraints.NotBlank;

// Datos para responder una pregunta (PUT /api/preguntas/{id}/respuesta), ej: {"respuesta": "..."}.
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
public record RespuestaRequest(
        @NotBlank(message = "La respuesta es obligatoria")
        String respuesta
) {}
