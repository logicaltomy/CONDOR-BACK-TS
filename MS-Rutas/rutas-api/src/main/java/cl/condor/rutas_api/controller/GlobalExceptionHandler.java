package cl.condor.rutas_api.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private String translateMessage(String msg) {
        if (msg == null) return "Ha ocurrido un error.";
        String lower = msg.toLowerCase();
        if (lower.contains("must not be null") || lower.contains("must not be empty") || lower.contains("not be null")) {
            return "Falta un valor obligatorio (campo nulo). Revisa los datos enviados.";
        }
        if (lower.contains("not found") || lower.contains("no encontrada") || lower.contains("no encontrado")) {
            return "Recurso no encontrado.";
        }
        if (lower.contains("constraint") || lower.contains("unique") || lower.contains("duplicate")) {
            return "Violación de restricción de datos (posible duplicado o valor inválido).";
        }
        if (lower.contains("service unavailable") || lower.contains("unavailable")) {
            return "Servicio externo no disponible.";
        }
        // Default: return original message but in Spanish prefix for clarity
        return msg;
    }

    private ResponseEntity<Map<String,String>> body(String message, HttpStatus status) {
        Map<String,String> m = new HashMap<>();
        m.put("message", message);
        return ResponseEntity.status(status).body(m);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,String>> handleResponseStatus(ResponseStatusException ex) {
        String translated = translateMessage(ex.getReason());
        return body(translated != null ? translated : "Error en la solicitud.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidation(MethodArgumentNotValidException ex) {
        return body("Datos inválidos en la solicitud. Revisa los campos obligatorios.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,String>> handleDataIntegrity(DataIntegrityViolationException ex) {
        return body("Error en la integridad de los datos. Revisa restricciones y longitudes.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handleRuntime(RuntimeException ex) {
        String message = ex.getMessage();
        String translated = translateMessage(message);
        return body(translated != null ? translated : "Error interno.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleAll(Exception ex) {
        return body("Error interno del servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
