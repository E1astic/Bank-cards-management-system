package com.example.bankcards.exception.user;

public class ExistingPhoneException extends RuntimeException {
    public ExistingPhoneException(String message) {
        super(message);
    }
}
