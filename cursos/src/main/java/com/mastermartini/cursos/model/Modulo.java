package com.mastermartini.cursos.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// Entidad que representa un módulo de un curso (tabla "modulos"). Agrupa varias lecciones en orden.
// @Entity: marca la clase como entidad JPA, es decir, que se guarda en la base de datos.
// @Table: indica el nombre de la tabla.
@Entity
@Table(name = "modulos")
public class Modulo {

    // @Id: llave primaria. @GeneratedValue(IDENTITY): MySQL genera el valor (autoincremental).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column: configura la columna (nullable = false equivale a NOT NULL).
    @Column(nullable = false)
    private String titulo;

    // Lecciones del módulo, en el orden en que se ven.
    // @OneToMany: un módulo tiene muchas lecciones.
    //   cascade = ALL: al guardar o borrar el módulo, también se guardan o borran sus lecciones.
    //   orphanRemoval = true: si una lección se saca de la lista, se borra de la base de datos.
    //   Por defecto es LAZY: las lecciones se cargan recién cuando se usan.
    // @JoinColumn: la tabla "lecciones" guarda la columna modulo_id que apunta a este módulo.
    // @OrderColumn: guarda la posición de cada lección (columna "orden") para mantener el orden de la lista.
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "modulo_id")
    @OrderColumn(name = "orden")
    private List<Leccion> lecciones = new ArrayList<>();

    // Constructor vacío obligatorio para que Hibernate pueda crear el objeto al leer de MySQL.
    public Modulo() {
    }

    public Modulo(String titulo, List<Leccion> lecciones) {
        this.titulo = titulo;
        this.lecciones = new ArrayList<>(lecciones); // Copia en una lista que Hibernate pueda modificar
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public List<Leccion> getLecciones() { return lecciones; }
}
