package com.itassettracker.it_asset_tracker.exception;

import com.itassettracker.it_asset_tracker.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private ErrorResponse body(HttpStatus status, String message, HttpServletRequest req) {
        return new ErrorResponse(LocalDateTime.now(), status.value(), message, req.getRequestURI());
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
        return ResponseEntity.status(404).body(body(HttpStatus.NOT_FOUND, ex.getMessage(), req));
    }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> badRequest(Exception ex, HttpServletRequest req) {
        String message = ex instanceof MethodArgumentNotValidException m
            ? m.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage()).collect(Collectors.joining("; "))
            : ex.getMessage();
        return ResponseEntity.badRequest().body(body(HttpStatus.BAD_REQUEST, message, req));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> conflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(HttpStatus.CONFLICT, "A record with the same unique value already exists.", req));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> forbidden(AccessDeniedException ex, HttpServletRequest req) {
        return ResponseEntity.status(403).body(body(HttpStatus.FORBIDDEN, "Access denied.", req));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unexpected(Exception ex, HttpServletRequest req) {
        return ResponseEntity.status(500).body(body(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.", req));
    }
}
