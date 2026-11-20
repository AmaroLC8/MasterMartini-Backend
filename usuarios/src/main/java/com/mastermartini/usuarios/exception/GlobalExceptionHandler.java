package com.mastermartini.usuarios.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

// Manejador global de errores: transforma las excepciones en respuestas JSON con el formato
// {"mensaje": "...", "estado": 400} para que la app siempre reciba errores con la misma forma.
// @RestControllerAdvice: hace que esta clase intercepte las excepciones lanzadas por todos los controladores
// y que lo que retornen sus métodos se envíe como JSON.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Atrapa errores de negocio (400, 401, 403, 404, 409) lanzados desde el servicio.
    // @ExceptionHandler: indica qué tipo de excepción atiende este método.
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> manejarApiException(ApiException ex) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), ex.getStatus().value());
        return ResponseEntity.status(ex.getStatus()).body(body);
    }

    // Atrapa errores de validación (@Valid) y arma el mapa "campos" con código 400.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCampos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                erroresCampos.put(error.getField(), error.getDefaultMessage())
        );

        ErrorResponse body = new ErrorResponse("Datos inválidos", HttpStatus.BAD_REQUEST.value(), erroresCampos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Atrapa la falta del header X-Usuario-Id: sin saber quién pide la acción no se da permiso (403).
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> manejarHeaderFaltante(MissingRequestHeaderException ex) {
        ErrorResponse body = new ErrorResponse("No tienes permiso para realizar esta acción", HttpStatus.FORBIDDEN.value());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // Atrapa parámetros con un tipo inválido, como ?rol=OTRO o un ID que no es número (400).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> manejarTipoInvalido(MethodArgumentTypeMismatchException ex) {
        ErrorResponse body = new ErrorResponse(
                "El valor del parámetro '" + ex.getName() + "' no es válido", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Atrapa cuerpos JSON mal formados o con valores que no se pueden convertir (ej: "rol": "OTRO") (400).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonInvalido(HttpMessageNotReadableException ex) {
        ErrorResponse body = new ErrorResponse("El cuerpo de la solicitud no es un JSON válido", HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
