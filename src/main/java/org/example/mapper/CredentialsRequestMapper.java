package org.example.mapper;

import static org.example.mapper.PasswordGenerator.PASSWORD_KEY;
import static org.example.mapper.PasswordGenerator.SALT_KEY;

import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.example.model.User;
import org.example.model.request.CredentialsRequest;
import org.springframework.stereotype.Component;

@Component
public class CredentialsRequestMapper {

    private final PasswordGenerator passwordGenerator;

    public CredentialsRequestMapper(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public User mapNewUser(CredentialsRequest credentialsRequest) {
        final String encodedId = new String(
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encode(UUID.randomUUID().toString().getBytes()));

        Map<String, String> credentialsMap = passwordGenerator.generateHashedPasswordWithSalt(credentialsRequest.password());

        return new User()
                .id(encodedId)
                .username(credentialsRequest.username())
                .password(credentialsMap.get(PASSWORD_KEY))
                .salt(credentialsMap.get(SALT_KEY))
                .authority(credentialsRequest.authority())
                .enabled(true);
    }

    public User mapExistingUser(CredentialsRequest credentialsRequest) {
        return new User()
                .username(credentialsRequest.username())
                .password(credentialsRequest.password())
                .authority(credentialsRequest.authority())
                .enabled(true);
    }
}
