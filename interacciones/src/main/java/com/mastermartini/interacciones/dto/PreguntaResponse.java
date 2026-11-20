package com.mastermartini.interacciones.dto;

import com.mastermartini.interacciones.model.Pregunta;

import java.time.LocalDateTime;

// Datos de una pregunta que se envían a la app en formato JSON:
// {id, cursoId, cursoTitulo, usuarioId, autorNombre, texto, respuesta, respondidaPor, fechaPregunta, fechaRespuesta}.
// respuesta, respondidaPor y fechaRespuesta vienen en null mientras la pregunta no tenga respuesta.
public record PreguntaResponse(
        Long id,
        Long cursoId,
        String cursoTitulo,
        Long usuarioId,
        String autorNombre,
        String texto,
        String respuesta,
        String respondidaPor,
        LocalDateTime fechaPregunta,
        LocalDateTime fechaRespuesta
) {
    // Convierte la entidad Pregunta en la respuesta que ve la app.
    public static PreguntaResponse desdeEntidad(Pregunta p) {
        return new PreguntaResponse(
                p.getId(),
                p.getCursoId(),
                p.getCursoTitulo(),
                p.getUsuarioId(),
                p.getAutorNombre(),
                p.getTexto(),
                p.getRespuesta(),
                p.getRespondidaPor(),
                p.getFechaPregunta(),
                p.getFechaRespuesta()
        );
    }
}
