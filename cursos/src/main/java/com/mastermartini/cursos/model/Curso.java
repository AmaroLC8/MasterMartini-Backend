package com.mastermartini.cursos.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

// Entidad que representa un curso online (en la app se llama "Capacitacion").
// Se guarda en la tabla NUEVA "capacitaciones" para no chocar con la tabla vieja "cursos"
// que ya existe en las bases de datos de los integrantes.
// Un curso se arma así: Curso -> Módulos -> Lecciones.
// @Entity: marca la clase como entidad JPA, es decir, que se guarda en la base de datos.
// @Table: indica el nombre de la tabla.
@Entity
@Table(name = "capacitaciones")
public class Curso {

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

    // "Chocolatería", "Pastelería", "Panadería", "Heladería" o "Decoración".
    @Column(nullable = false)
    private String categoria;

    // "Básico", "Intermedio" o "Avanzado".
    @Column(nullable = false)
    private String nivel;

    // Modelo freemium: true = curso gratis; false = se desbloquea con el Plan Pro.
    @Column(nullable = false)
    private boolean esGratis;

    // Id del usuario instructor dueño del curso (vive en el microservicio usuarios).
    // Solo se guarda el número: cada microservicio tiene su propia base de datos.
    @Column(nullable = false)
    private Long instructorId;

    // Nombre del instructor que se muestra en la app.
    @Column(nullable = false)
    private String instructor;

    // Duración total como texto, ej. "2 h 5 min".
    @Column(nullable = false)
    private String duracionTotal;

    // Nombre exacto de un producto del catálogo de la app.
    @Column(nullable = false)
    private String insumoPrincipal;

    // Video corto que presenta el curso. Puede ser null: se subirá después a AWS S3.
    private String videoResumenUrl;

    // true si el curso usa solo ingredientes veganos.
    @Column(nullable = false)
    private boolean esVegano;

    // Lista de logros del curso ("Templar chocolate sin termómetro...").
    // @ElementCollection: guarda una lista de valores simples (String) en una tabla aparte,
    //   sin crear una entidad. Por defecto es LAZY.
    // @CollectionTable: nombre de esa tabla y la columna (curso_id) que la une con el curso.
    // @OrderColumn: guarda la posición de cada texto (columna "orden") para mantener el orden.
    // @Column(name = "texto"): nombre de la columna donde va cada texto.
    @ElementCollection
    @CollectionTable(name = "capacitacion_logros", joinColumns = @JoinColumn(name = "curso_id"))
    @OrderColumn(name = "orden")
    @Column(name = "texto", nullable = false)
    private List<String> loQueLograras = new ArrayList<>();

    // Lo que trae el curso (certificado, acceso sin límite, recetario...). Igual que loQueLograras.
    @ElementCollection
    @CollectionTable(name = "capacitacion_incluye", joinColumns = @JoinColumn(name = "curso_id"))
    @OrderColumn(name = "orden")
    @Column(name = "texto", nullable = false)
    private List<String> incluye = new ArrayList<>();

    // Módulos del curso, en orden.
    // @OneToMany: un curso tiene muchos módulos.
    //   cascade = ALL: al guardar o borrar el curso, también se guardan o borran sus módulos (y sus lecciones).
    //   orphanRemoval = true: si un módulo se saca de la lista, se borra de la base de datos.
    //   Por defecto es LAZY: los módulos se cargan recién cuando se usan.
    // @JoinColumn: la tabla "modulos" guarda la columna curso_id que apunta a este curso.
    // @OrderColumn: guarda la posición de cada módulo (columna "orden").
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "curso_id")
    @OrderColumn(name = "orden")
    private List<Modulo> modulos = new ArrayList<>();

    // Constructor vacío obligatorio para que Hibernate pueda crear el objeto al leer de MySQL.
    public Curso() {
    }

    // Constructor con todos los datos, usado al crear un curso y en DataSeeder (el id lo genera MySQL).
    public Curso(String titulo, String descripcion, String categoria, String nivel, boolean esGratis,
                 Long instructorId, String instructor, String duracionTotal, String insumoPrincipal,
                 String videoResumenUrl, boolean esVegano, List<String> loQueLograras,
                 List<String> incluye, List<Modulo> modulos) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.nivel = nivel;
        this.esGratis = esGratis;
        this.instructorId = instructorId;
        this.instructor = instructor;
        this.duracionTotal = duracionTotal;
        this.insumoPrincipal = insumoPrincipal;
        this.videoResumenUrl = videoResumenUrl;
        this.esVegano = esVegano;
        // Se copian en listas nuevas que Hibernate pueda modificar (List.of no se puede modificar).
        this.loQueLograras = new ArrayList<>(loQueLograras);
        this.incluye = new ArrayList<>(incluye);
        this.modulos = new ArrayList<>(modulos);
    }

    // --- Getters y Setters ---
    // Las listas no tienen setter: para cambiarlas se usa clear() y addAll() sobre la misma lista,
    // porque Hibernate necesita seguir usando el mismo objeto lista (si no, falla orphanRemoval).
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public boolean isEsGratis() { return esGratis; }
    public void setEsGratis(boolean esGratis) { this.esGratis = esGratis; }

    public Long getInstructorId() { return instructorId; }
    public void setInstructorId(Long instructorId) { this.instructorId = instructorId; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }

    public String getDuracionTotal() { return duracionTotal; }
    public void setDuracionTotal(String duracionTotal) { this.duracionTotal = duracionTotal; }

    public String getInsumoPrincipal() { return insumoPrincipal; }
    public void setInsumoPrincipal(String insumoPrincipal) { this.insumoPrincipal = insumoPrincipal; }

    public String getVideoResumenUrl() { return videoResumenUrl; }
    public void setVideoResumenUrl(String videoResumenUrl) { this.videoResumenUrl = videoResumenUrl; }

    public boolean isEsVegano() { return esVegano; }
    public void setEsVegano(boolean esVegano) { this.esVegano = esVegano; }

    public List<String> getLoQueLograras() { return loQueLograras; }

    public List<String> getIncluye() { return incluye; }

    public List<Modulo> getModulos() { return modulos; }
}
