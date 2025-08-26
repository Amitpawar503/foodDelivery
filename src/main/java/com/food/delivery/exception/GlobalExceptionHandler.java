package com.food.delivery.exception;

import com.food.delivery.constants.ErrorConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse(ErrorConstants.VALIDATION_ERROR_MESSAGE);
        return build(HttpStatus.BAD_REQUEST, ErrorConstants.VALIDATION_ERROR_CODE, message, req.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ErrorConstants.VALIDATION_ERROR_CODE, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegal(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ErrorConstants.VALIDATION_ERROR_CODE, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiError> handleSecurity(SecurityException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, ErrorConstants.AUTHORIZATION_ERROR_CODE, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRuntime(RuntimeException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ErrorConstants.BUSINESS_LOGIC_ERROR_CODE, ex.getMessage(), req.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorConstants.SYSTEM_ERROR_CODE, ErrorConstants.UNEXPECTED_ERROR_MESSAGE, req.getRequestURI());
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String errorCode, String message, String path) {
        return ResponseEntity.status(status).body(new ApiError(
                LocalDateTime.now(), status.value(), errorCode, message, path
        ));
    }
}
