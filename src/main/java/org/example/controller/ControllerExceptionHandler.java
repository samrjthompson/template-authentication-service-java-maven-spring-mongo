package org.example.controller;

import static org.example.Main.NAMESPACE;

import org.example.exception.BadAlgorithmException;
import org.example.exception.BadGatewayException;
import org.example.exception.ConflictException;
import org.example.exception.InvalidAuthorityException;
import org.example.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Void> handleUnauthorizedException(Exception ex) {
        LOGGER.error("401 Unauthorized exception", ex);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleForbiddenException(Exception ex) {
        LOGGER.error("403 Forbidden exception", ex);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .build();
    }

    @ExceptionHandler(BadAlgorithmException.class)
    public ResponseEntity<Void> handleInternalServerErrorException(Exception ex) {
        LOGGER.error("500 Internal server error exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Void> handleNotFoundException(Exception ex) {
        LOGGER.error("404 Not Found exception", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .build();
    }

    @ExceptionHandler(BadGatewayException.class)
    public ResponseEntity<Void> handleBadGatewayException(Exception ex) {
        LOGGER.error("502 Bad Gateway exception", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .build();
    }

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
