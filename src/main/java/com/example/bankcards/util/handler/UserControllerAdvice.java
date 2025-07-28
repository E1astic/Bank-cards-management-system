package com.example.bankcards.util.handler;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.exception.user.ExistingPhoneException;
import com.example.bankcards.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserControllerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<SimpleResponseBody> handleException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(ExistingPhoneException.class)
    public ResponseEntity<SimpleResponseBody> handleException(ExistingPhoneException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponseBody(e.getMessage()));
    }
}
