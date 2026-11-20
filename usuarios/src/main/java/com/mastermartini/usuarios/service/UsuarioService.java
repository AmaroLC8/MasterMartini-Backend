package com.mastermartini.usuarios.service;

import com.mastermartini.usuarios.dto.*;
import com.mastermartini.usuarios.exception.ApiException;
import com.mastermartini.usuarios.model.Rol;
import com.mastermartini.usuarios.model.Usuario;
import com.mastermartini.usuarios.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// Contiene la lógica de negocio de los usuarios: registro, login, recuperación de contraseña,
// perfil y administración (Plan Pro, roles y eliminación). El controlador solo llama a estos métodos.
// @Service: marca la clase como componente de lógica de negocio; Spring crea una sola instancia
// y la inyecta donde se necesite (por ejemplo, en UsuarioController).
@Service
public class UsuarioService {

    // Logger para escribir mensajes en la consola (aquí se "envía" el código de recuperación).
    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    // Mensajes que se repiten en varios métodos.
    private static final String SIN_PERMISO = "No tienes permiso para realizar esta acción";
    private static final String CODIGO_INVALIDO = "Código de recuperación inválido o vencido";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring entrega el repositorio y el codificador de contraseñas por el constructor (inyección de dependencias).
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registra un nuevo usuario con rol CLIENTE y sin Plan Pro; responde 409 si el correo o el RUT ya existen.
    public UsuarioResponse registrar(RegistroRequest req) {
        String correo = normalizarCorreo(req.correo());
        String rut = normalizarRut(req.rut());

        if (usuarioRepository.existsByCorreo(correo)) {
            throw new ApiException(HttpStatus.CONFLICT, "El correo ya esta registrado");
        }
        if (usuarioRepository.existsByRut(rut)) {
            throw new ApiException(HttpStatus.CONFLICT, "El rut ya esta registrado");
        }
        Usuario nuevo = new Usuario(
                req.nombre(),
                rut,
                correo,
                req.telefono(),
                req.rubro(),
                req.personasNegocio(),
                passwordEncoder.encode(req.password()), // Se guarda el hash BCrypt, nunca la clave real
                Rol.CLIENTE,
                false
        );
        Usuario guardado = usuarioRepository.save(nuevo);
        return UsuarioResponse.desdeEntidad(guardado);
    }

    // Autentica con correo o RUT y contraseña; responde 401 con el mismo mensaje si falla cualquiera de los dos,
    // para no revelar si el usuario existe.
    public UsuarioResponse login(LoginRequest req) {
        // Primero se busca como correo y, si no aparece, como RUT.
        Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(req.usuario()))
                .or(() -> usuarioRepository.findByRut(normalizarRut(req.usuario())))
                .orElseThrow(() -> new ApiException(
                        HttpStatus.UNAUTHORIZED, "RUT/correo o contraseña incorrectos"
                ));

