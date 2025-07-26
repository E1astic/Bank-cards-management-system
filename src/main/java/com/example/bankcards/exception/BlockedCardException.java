package com.example.bankcards.exception;

public class BlockedCardException extends RuntimeException {
    public BlockedCardException(String message) {
        super(message);
    }
}
