package com.teamarrow.mirakuru.infrastructure.adapter.in.web.error;

import com.teamarrow.mirakuru.domain.exception.AffectedNotFoundException;
import com.teamarrow.mirakuru.domain.exception.DomainException;
import com.teamarrow.mirakuru.domain.exception.DuplicateAffectedException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Translates exceptions into HTTP responses in one central place.
 *
 * <p>This is where the architectural boundary pays off: the domain throws
 * technology-agnostic exceptions and this adapter maps each to the right status
 * code. The domain never imports anything from {@code jakarta.servlet} or
 * {@code org.springframework.http}; the dependency points inward only.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** A missing aggregate is a 404. */
    @ExceptionHandler(AffectedNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(AffectedNotFoundException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /** A code-name clash is a 409. */
    @ExceptionHandler(DuplicateAffectedException.class)
    public ResponseEntity<ApiError> handleDuplicate(DuplicateAffectedException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /** Any other broken business rule is a 400. */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiError> handleDomain(DomainException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /** Bean Validation failures become a 400 with per-field detail. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        List<ApiError.FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ApiError.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        ApiError body = ApiError.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed for the request body",
                request.getRequestURI(),
                fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, HttpServletRequest request) {
        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}
