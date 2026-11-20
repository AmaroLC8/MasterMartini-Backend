package com.mastermartini.interacciones.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Entidad que representa una pregunta de un alumno al instructor de un curso (tabla "preguntas").
// La respuesta, quién respondió y la fecha de respuesta quedan en null hasta que alguien responda.
// @Entity: marca la clase como entidad JPA, es decir, que se guarda en la base de datos.
// @Table: indica el nombre de la tabla.
@Entity
@Table(name = "preguntas")
public class Pregunta {

    // @Id: llave primaria. @GeneratedValue(IDENTITY): MySQL genera el valor (autoincremental).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del curso (vive en el microservicio cursos).
    // @Column: configura la columna (nullable = false equivale a NOT NULL).
    @Column(nullable = false)
    private Long cursoId;

    // Título del curso, guardado al preguntar para mostrarlo sin llamar a cursos.
    @Column(nullable = false)
    private String cursoTitulo;

    // Id del alumno que pregunta (vive en el microservicio usuarios).
    @Column(nullable = false)
    private Long usuarioId;

    // Nombre del alumno, guardado al preguntar para mostrarlo sin llamar a usuarios.
    @Column(nullable = false)
    private String autorNombre;

    // length = 500: la columna es VARCHAR(500).
    @Column(nullable = false, length = 500)
    private String texto;

    // columnDefinition = "TEXT": permite respuestas largas. null hasta que respondan.
    @Column(columnDefinition = "TEXT")
    private String respuesta;

    // Nombre de quien respondió (instructor o administrador). null hasta que respondan.
    private String respondidaPor;

    @Column(nullable = false)
    private LocalDateTime fechaPregunta;

    // null hasta que respondan.
    private LocalDateTime fechaRespuesta;

    // Constructor vacío obligatorio para que Hibernate pueda crear el objeto al leer de MySQL.
    public Pregunta() {
    }

    // Constructor usado al preguntar y en DataSeeder (la pregunta parte sin respuesta).
    public Pregunta(Long cursoId, String cursoTitulo, Long usuarioId, String autorNombre,
                    String texto, LocalDateTime fechaPregunta) {
        this.cursoId = cursoId;
        this.cursoTitulo = cursoTitulo;
        this.usuarioId = usuarioId;
        this.autorNombre = autorNombre;
        this.texto = texto;
        this.fechaPregunta = fechaPregunta;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public String getCursoTitulo() { return cursoTitulo; }
    public void setCursoTitulo(String cursoTitulo) { this.cursoTitulo = cursoTitulo; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getAutorNombre() { return autorNombre; }
    public void setAutorNombre(String autorNombre) { this.autorNombre = autorNombre; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getRespuesta() { return respuesta; }
    public void setRespuesta(String respuesta) { this.respuesta = respuesta; }

    public String getRespondidaPor() { return respondidaPor; }
    public void setRespondidaPor(String respondidaPor) { this.respondidaPor = respondidaPor; }

    public LocalDateTime getFechaPregunta() { return fechaPregunta; }
    public void setFechaPregunta(LocalDateTime fechaPregunta) { this.fechaPregunta = fechaPregunta; }

    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
}
