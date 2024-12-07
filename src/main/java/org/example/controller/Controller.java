package org.example.controller;

import static org.example.Main.NAMESPACE;

import org.example.model.request.CredentialsRequest;
import org.example.service.CredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private final CredentialsService credentialsService;

    public Controller(CredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> insertCredentials(
            @RequestBody CredentialsRequest requestBody) {
        LOGGER.info("Received insert credentials request");

        credentialsService.insertCredentials(requestBody);

        LOGGER.info("Successfully inserted credentials");
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/register")
    public ResponseEntity<Void> updateCredentials() {
        LOGGER.info("Received update credentials request");

        LOGGER.info("Successfully updated credentials");
        return ResponseEntity.ok().build();
    }
}