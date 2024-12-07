package org.example.exception;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException(String message) {
        super(message);
    }
}
