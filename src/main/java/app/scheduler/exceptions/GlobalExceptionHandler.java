package app.scheduler.exceptions;

import app.scheduler.services.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.UncategorizedSQLException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final LoggerService loggerService;

    public GlobalExceptionHandler(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", message);
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        loggerService.error("ResourceNotFoundException: " + ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        loggerService.error("ValidationException: " + ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex) {
        loggerService.error("AuthenticationException: " + ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler({DataIntegrityViolationException.class, UncategorizedSQLException.class})
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(Exception ex) {
        String msg = ex.getMessage();
        if (msg != null && msg.contains("UNIQUE constraint failed")) {
            // Extract the table.column
            String details = msg.substring(msg.indexOf("UNIQUE constraint failed"));
            details = details.split(";")[0].split("\\]")[0]; // Clean it up a bit
            return buildResponse(HttpStatus.CONFLICT, "Duplicate entry: " + details);
        } else if (msg != null && msg.contains("FOREIGN KEY constraint failed")) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Invalid reference: A provided ID does not exist in the database.");
        } else if (msg != null && msg.contains("NOT NULL constraint failed")) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Missing required field.");
        }
        loggerService.logError("DataIntegrityViolationException: " + msg);
        return buildResponse(HttpStatus.BAD_REQUEST, "Database constraint violation.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        loggerService.error("Malformed JSON payload: " + ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request payload. Ensure all data types and formats (like Dates) are correct.");
    }

    @ExceptionHandler(SchedulerException.class)
    public ResponseEntity<Map<String, Object>> handleSchedulerException(SchedulerException ex) {
        loggerService.error("SchedulerException: " + ex.getMessage());
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        loggerService.logError("Unhandled Exception: " + ex.getMessage());
        ex.printStackTrace();
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }
}
