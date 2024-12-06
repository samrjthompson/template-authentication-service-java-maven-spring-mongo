package org.example.exception;

public class InvalidAuthorityException extends RuntimeException {

    public InvalidAuthorityException(String message) {
        super(message);
    }
}