        // matches compara la contraseña escrita con el hash guardado en la base de datos.
        if (!passwordEncoder.matches(req.password(), usuario.getPassword())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "RUT/correo o contraseña incorrectos");
        }

        return UsuarioResponse.desdeEntidad(usuario);
    }

    // Genera y registra un código de recuperación de 6 dígitos si el correo existe; no requiere autenticación.
    // Si el correo no existe no hace nada, así la respuesta es igual y no se revela qué correos están registrados.
    public void recuperar(RecuperarRequest req) {
        String correo = normalizarCorreo(req.correo());
        usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
            String codigo = String.format("%06d", new SecureRandom().nextInt(1_000_000));
            usuario.setCodigoRecuperacion(codigo);
            usuario.setCodigoExpira(LocalDateTime.now().plusMinutes(10)); // El código dura 10 minutos
            usuarioRepository.save(usuario);
            // No hay envío real de correos: el código se muestra en la consola del servidor.
            log.info("Código de recuperación para {}: {}", correo, codigo);
        });
    }

    // Valida el código de recuperación y su vigencia para restablecer la contraseña del usuario; no requiere autenticación.
    // Correo inexistente, código incorrecto y código vencido responden el mismo 400, para no revelar qué correos existen.
    public void restablecer(RestablecerRequest req) {
        Usuario usuario = usuarioRepository.findByCorreo(normalizarCorreo(req.correo()))
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, CODIGO_INVALIDO));

        if (usuario.getCodigoRecuperacion() == null || !usuario.getCodigoRecuperacion().equals(req.codigo())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, CODIGO_INVALIDO);
        }
        if (usuario.getCodigoExpira() == null || usuario.getCodigoExpira().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, CODIGO_INVALIDO);
        }

        usuario.setPassword(passwordEncoder.encode(req.nuevaPassword()));
        // Se borra el código para que no se pueda volver a usar.
        usuario.setCodigoRecuperacion(null);
        usuario.setCodigoExpira(null);
        usuarioRepository.save(usuario);
    }

    // Obtiene los datos de un usuario por su ID; solo permitido para el propio usuario, VENTAS o ADMINISTRADOR.
    public UsuarioResponse obtenerPorId(Long id, Long solicitanteId) {
        Usuario usuario = buscarUsuarioPorId(id);
        if (!id.equals(solicitanteId)) {
            verificarRol(solicitanteId, Rol.VENTAS, Rol.ADMINISTRADOR);
        }
        return UsuarioResponse.desdeEntidad(usuario);
    }

    // Actualiza nombre, teléfono, rubro y personas del negocio de un usuario; solo permitido para el propio usuario o ADMINISTRADOR.
    public UsuarioResponse actualizarPerfil(Long id, ActualizarPerfilRequest req, Long solicitanteId) {
        Usuario usuario = buscarUsuarioPorId(id);
        if (!id.equals(solicitanteId)) {
            verificarRol(solicitanteId, Rol.ADMINISTRADOR);
        }

        usuario.setNombre(req.nombre());
        usuario.setTelefono(req.telefono());
        usuario.setRubro(req.rubro());
        usuario.setPersonasNegocio(req.personasNegocio());

        return UsuarioResponse.desdeEntidad(usuarioRepository.save(usuario));
    }

    // Lista todos los usuarios o los filtra por rol; solo permitido para VENTAS o ADMINISTRADOR.
    public List<UsuarioResponse> listar(Rol rolFiltro, Long solicitanteId) {
        verificarRol(solicitanteId, Rol.VENTAS, Rol.ADMINISTRADOR);
        List<Usuario> usuarios = (rolFiltro == null)
                ? usuarioRepository.findAll()
                : usuarioRepository.findByRol(rolFiltro);
        return usuarios.stream().map(UsuarioResponse::desdeEntidad).toList();
    }

    // Activa o desactiva el Plan Pro de un usuario; solo permitido para VENTAS o ADMINISTRADOR.
    public UsuarioResponse cambiarPlanPro(Long id, PlanProRequest req, Long solicitanteId) {
        verificarRol(solicitanteId, Rol.VENTAS, Rol.ADMINISTRADOR);
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setPlanPro(req.activo());
        return UsuarioResponse.desdeEntidad(usuarioRepository.save(usuario));
    }

    // Cambia el rol de un usuario; solo permitido para ADMINISTRADOR y nunca sobre su propia cuenta
    // (así el sistema no se queda sin administradores por error).
    public UsuarioResponse cambiarRol(Long id, CambiarRolRequest req, Long solicitanteId) {
        verificarRol(solicitanteId, Rol.ADMINISTRADOR);
        if (id.equals(solicitanteId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No puedes cambiar tu propio rol");
        }
        Usuario usuario = buscarUsuarioPorId(id);
        usuario.setRol(req.rol());
        return UsuarioResponse.desdeEntidad(usuarioRepository.save(usuario));
    }

    // Elimina un usuario; solo permitido para ADMINISTRADOR y nunca sobre su propia cuenta.
    public void eliminar(Long id, Long solicitanteId) {
        verificarRol(solicitanteId, Rol.ADMINISTRADOR);
        if (id.equals(solicitanteId)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No puedes eliminar tu propia cuenta");
        }
        Usuario usuario = buscarUsuarioPorId(id); // Responde 404 si no existe
        usuarioRepository.delete(usuario);
    }

    // Busca un usuario por ID o responde 404 si no existe.
    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    // Verifica que quien hace la petición (header X-Usuario-Id) exista y tenga uno de los roles permitidos;
    // si no, responde 403.
    private void verificarRol(Long solicitanteId, Rol... rolesPermitidos) {
        if (solicitanteId == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO));
        boolean autorizado = Arrays.asList(rolesPermitidos).contains(solicitante.getRol());
        if (!autorizado) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }
    }

    // Deja el correo sin espacios y en minúsculas, para que "Ana@Mail.com " y "ana@mail.com" sean el mismo.
    private String normalizarCorreo(String correo) {
        return correo.trim().toLowerCase();
    }

    // Deja el RUT sin puntos, sin espacios y en mayúsculas (la "k" queda como "K"): "12.345.678-k" -> "12345678-K".
    private String normalizarRut(String rut) {
        return rut.replace(".", "").trim().toUpperCase();
    }
}
