package com.mastermartini.interacciones.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

// Formato JSON de todos los errores: {"mensaje": "...", "estado": 400, "campos": {...}}.
// "campos" solo aparece en errores de validación, con el mensaje de cada campo inválido.
// @JsonInclude(NON_NULL): los campos nulos no se escriben en el JSON (por eso "campos" se omite cuando es null).
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String mensaje,
        int estado,
        Map<String, String> campos
) {

    // Constructor para errores sin detalle por campo.
    public ErrorResponse(String mensaje, int estado) {
        this(mensaje, estado, null);
    }
}
