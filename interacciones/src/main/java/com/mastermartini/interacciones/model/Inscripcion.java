package com.mastermartini.interacciones.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Entidad que representa la inscripción de un usuario en un curso (tabla nueva "inscripciones",
// para no chocar con la tabla vieja "interacciones"). También guarda el progreso por lección.
// Un usuario solo puede inscribirse una vez en cada curso.
// @Entity: marca la clase como entidad JPA, es decir, que se guarda en la base de datos.
// @Table: indica el nombre de la tabla.
//   uniqueConstraints + @UniqueConstraint: MySQL impide dos filas con el mismo (usuario_id, curso_id).
@Entity
@Table(name = "inscripciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "curso_id"}))
public class Inscripcion {

    // @Id: llave primaria. @GeneratedValue(IDENTITY): MySQL genera el valor (autoincremental).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Id del usuario (vive en el microservicio usuarios). Solo se guarda el número:
    // cada microservicio tiene su propia base de datos.
    // @Column: configura la columna (name = nombre en MySQL, nullable = false equivale a NOT NULL).
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    // Id del curso (vive en el microservicio cursos).
    @Column(name = "curso_id", nullable = false)
    private Long cursoId;

    // Título del curso, guardado al inscribirse para mostrarlo sin tener que llamar a cursos.
    @Column(nullable = false)
    private String cursoTitulo;

    @Column(nullable = false)
    private LocalDateTime fechaInscripcion;

    // Lecciones completadas, cada una como "modulo-leccion" con posiciones desde 0.
    // Ej: "0-2" = tercera lección del primer módulo.
    // @ElementCollection: guarda una lista de valores simples (String) en una tabla aparte,
    //   sin crear una entidad. Por defecto es LAZY (se carga recién cuando se usa).
    // @CollectionTable: nombre de esa tabla y la columna (inscripcion_id) que la une con la inscripción.
    // @OrderColumn: guarda la posición de cada elemento (columna "orden") para mantener el orden.
    @ElementCollection
    @CollectionTable(name = "inscripcion_lecciones", joinColumns = @JoinColumn(name = "inscripcion_id"))
    @OrderColumn(name = "orden")
    @Column(name = "leccion", nullable = false)
    private List<String> leccionesCompletadas = new ArrayList<>();

    // Constructor vacío obligatorio para que Hibernate pueda crear el objeto al leer de MySQL.
    public Inscripcion() {
    }

    // Constructor usado al inscribirse y en DataSeeder (el id lo genera MySQL; empieza sin lecciones completadas).
    public Inscripcion(Long usuarioId, Long cursoId, String cursoTitulo, LocalDateTime fechaInscripcion) {
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.cursoTitulo = cursoTitulo;
        this.fechaInscripcion = fechaInscripcion;
    }

    // --- Getters y Setters ---
    // La lista no tiene setter: se modifica con add() y remove() sobre la misma lista.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }

    public String getCursoTitulo() { return cursoTitulo; }
    public void setCursoTitulo(String cursoTitulo) { this.cursoTitulo = cursoTitulo; }

    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public List<String> getLeccionesCompletadas() { return leccionesCompletadas; }
}
