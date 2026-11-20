package com.mastermartini.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

// Datos para iniciar sesión (POST /api/usuarios/login).
// "usuario" puede ser el correo o el RUT; el servicio prueba con ambos.
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
public record LoginRequest(
        @NotBlank(message = "Debes ingresar tu RUT o correo")
        String usuario,

        @NotBlank(message = "Debes ingresar tu contraseña")
        String password
) {}
