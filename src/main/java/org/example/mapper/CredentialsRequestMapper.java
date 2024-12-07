package org.example.mapper;

import static org.example.mapper.PasswordGenerator.PASSWORD_KEY;
import static org.example.mapper.PasswordGenerator.SALT_KEY;

import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import org.example.model.Created;
import org.example.model.Updated;
import org.example.model.User;
import org.example.model.request.CredentialsRequest;
import org.example.util.EncoderUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CredentialsRequestMapper {

    private final PasswordGenerator passwordGenerator;
    private final Supplier<Instant> instantSupplier;

    public CredentialsRequestMapper(PasswordGenerator passwordGenerator, Supplier<Instant> instantSupplier) {
        this.passwordGenerator = passwordGenerator;
        this.instantSupplier = instantSupplier;
    }

    public User mapNewUser(CredentialsRequest credentialsRequest) {
        final String username = credentialsRequest.username();
        final String encodedId = EncoderUtils.encodeUsernameIntoMongoId(username);

        Map<String, String> credentialsMap = passwordGenerator.generateHashedPasswordWithSalt(
                credentialsRequest.password());

        final Instant now = instantSupplier.get();

        return new User()
                .id(encodedId)
                .username(username)
                .password(credentialsMap.get(PASSWORD_KEY))
                .salt(credentialsMap.get(SALT_KEY))
                .authority(credentialsRequest.authority())
                .enabled(true)
                .created(new Created()
                        .at(now)
                        .by(MDC.get("request_id")))
                .updated(new Updated()
                        .at(now)
                        .by(MDC.get("request_id")));
    }

    public User mapExistingUser(CredentialsRequest credentialsRequest) {
        return new User()
                .username(credentialsRequest.username())
                .password(credentialsRequest.password())
                .authority(credentialsRequest.authority())
                .enabled(true);
    }
}
