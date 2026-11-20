package com.mastermartini.usuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// Entidad que representa la tabla "usuarios" en MySQL: cada objeto Usuario es una fila.
// Hibernate crea o actualiza la tabla automáticamente (spring.jpa.hibernate.ddl-auto=update).
// @Entity: marca la clase como entidad JPA, es decir, que se guarda en la base de datos.
// @Table: indica el nombre de la tabla.
@Entity
@Table(name = "usuarios")
public class Usuario {

    // @Id: llave primaria de la tabla. @GeneratedValue: MySQL genera el valor automáticamente.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincremental: 1, 2, 3, 4...
    private Long id;

    // @Column: configura la columna (nullable = false equivale a NOT NULL, unique impide repetidos).
    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true, length = 12) // Impide RUTs repetidos en BD; se guarda sin puntos y con K mayúscula
    private String rut;

    @Column(nullable = false, unique = true) // Impide correos repetidos en BD; se guarda en minúsculas
    private String correo;

    @Column(nullable = false, length = 9)
    private String telefono;

    @Column(nullable = false)
    private String rubro;

    @Column(nullable = false)
    private String personasNegocio;

    @Column(nullable = false)
    private String password; // Guardará el hash BCrypt, nunca la clave real

    @Enumerated(EnumType.STRING) // Guarda "CLIENTE" como texto en MySQL, no como número 0
    @Column(nullable = false)
    private Rol rol = Rol.CLIENTE;

    @Column(nullable = false)
    private boolean planPro = false;

    // Se llenan solo cuando el usuario pide recuperar su contraseña
    private String codigoRecuperacion;
    private LocalDateTime codigoExpira;

    // Constructor vacío obligatorio para que Hibernate pueda leer filas desde MySQL
    public Usuario() {
    }

    // Constructor con todos los datos, usado al registrar y en DataSeeder (el id lo genera MySQL).
    public Usuario(String nombre, String rut, String correo, String telefono,
                   String rubro, String personasNegocio, String password,
                   Rol rol, boolean planPro) {
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
        this.telefono = telefono;
        this.rubro = rubro;
        this.personasNegocio = personasNegocio;
        this.password = password;
        this.rol = rol;
        this.planPro = planPro;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }

    public String getPersonasNegocio() { return personasNegocio; }
    public void setPersonasNegocio(String personasNegocio) { this.personasNegocio = personasNegocio; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public boolean isPlanPro() { return planPro; }
    public void setPlanPro(boolean planPro) { this.planPro = planPro; }

    public String getCodigoRecuperacion() { return codigoRecuperacion; }
    public void setCodigoRecuperacion(String codigoRecuperacion) { this.codigoRecuperacion = codigoRecuperacion; }

    public LocalDateTime getCodigoExpira() { return codigoExpira; }
    public void setCodigoExpira(LocalDateTime codigoExpira) { this.codigoExpira = codigoExpira; }
}
