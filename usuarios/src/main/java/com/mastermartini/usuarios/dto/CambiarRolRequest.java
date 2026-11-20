package com.mastermartini.usuarios.dto;

import com.mastermartini.usuarios.model.Rol;
import jakarta.validation.constraints.NotNull;

// Datos para cambiar el rol de un usuario (PUT /api/usuarios/{id}/rol), ej: {"rol": "VENTAS"}.
// @NotNull: el campo es obligatorio (no puede venir nulo).
public record CambiarRolRequest(
        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {}
