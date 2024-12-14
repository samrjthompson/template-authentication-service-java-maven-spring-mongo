package org.example.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UsernameValidatorTest {

    @ParameterizedTest
    @CsvSource({
            "bob@example.com",
            "bob@example.co.uk",
            "bob@example.org",
            "bob@example.uk",
            "bob_test@example.com",
            "bob_test@example.co.uk",
            "bob_test@example.org",
            "bob_test@example.uk",
            "bob_test@organisation.com",
            "bob_test@organisation.co.uk",
            "bob_test@organisation.org",
            "bob_test@organisation.uk",
            "bob-test@example.com",
            "bob-test@example.co.uk",
            "bob-test@example.org",
            "bob-test@example.uk",
    })
    void shouldMatchOnValidUsername(final String validUsername) {
        assertDoesNotThrow(() -> UsernameValidator.validate(validUsername));
    }

    @ParameterizedTest
    @CsvSource({
            "bob",
            "bobexample.com",
            "bob@example",
            "bob@example.me",
            "_@example.com",
            " @example.com",
            "-@example.com",
            "_bob@example.com",
            "-bob@example.com",
            "bob_@example.com",
            "bob @example.com",
            "bob-@example.com",
    })
    void shouldFailToMatchOnInvalidUsername(final String invalidUsername) {
        assertThrows(BadRequestException.class, () -> UsernameValidator.validate(invalidUsername));
    }

    @Test
    void shouldFailIfWhitespaceAtStartOfUsername() {
        assertThrows(BadRequestException.class, () -> UsernameValidator.validate(" bob@example.com"));
    }
}
