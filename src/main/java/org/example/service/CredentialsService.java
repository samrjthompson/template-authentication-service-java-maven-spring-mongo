package org.example.service;

import static org.example.Main.NAMESPACE;

import java.util.Optional;
import org.example.mapper.CredentialsRequestMapper;
import org.example.model.UserAuthorities;
import org.example.model.request.CredentialsRequest;
import org.example.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CredentialsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private final CredentialsRequestMapper credentialsRequestMapper;
    private final Repository repository;

    public CredentialsService(CredentialsRequestMapper credentialsRequestMapper, Repository repository) {
        this.credentialsRequestMapper = credentialsRequestMapper;
        this.repository = repository;
    }

    public void insertCredentials(CredentialsRequest requestBody) {
        LOGGER.info("Inserting credentials");

        validateAuthorityString(requestBody.authority());

        final String username = requestBody.username();
        Optional.ofNullable(repository.findByUsername(username))
                .ifPresentOrElse(user -> {
                    LOGGER.info("Failed to insert new user. Username [{}] already exists in DB.", username);
                    throw new RuntimeException();
                }, () -> repository.save(credentialsRequestMapper.mapNewUser(requestBody)));
    }

    public void updateCredentials(CredentialsRequest requestBody) {
        LOGGER.info("Updating credentials");

        validateAuthorityString(requestBody.authority());

        final String username = requestBody.username();
        Optional.ofNullable(repository.findByUsername(username))
                .ifPresentOrElse(user -> {
                    LOGGER.info("Failed to insert new user. Username [{}] already exists in DB.", username);
                    throw new RuntimeException();
                }, () -> repository.save(credentialsRequestMapper.mapNewUser(requestBody)));
    }

    private static void validateAuthorityString(final String authority) {
        UserAuthorities.validate(authority);
    }
}
