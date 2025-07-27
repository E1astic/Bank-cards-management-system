package com.example.bankcards.util.validator;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ValidationViolationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ViolationResponseBody> onConstraintValidationException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors()
                .stream()
                .map(err -> new Violation(
                        err.getField(),
                        err.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ViolationResponseBody(violations));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ViolationResponseBody> onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations()
                .stream()
                .map(err -> new Violation(
                        err.getPropertyPath().toString(),
                        err.getMessage()))
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ViolationResponseBody(violations));
    }
}
