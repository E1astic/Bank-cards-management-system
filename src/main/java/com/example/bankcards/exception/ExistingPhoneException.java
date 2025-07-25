package com.example.bankcards.exception;

public class ExistingPhoneException extends RuntimeException {
    public ExistingPhoneException(String message) {
        super(message);
    }
}
