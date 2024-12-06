package org.example.controller;

import static org.example.Main.NAMESPACE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    @GetMapping("/healthcheck")
    public ResponseEntity<Void> healthcheck() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        LOGGER.info("Calling hello GET endpoint");

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        final String response = "Hello %s! %n".formatted(authentication.getName());

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/hello/private")
    public ResponseEntity<String> helloPrivate() {
        LOGGER.info("Calling hello private endpoint");

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        final String response = "Hello %s! %n".formatted(authentication.getName());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/hello")
    public ResponseEntity<String> helloPost() {
        LOGGER.info("Calling hello POST endpoint");
        return ResponseEntity.ok().body("POST Hello!");
    }

}