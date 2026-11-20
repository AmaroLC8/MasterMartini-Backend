package com.mastermartini.usuarios.dto;

import jakarta.validation.constraints.*;

// Datos que la app envía para crear una cuenta nueva (POST /api/usuarios/registro).
// El rol y el Plan Pro no vienen aquí: todo registro nuevo queda como CLIENTE sin Plan Pro.
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
// @Pattern: el campo debe cumplir la expresión regular indicada.
// @Email: el texto debe tener formato de correo electrónico.
// @Size: define el largo mínimo (o máximo) del texto.
public record RegistroRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        // Acepta el RUT con o sin puntos (12.345.678-9 o 12345678-9); el servicio lo guarda sin puntos.
        @NotBlank(message = "El RUT es obligatorio")
        @Pattern(regexp = "^\\s*\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]\\s*$", message = "Formato de RUT inválido (ej: 12345678-9)")
        String rut,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Formato de correo inválido")
        String correo,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
        String telefono,

        @NotBlank(message = "El rubro es obligatorio")
        String rubro,

        @NotBlank(message = "La cantidad de personas es obligatoria")
        String personasNegocio,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
        String password
) {}
