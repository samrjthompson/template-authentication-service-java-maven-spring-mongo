package org.example.service;

import static org.example.Main.NAMESPACE;

import java.util.Optional;
import org.example.exception.ConflictException;
import org.example.exception.NotFoundException;
import org.example.mapper.CredentialsRequestMapper;
import org.example.mapper.CredentialsUpdateMapper;
import org.example.model.UserAuthorities;
import org.example.model.request.CredentialsRequest;
import org.example.repository.Repository;
import org.example.util.EncoderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CredentialsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private static final String FAILED_INSERT_MSG = "Failed to insert new user. Username [{}] already exists in DB.";

    private final CredentialsRequestMapper credentialsRequestMapper;
    private final CredentialsUpdateMapper credentialsUpdateMapper;
    private final Repository repository;

    public CredentialsService(CredentialsRequestMapper credentialsRequestMapper,
                              CredentialsUpdateMapper credentialsUpdateMapper, Repository repository) {
        this.credentialsRequestMapper = credentialsRequestMapper;
        this.credentialsUpdateMapper = credentialsUpdateMapper;
        this.repository = repository;
    }

    public void insertCredentials(CredentialsRequest requestBody) {
        LOGGER.info("Inserting credentials");
        final String username = requestBody.username();
        UsernameValidator.validate(username);
        UserAuthorities.validate(requestBody.authority());

        final String id = EncoderUtils.urlSafeBase64Encode(username);
        Optional.ofNullable(repository.findById(id))
                .ifPresentOrElse(user -> {
                    LOGGER.error(FAILED_INSERT_MSG, username);
                    throw new ConflictException("Username already present in DB");
                }, () -> repository.insert(credentialsRequestMapper.mapNewUser(id, requestBody)));
    }

    public void updateCredentials(CredentialsRequest requestBody) {
        LOGGER.info("Updating credentials");
        final String username = requestBody.username();
        UsernameValidator.validate(username);

        final String authority = requestBody.authority();
        if (StringUtils.hasText(authority)) {
            UserAuthorities.validate(authority);
        }

        final String id = EncoderUtils.urlSafeBase64Encode(username);
        Optional.ofNullable(repository.findById(id))
                .ifPresentOrElse(user -> {
                    LOGGER.info("User found - updating credentials");
                    repository.update(id, credentialsUpdateMapper.mapUpdate(requestBody));
                }, () -> {
                    LOGGER.error("Record not found in DB with id: [{}]", id);
                    throw new NotFoundException("Record not found in DB");
                });
    }
}