package com.mastermartini.cursos.model;

import jakarta.persistence.*;

// Entidad que representa una lección dentro de un módulo (tabla "lecciones").
// Un curso se arma así: Curso -> Módulos -> Lecciones.
// @Entity: marca la clase como entidad JPA, es decir, que se guarda en la base de datos.
// @Table: indica el nombre de la tabla.
@Entity
@Table(name = "lecciones")
public class Leccion {

    // @Id: llave primaria. @GeneratedValue(IDENTITY): MySQL genera el valor (autoincremental).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column: configura la columna (nullable = false equivale a NOT NULL).
    @Column(nullable = false)
    private String titulo;

    // columnDefinition = "TEXT": permite textos más largos que el VARCHAR(255) por defecto.
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    // Duración como texto, ej. "12 min" (la app saca los minutos de este texto).
    @Column(nullable = false)
    private String duracion;

    // Dirección del video. Puede ser null: los videos se subirán después a AWS S3.
    private String videoUrl;

    // Constructor vacío obligatorio para que Hibernate pueda crear el objeto al leer de MySQL.
    public Leccion() {
    }

    public Leccion(String titulo, String descripcion, String duracion, String videoUrl) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.videoUrl = videoUrl;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
}
