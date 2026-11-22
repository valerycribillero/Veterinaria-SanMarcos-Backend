package cl.veterinariasanmarcos.pedidos.controller;

import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ErroresApi {

    @ExceptionHandler(cl.veterinariasanmarcos.pedidos.service.PedidoPendienteException.class)
    ResponseEntity<?> pendiente(
            cl.veterinariasanmarcos.pedidos.service.PedidoPendienteException error) {
        return ResponseEntity.status(error.getEstadoHttp())
                .body(Map.of(
                    "mensaje",
                    error.getMessage(),
                    "pedidoId",
                    error.getPedidoId()
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<?> negocio(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode())
                .body(
                        Map.of(
                            "mensaje",
                            error.getReason() == null
                                        ? "Solicitud rechazada"
                                        : error.getReason()
                        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validacion(MethodArgumentNotValidException error) {
        List<String> detalles =
                error.getBindingResult().getFieldErrors().stream()
                        .map(e -> e.getField() + ": " + e.getDefaultMessage())
                        .toList();
        return ResponseEntity.badRequest()
                .body(Map.of(
                    "mensaje",
                    "Revisa los datos enviados",
                    "errores",
                    detalles
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> jsonInvalido() {
        return ResponseEntity.badRequest()
                .body(
                        Map.of(
                            "mensaje",
                            "JSON inválido: revisa campos, tipos de datos y valores permitidos"
                        ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<?> conflicto() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "mensaje",
                    "Los datos entran en conflicto con un registro existente"
                ));
    }
}
