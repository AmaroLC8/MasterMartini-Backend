package com.mastermartini.usuarios.model;

// Roles posibles de un usuario. Definen qué acciones puede hacer cada uno:
// CLIENTE (usuario normal), INSTRUCTOR (dicta cursos), VENTAS (gestiona clientes y Plan Pro)
// y ADMINISTRADOR (control total, incluido cambiar roles y eliminar usuarios).
public enum Rol {
    CLIENTE,
    INSTRUCTOR,
    VENTAS,
    ADMINISTRADOR
}
