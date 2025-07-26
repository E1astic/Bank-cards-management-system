package com.example.bankcards.exception.user;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super();
    }
    public UserNotFoundException(String message) {
        super(message);
    }
}
