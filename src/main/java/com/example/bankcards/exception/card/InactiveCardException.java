package com.example.bankcards.exception.card;

public class InactiveCardException extends RuntimeException {
    public InactiveCardException(String message) {
        super(message);
    }
}
