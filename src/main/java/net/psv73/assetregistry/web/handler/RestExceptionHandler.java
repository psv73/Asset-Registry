package net.psv73.assetregistry.web.handler;

import net.psv73.assetregistry.web.response.ErrorResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "bad_request", ex.getMessage(), req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fields = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fields.put(err.getField(), err.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST, "validation_failed", "Validation failed", req, fields);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> notFound(NoSuchElementException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, "not_found", ex.getMessage(), req, null);
    }

    // Фоллбек на неожиданные ошибки -> 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> internal(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "internal_error", ex.getMessage(), req, null);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> duplicate(
            org.springframework.dao.DataIntegrityViolationException ex,
            jakarta.servlet.http.HttpServletRequest req
    ) {
        // вытащим ConstraintViolationException из цепочки причин
        String constraint = null;
        Throwable t = ex;
        while (t != null) {
            if (t instanceof org.hibernate.exception.ConstraintViolationException cve) {
                constraint = cve.getConstraintName();
                break;
            }
            t = t.getCause();
        }

        java.util.Map<String, String> fields = new java.util.LinkedHashMap<>();
        String message = "Duplicate key";

        if ("uq_assets_inventory_code".equalsIgnoreCase(constraint)) {
            fields.put("inventoryCode", "already exists");
            message = "inventoryCode already exists";
        } else if ("uq_assets_serial_number".equalsIgnoreCase(constraint)) {
            fields.put("serialNumber", "already exists");
            message = "serialNumber already exists";
        }

        return build(org.springframework.http.HttpStatus.CONFLICT,
                "duplicate_key", message, req, fields.isEmpty() ? null : fields);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message,
                                                HttpServletRequest req, Map<String, String> fields) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(code)
                .message(message)
                .path(req.getRequestURI())
                .fields(fields)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
