package com.mastermartini.usuarios.dto;

import jakarta.validation.constraints.NotNull;

// Datos para activar o desactivar el Plan Pro (PUT /api/usuarios/{id}/plan-pro), ej: {"activo": true}.
// Se usa Boolean (con mayúscula) para poder detectar cuando el campo no viene.
// @NotNull: el campo es obligatorio (no puede venir nulo).
public record PlanProRequest(
        @NotNull(message = "Debes indicar si el Plan Pro queda activo (true/false)")
        Boolean activo
) {}
