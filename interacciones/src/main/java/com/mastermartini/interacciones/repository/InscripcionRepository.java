package com.mastermartini.interacciones.repository;

import com.mastermartini.interacciones.model.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acceso a la tabla "inscripciones". Al extender JpaRepository ya trae save, findById, findAll, delete, count, etc.
// Spring Data genera las consultas SQL a partir del nombre de cada método.
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Inscripciones de un usuario, de la más nueva a la más antigua.
    // (WHERE usuario_id = ? ORDER BY fecha_inscripcion DESC)
    List<Inscripcion> findByUsuarioIdOrderByFechaInscripcionDesc(Long usuarioId);

    // Revisa si el usuario ya está inscrito en el curso (para no inscribirlo dos veces y para permitir preguntar).
    boolean existsByUsuarioIdAndCursoId(Long usuarioId, Long cursoId);
}
