package com.mastermartini.cursos.service;

import com.mastermartini.cursos.client.UsuariosClient;
import com.mastermartini.cursos.dto.CursoRequest;
import com.mastermartini.cursos.dto.CursoResponse;
import com.mastermartini.cursos.dto.ModuloDto;
import com.mastermartini.cursos.exception.ApiException;
import com.mastermartini.cursos.model.Curso;
import com.mastermartini.cursos.repository.CursoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Lógica de negocio de los cursos: listar, ver detalle, crear, editar y borrar.
// Los permisos se revisan preguntando el rol al microservicio usuarios (ver UsuariosClient).
// @Service: marca la clase como componente de lógica de negocio; Spring crea una sola instancia
// y la inyecta donde se necesite (por ejemplo, en CursoController).
@Service
public class CursoService {

    private static final String SIN_PERMISO = "No tienes permiso para realizar esta acción";

    private final CursoRepository cursoRepository;
    private final UsuariosClient usuariosClient;

    // Spring entrega el repositorio y el cliente de usuarios por el constructor (inyección de dependencias).
    public CursoService(CursoRepository cursoRepository, UsuariosClient usuariosClient) {
        this.cursoRepository = cursoRepository;
        this.usuariosClient = usuariosClient;
    }

    // Lista los cursos ordenados por id. Los dos filtros son opcionales y se pueden combinar:
    // categoria (ej. "Panadería") y gratis (true = solo gratis, false = solo Plan Pro). Público.
    // @Transactional(readOnly = true): todo el método corre en una sola transacción de solo lectura.
    // Así las listas LAZY (módulos, lecciones, logros) se pueden leer mientras se arma el DTO,
    // sin el error "could not initialize proxy - no Session".
    @Transactional(readOnly = true)
    public List<CursoResponse> listar(String categoria, Boolean gratis) {
        return cursoRepository.findAll(Sort.by("id")).stream()
                .filter(c -> categoria == null || categoria.isBlank() || c.getCategoria().equalsIgnoreCase(categoria))
                .filter(c -> gratis == null || c.isEsGratis() == gratis)
                .map(CursoResponse::desdeEntidad)
                .toList();
    }

    // Devuelve el curso completo (con módulos y lecciones) o 404 si no existe. Público.
    @Transactional(readOnly = true)
    public CursoResponse obtenerPorId(Long id) {
        return CursoResponse.desdeEntidad(buscarCurso(id));
    }

    // Crea un curso; solo INSTRUCTOR o ADMINISTRADOR. El curso queda a nombre de quien lo crea (instructorId).
    // @Transactional: si algo falla a mitad de camino, no se guarda nada (todo o nada).
    @Transactional
    public CursoResponse crear(CursoRequest req, Long solicitanteId) {
        String rol = usuariosClient.obtenerRol(solicitanteId);
        if (!rol.equals("INSTRUCTOR") && !rol.equals("ADMINISTRADOR")) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }

        Curso nuevo = new Curso(
                req.titulo(),
                req.descripcion(),
                req.categoria(),
                req.nivel(),
                req.esGratis(),
                solicitanteId, // El dueño del curso es quien lo crea
                req.instructor(),
                req.duracionTotal(),
                req.insumoPrincipal(),
                req.videoResumenUrl(),
                req.esVegano(),
                listaOVacia(req.loQueLograras()),
                listaOVacia(req.incluye()),
                req.modulos().stream().map(ModuloDto::aEntidad).toList()
        );
        return CursoResponse.desdeEntidad(cursoRepository.save(nuevo));
    }

    // Reemplaza todos los datos de un curso; solo el INSTRUCTOR dueño del curso o un ADMINISTRADOR.
    // El dueño (instructorId) no cambia.
    @Transactional
    public CursoResponse actualizar(Long id, CursoRequest req, Long solicitanteId) {
        String rol = usuariosClient.obtenerRol(solicitanteId);
        Curso curso = buscarCurso(id);

        boolean esAdmin = rol.equals("ADMINISTRADOR");
        boolean esDueno = rol.equals("INSTRUCTOR") && curso.getInstructorId().equals(solicitanteId);
        if (!esAdmin && !esDueno) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }

        curso.setTitulo(req.titulo());
        curso.setDescripcion(req.descripcion());
        curso.setCategoria(req.categoria());
        curso.setNivel(req.nivel());
        curso.setEsGratis(req.esGratis());
        curso.setInstructor(req.instructor());
        curso.setDuracionTotal(req.duracionTotal());
        curso.setInsumoPrincipal(req.insumoPrincipal());
        curso.setVideoResumenUrl(req.videoResumenUrl());
        curso.setEsVegano(req.esVegano());

        // Las listas se vacían y se vuelven a llenar (la misma lista, no una nueva).
        // Gracias a orphanRemoval, los módulos y lecciones antiguos se borran de la base de datos.
        curso.getLoQueLograras().clear();
        curso.getLoQueLograras().addAll(listaOVacia(req.loQueLograras()));
        curso.getIncluye().clear();
        curso.getIncluye().addAll(listaOVacia(req.incluye()));
        curso.getModulos().clear();
        curso.getModulos().addAll(req.modulos().stream().map(ModuloDto::aEntidad).toList());

        return CursoResponse.desdeEntidad(cursoRepository.save(curso));
    }

    // Borra un curso (con sus módulos y lecciones, por el cascade); solo ADMINISTRADOR.
    @Transactional
    public void eliminar(Long id, Long solicitanteId) {
        String rol = usuariosClient.obtenerRol(solicitanteId);
        if (!rol.equals("ADMINISTRADOR")) {
            throw new ApiException(HttpStatus.FORBIDDEN, SIN_PERMISO);
        }
        cursoRepository.delete(buscarCurso(id)); // Responde 404 si no existe
    }

    // Busca un curso por id o responde 404 si no existe.
    private Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Curso no encontrado"));
    }

    // Devuelve la lista recibida, o una lista vacía si no vino en el JSON.
    private List<String> listaOVacia(List<String> lista) {
        return (lista == null) ? List.of() : lista;
    }
}
