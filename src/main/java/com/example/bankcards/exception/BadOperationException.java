package com.example.bankcards.exception;

public class BadOperationException extends RuntimeException {
    public BadOperationException(String message) {
        super(message);
    }
}
