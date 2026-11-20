package com.mastermartini.cursos.repository;

import com.mastermartini.cursos.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

// Acceso a la tabla "capacitaciones". Al extender JpaRepository ya trae save, findAll, findById,
// delete, count, etc., sin escribir SQL. Los filtros de categoría y gratis se aplican en CursoService.
public interface CursoRepository extends JpaRepository<Curso, Long> {
}
