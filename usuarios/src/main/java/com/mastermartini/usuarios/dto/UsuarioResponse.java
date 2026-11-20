package com.mastermartini.usuarios.dto;

import com.mastermartini.usuarios.model.Rol;
import com.mastermartini.usuarios.model.Usuario;

// Datos del usuario que se devuelven a la app en formato JSON.
// No incluye la contraseña ni el código de recuperación, que nunca deben salir del servidor.
public record UsuarioResponse(
        Long id,
        String nombre,
        String rut,
        String correo,
        String telefono,
        String rubro,
        String personasNegocio,
        Rol rol,
        boolean planPro
) {
    // Convierte la entidad de la base de datos (Usuario) en la respuesta que ve la app.
    public static UsuarioResponse desdeEntidad(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getRut(),
                u.getCorreo(),
                u.getTelefono(),
                u.getRubro(),
                u.getPersonasNegocio(),
                u.getRol(),
                u.isPlanPro()
        );
    }
}
