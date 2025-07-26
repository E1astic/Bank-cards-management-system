package com.example.bankcards.exception.card;

public class ExpiredCardException extends RuntimeException {
    public ExpiredCardException(String message) {
        super(message);
    }
}
