package cl.veterinariasanmarcos.agenda.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErroresApi {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> negocio(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode())
                .body(Map.of("mensaje", error.getReason()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validacion(MethodArgumentNotValidException error) {
        List<String> errores = new ArrayList<>();
        for (var campo : error.getBindingResult().getFieldErrors()) {
            errores.add(campo.getField() + ": " + campo.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", "Revisa los datos enviados", "errores", errores));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> formato() {
        return ResponseEntity.badRequest()
                .body(Map.of("mensaje", "Revisa el formato de los datos. Fecha AAAA-MM-DD y hora HH:mm."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> conflicto() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", "El horario ya fue registrado o reservado"));
    }
}
