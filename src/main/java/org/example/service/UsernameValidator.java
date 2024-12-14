package org.example.service;

import static org.example.Main.NAMESPACE;

import java.util.regex.Pattern;
import org.example.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsernameValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s_-][a-zA-Z_-]+[^\\s_-]@[a-zA-Z]+\\.(com|co.uk|org|uk)+$");

    private UsernameValidator() {
    }

    public static void validate(final String username) {
        if (!EMAIL_PATTERN.matcher(username).matches()) {
            final String msg = "Username [%s] did not match required format".formatted(username);
            LOGGER.error(msg);
            throw new BadRequestException(msg);
        }
        LOGGER.info("Username validated");
    }
}
