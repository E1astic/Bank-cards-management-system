package com.example.bankcards.util.validator;

import com.example.bankcards.dto.SimpleResponseBody;
import com.example.bankcards.exception.card.BlockedCardException;
import com.example.bankcards.exception.card.CardNotFoundException;
import com.example.bankcards.exception.card.ExpiredCardException;
import com.example.bankcards.exception.card.InactiveCardException;
import com.example.bankcards.exception.card.IncorrectCardNumberException;
import com.example.bankcards.exception.card.IncorrectCardStatusException;
import com.example.bankcards.exception.card.InsufficientFundsOnCardException;
import com.example.bankcards.exception.crypto.DecryptionException;
import com.example.bankcards.exception.crypto.EncryptionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class CardControllerAdvice {

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

    @ExceptionHandler(BlockedCardException.class)
    public ResponseEntity<SimpleResponseBody> handleException(BlockedCardException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(ExpiredCardException.class)
    public ResponseEntity<SimpleResponseBody> handleException(ExpiredCardException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(InactiveCardException.class)
    public ResponseEntity<SimpleResponseBody> handleException(InactiveCardException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsOnCardException.class)
    public ResponseEntity<SimpleResponseBody> handleException(InsufficientFundsOnCardException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new SimpleResponseBody("На карте недостаточно средств для списания"));
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity<SimpleResponseBody> handleException(EncryptionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(DecryptionException.class)
    public ResponseEntity<SimpleResponseBody> handleException(DecryptionException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new SimpleResponseBody(e.getMessage()));
    }

    @ExceptionHandler(IncorrectCardNumberException.class)
    public ResponseEntity<SimpleResponseBody> handleException(IncorrectCardNumberException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new SimpleResponseBody("Некорректный номер карты"));
    }
}
