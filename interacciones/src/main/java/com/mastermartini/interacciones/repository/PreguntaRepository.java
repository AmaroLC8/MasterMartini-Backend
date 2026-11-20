package com.mastermartini.interacciones.repository;

import com.mastermartini.interacciones.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Acceso a la tabla "preguntas". Al extender JpaRepository ya trae save, findById, findAll, delete, count, etc.
// Spring Data genera las consultas SQL a partir del nombre de cada método.
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    // Preguntas de un curso, de la más nueva a la más antigua.
    // (WHERE curso_id = ? ORDER BY fecha_pregunta DESC)
    List<Pregunta> findByCursoIdOrderByFechaPreguntaDesc(Long cursoId);

    // Todas las preguntas sin respuesta, de la más antigua a la más nueva (primero las que llevan más tiempo esperando).
    // (WHERE respuesta IS NULL ORDER BY fecha_pregunta ASC)
    List<Pregunta> findByRespuestaIsNullOrderByFechaPreguntaAsc();

    // Preguntas sin respuesta de una lista de cursos (los del instructor), de la más antigua a la más nueva.
    // (WHERE respuesta IS NULL AND curso_id IN (...) ORDER BY fecha_pregunta ASC)
    List<Pregunta> findByRespuestaIsNullAndCursoIdInOrderByFechaPreguntaAsc(List<Long> cursoIds);
}
