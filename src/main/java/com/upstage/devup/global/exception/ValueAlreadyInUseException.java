package com.upstage.devup.global.exception;

public class ValueAlreadyInUseException extends RuntimeException {
    public ValueAlreadyInUseException(String message) {
        super(message);
    }
}
