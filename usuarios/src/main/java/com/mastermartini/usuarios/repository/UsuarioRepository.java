package com.mastermartini.usuarios.repository;

import com.mastermartini.usuarios.model.Rol;
import com.mastermartini.usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Acceso a la tabla "usuarios". Al extender JpaRepository ya trae save, findById, findAll, delete, count, etc.
// Spring Data genera las consultas SQL a partir del nombre de cada método (ej: findByCorreo -> WHERE correo = ?).
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Buscan un usuario por correo o por RUT (Optional vacío si no existe).
    Optional<Usuario> findByCorreo(String correo);
    Optional<Usuario> findByRut(String rut);

    // Revisan si ya existe un correo o RUT (se usan al registrar para evitar duplicados).
    boolean existsByCorreo(String correo);
    boolean existsByRut(String rut);

    // Lista los usuarios que tienen un rol específico.
    List<Usuario> findByRol(Rol rol);
}
