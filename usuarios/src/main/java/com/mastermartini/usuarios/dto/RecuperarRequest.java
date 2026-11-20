package com.mastermartini.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Datos para pedir un código de recuperación de contraseña (POST /api/usuarios/recuperar).
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
// @Email: el texto debe tener formato de correo electrónico.
public record RecuperarRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String correo
) {}
