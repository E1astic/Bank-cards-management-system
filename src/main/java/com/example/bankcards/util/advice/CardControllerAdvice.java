package com.example.bankcards.util.advice;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.IncorrectCardStatusException;
import com.example.bankcards.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class CardControllerAdvice {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<SimpleResponseBody> handleException(UserNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<SimpleResponseBody> handleException(CardNotFoundException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(IncorrectCardStatusException.class)
    public ResponseEntity<SimpleResponseBody> handleException(IncorrectCardStatusException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new SimpleResponseBody("Некорректный статус карты"));
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<SimpleResponseBody> handleException(DateTimeParseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new SimpleResponseBody("Некорректный формат даты"));
    }
}
