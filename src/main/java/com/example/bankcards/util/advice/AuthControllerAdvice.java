package com.example.bankcards.util.advice;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.exception.ExistingEmailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthControllerAdvice {

    @ExceptionHandler(ExistingEmailException.class)
    public ResponseEntity<SimpleResponseBody> handleException(ExistingEmailException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new SimpleResponseBody("Пользователь с таким email уже существует")
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<SimpleResponseBody> handleException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new SimpleResponseBody("Неправильный логин или пароль"));
    }
}
