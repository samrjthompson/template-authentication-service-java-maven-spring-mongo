package org.example.mapper;

import java.util.Map;
import org.example.security.SaltGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class PasswordGenerator {

    public static final String PASSWORD_KEY = "hashed_password";
    public static final String SALT_KEY = "salt";

    private final SaltGenerator saltGenerator;
    private final PasswordEncoder passwordEncoder;

    public PasswordGenerator(SaltGenerator saltGenerator, PasswordEncoder passwordEncoder) {
        this.saltGenerator = saltGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, String> generateHashedPasswordWithSalt(final String rawPassword) {
        final String salt = saltGenerator.generateKey();
        final String passwordWithSalt = "%s%s".formatted(rawPassword, salt);

        return Map.ofEntries(
                Map.entry(PASSWORD_KEY, passwordEncoder.encode(passwordWithSalt)),
                Map.entry(SALT_KEY, salt)
        );
    }
}
