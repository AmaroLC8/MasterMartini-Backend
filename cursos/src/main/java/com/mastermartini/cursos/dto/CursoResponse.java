package com.mastermartini.cursos.dto;

import com.mastermartini.cursos.model.Curso;

import java.util.List;

// Datos completos de un curso que se envían a la app en formato JSON.
// Los nombres de los campos son EXACTAMENTE los que lee la app (modelo Capacitacion), no se deben cambiar.
// Es un "record": una clase inmutable cuyos campos se convierten en las claves del JSON.
public record CursoResponse(
        Long id,
        String titulo,
        String descripcion,
        String categoria,
        String nivel,
        boolean esGratis,
        Long instructorId,
        String instructor,
        String duracionTotal,
        String insumoPrincipal,
        String videoResumenUrl,
        boolean esVegano,
        List<String> loQueLograras,
        List<String> incluye,
        List<ModuloDto> modulos
) {
    // Convierte la entidad Curso en la respuesta que ve la app.
    // Las listas se copian a listas normales: así se leen todas de la base de datos aquí mismo,
    // dentro de la transacción del servicio (las listas son LAZY).
    public static CursoResponse desdeEntidad(Curso c) {
        return new CursoResponse(
                c.getId(),
                c.getTitulo(),
                c.getDescripcion(),
                c.getCategoria(),
                c.getNivel(),
                c.isEsGratis(),
                c.getInstructorId(),
                c.getInstructor(),
                c.getDuracionTotal(),
                c.getInsumoPrincipal(),
                c.getVideoResumenUrl(),
                c.isEsVegano(),
                List.copyOf(c.getLoQueLograras()),
                List.copyOf(c.getIncluye()),
                c.getModulos().stream().map(ModuloDto::desdeEntidad).toList()
        );
    }
}
