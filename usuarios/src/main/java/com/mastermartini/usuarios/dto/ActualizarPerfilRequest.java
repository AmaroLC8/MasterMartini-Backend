package com.mastermartini.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

// Datos que la app envía para editar el perfil (PUT /api/usuarios/{id}).
// El correo, el RUT, el rol y el Plan Pro no se pueden cambiar desde aquí.
// Es un "record": una clase inmutable cuyos campos coinciden con las claves del JSON.
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
// @Pattern: el campo debe cumplir la expresión regular indicada.
public record ActualizarPerfilRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El teléfono es obligatorio")
        @Pattern(regexp = "^9\\d{8}$", message = "El teléfono debe tener 9 dígitos y empezar con 9")
        String telefono,

        @NotBlank(message = "El rubro es obligatorio")
        String rubro,

        @NotBlank(message = "La cantidad de personas es obligatoria")
        String personasNegocio
) {}
