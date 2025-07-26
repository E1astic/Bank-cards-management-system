package com.example.bankcards.exception.card;

public class BlockedCardException extends RuntimeException {
    public BlockedCardException(String message) {
        super(message);
    }
}
