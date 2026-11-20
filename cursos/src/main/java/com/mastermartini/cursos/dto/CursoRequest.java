package com.mastermartini.cursos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

// Datos que se envían para crear (POST) o editar (PUT) un curso.
// Son los mismos campos de CursoResponse, sin "id" (lo genera MySQL) ni "instructorId"
// (se toma del usuario que hace la petición, header X-Usuario-Id).
// @NotBlank: el campo no puede venir nulo, vacío ni solo con espacios.
// @Pattern: el texto debe calzar con la expresión regular (aquí, una de las opciones permitidas).
// @NotEmpty: la lista no puede venir nula ni vacía.
// @Valid: valida también cada elemento de la lista con sus propias reglas (ModuloDto y LeccionDto).
public record CursoRequest(
        @NotBlank(message = "El título es obligatorio")
        String titulo,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @NotBlank(message = "La categoría es obligatoria")
        @Pattern(regexp = "Chocolatería|Pastelería|Panadería|Heladería|Decoración",
                message = "La categoría debe ser Chocolatería, Pastelería, Panadería, Heladería o Decoración")
        String categoria,

        @NotBlank(message = "El nivel es obligatorio")
        @Pattern(regexp = "Básico|Intermedio|Avanzado",
                message = "El nivel debe ser Básico, Intermedio o Avanzado")
        String nivel,

        // Si no viene en el JSON queda en false.
        boolean esGratis,

        @NotBlank(message = "El instructor es obligatorio")
        String instructor,

        @NotBlank(message = "La duración total es obligatoria")
        String duracionTotal,

        @NotBlank(message = "El insumo principal es obligatorio")
        String insumoPrincipal,

        // Opcional: null mientras no se suban los videos a AWS S3.
        String videoResumenUrl,

        // Si no viene en el JSON queda en false.
        boolean esVegano,

        // Opcionales: si no vienen, se guardan como listas vacías.
        List<String> loQueLograras,
        List<String> incluye,

        @NotEmpty(message = "El curso debe tener al menos 1 módulo")
        @Valid
        List<ModuloDto> modulos
) {}
