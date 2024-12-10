package org.example.mapper;

import static org.example.mapper.PasswordGenerator.PASSWORD_KEY;
import static org.example.mapper.PasswordGenerator.SALT_KEY;

import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import org.example.model.Updated;
import org.example.model.UserAuthorities;
import org.example.model.request.CredentialsRequest;
import org.slf4j.MDC;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CredentialsUpdateMapper {

    private final PasswordGenerator passwordGenerator;
    private final Supplier<Instant> instantSupplier;

    public CredentialsUpdateMapper(PasswordGenerator passwordGenerator, Supplier<Instant> instantSupplier) {
        this.passwordGenerator = passwordGenerator;
        this.instantSupplier = instantSupplier;
    }

    public Update mapUpdate(CredentialsRequest credentialsRequest) {
        Update update = new Update();
        if (StringUtils.hasText(credentialsRequest.password())) {
            Map<String, String> credentialsMap =
                    passwordGenerator.generateHashedPasswordWithSalt(credentialsRequest.password());
            update.set("password", credentialsMap.get(PASSWORD_KEY));
            update.set("salt", credentialsMap.get(SALT_KEY));
        }
        if (StringUtils.hasText(credentialsRequest.authority())) {
            UserAuthorities.validate(credentialsRequest.authority());
            update.set("authority", credentialsRequest.authority());
        }
        update.set("enabled", credentialsRequest.isEnabled());
        update.set("updated", new Updated()
                .at(instantSupplier.get())
                .by(MDC.get("request_id"))
        );

        return update;
    }
}