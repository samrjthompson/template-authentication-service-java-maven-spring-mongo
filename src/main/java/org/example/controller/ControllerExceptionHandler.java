package org.example.controller;

import static org.example.Main.NAMESPACE;

import org.example.exception.ConflictException;
import org.example.exception.InvalidAuthorityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @ExceptionHandler(InvalidAuthorityException.class)
    public ResponseEntity<Void> handleBadRequestException(Exception ex) {
        LOGGER.error("400 Bad request exception", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Void> handleConflictException(Exception ex) {
        LOGGER.error("409 Conflict exception", ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleUnknownException(Exception ex) {
        LOGGER.error("Unknown exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }
}
