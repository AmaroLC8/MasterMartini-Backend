package com.mastermartini.cursos.exception;

import org.springframework.http.HttpStatus;

// Excepción propia para los errores de negocio: guarda el mensaje y el código HTTP que se debe responder
// (por ejemplo 404 "Curso no encontrado"). GlobalExceptionHandler la convierte en JSON.
// Extiende RuntimeException para no tener que declararla con "throws" en cada método.
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensaje) {
        super(mensaje);
        this.status = status;
    }

    // Código HTTP que se enviará en la respuesta.
    public HttpStatus getStatus() {
        return status;
    }
}
