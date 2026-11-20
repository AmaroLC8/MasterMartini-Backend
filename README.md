# Master Martini – Backend

Backend de la app Android **Master Martini**, formado por 3 microservicios Spring Boot independientes.
Cada uno tiene su propia base de datos MySQL, que se crea sola al iniciar (`createDatabaseIfNotExist=true`).

| Microservicio   | Puerto | Base de datos   | Qué hace |
|-----------------|--------|-----------------|----------|
| `usuarios`      | 9091   | `usuarios`      | Registro, login, recuperación de contraseña, perfiles, roles y Plan Pro |
| `cursos`        | 9092   | `cursos`        | Catálogo de capacitaciones |
| `interacciones` | 9093   | `interacciones` | Favoritos y notas técnicas de los usuarios sobre los cursos |

App Android: https://github.com/AmaroLC8/MasterMartini

> **Nota:** el historial completo del desarrollo del backend está en el repositorio de la app,
> en la carpeta `backend/`, hasta el commit `a608401`. Este repositorio es la versión separada en microservicios.

## Requisitos

- Java 17 o superior.
- MySQL 8 o XAMPP (MariaDB) corriendo en `localhost:3306`.
- No hace falta instalar Maven: cada microservicio trae su Maven Wrapper (`mvnw` / `mvnw.cmd`).

## Configurar la contraseña de MySQL

El usuario y la contraseña se leen de variables de entorno:

| Variable      | Valor por defecto | Uso |
|---------------|-------------------|-----|
| `DB_USER`     | `root`            | Usuario de MySQL |
| `DB_PASSWORD` | *(vacío)*         | Contraseña de MySQL |

- **XAMPP:** no hay que configurar nada (root sin contraseña).
- **MySQL con contraseña:** en IntelliJ, `Run → Edit Configurations…`, elegir la aplicación y en
  **Environment variables** poner `DB_PASSWORD=tuClave`. Hay que hacerlo en las 3 aplicaciones.
- Desde la terminal (PowerShell): `$env:DB_PASSWORD="tuClave"` antes de ejecutar `mvnw`.

Así nadie sube su contraseña al repositorio.

## Ejecutar

Cada microservicio se ejecuta por separado (para usar la app completa, levantar los 3).

**Con IntelliJ:** abrir la carpeta del microservicio (`usuarios`, `cursos` o `interacciones`) y ejecutar
la clase `UsuariosApplication`, `CursosApplication` o `InteraccionesApplication`.

**Con la terminal:**

```bash
cd usuarios
./mvnw spring-boot:run        # Linux / macOS / Git Bash
mvnw.cmd spring-boot:run      # Windows (cmd o PowerShell)
```

Lo mismo dentro de `cursos` y `interacciones`.

## Endpoints de `usuarios` (puerto 9091)

Ruta base: `http://localhost:9091/api/usuarios`

Los endpoints protegidos necesitan el header **`X-Usuario-Id`** con el id del usuario que hace la petición.
Si falta o el usuario no tiene permiso, la respuesta es `403`.

| Método | Ruta                 | Permiso | Respuesta OK |
|--------|----------------------|---------|--------------|
| POST   | `/registro`          | Público (queda como CLIENTE) | 201 |
| POST   | `/login`             | Público (correo o RUT + contraseña) | 200 |
| POST   | `/recuperar`         | Público | 200 |
| POST   | `/restablecer`       | Público | 200 |
| GET    | `/{id}`              | El propio usuario, VENTAS o ADMINISTRADOR | 200 |
| PUT    | `/{id}`              | El propio usuario o ADMINISTRADOR | 200 |
| GET    | `/` (opcional `?rol=CLIENTE`) | VENTAS o ADMINISTRADOR | 200 |
| PUT    | `/{id}/plan-pro`     | VENTAS o ADMINISTRADOR | 200 |
| PUT    | `/{id}/rol`          | ADMINISTRADOR (no puede cambiar su propio rol) | 200 |
| DELETE | `/{id}`              | ADMINISTRADOR (no puede eliminar su propia cuenta) | 204 |

Detalles:
- El correo se guarda y busca en minúsculas y sin espacios; el RUT sin puntos y con la `K` en mayúscula.
- `/recuperar` responde lo mismo exista o no el correo. El código (6 dígitos, dura 10 minutos)
  se muestra en la consola del servidor, porque no hay envío real de correos.
- `/restablecer` responde `400 "Código de recuperación inválido o vencido"` tanto si el código es malo
  o venció como si el correo no existe, para no revelar qué correos están registrados.

Todos los errores tienen el formato:

```json
{ "mensaje": "Usuario no encontrado", "estado": 404 }
```

Los errores de validación agregan `"campos"` con el mensaje de cada campo inválido.

## Usuarios de prueba

Se crean automáticamente la primera vez que se inicia `usuarios` (si la tabla está vacía).
Contraseña de todos: **`Demo1234`**

| Id | Nombre           | Correo                   | RUT        | Rol           | Plan Pro |
|----|------------------|--------------------------|------------|---------------|----------|
| 1  | Carlos Mendoza   | cliente@example.com      | 11111111-1 | CLIENTE       | No |
| 2  | Chef Marco Rossi | instructor@example.com   | 22222222-2 | INSTRUCTOR    | Sí |
| 3  | Sofía Valdés     | ventas@example.com       | 15482930-K | VENTAS        | Sí |
| 4  | Admin Sistema    | admin@example.com        | 18765432-7 | ADMINISTRADOR | Sí |
