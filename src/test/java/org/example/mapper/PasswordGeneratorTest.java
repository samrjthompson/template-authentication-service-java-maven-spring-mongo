package org.example.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.example.security.SaltGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class PasswordGeneratorTest {

    private static final String RAW_PASSWORD = "raw_password";
    private static final String ENCODED_PASSWORD = "encoded_password";
    private static final String SALT = "salt";

    @InjectMocks
    private PasswordGenerator passwordGenerator;

    @Mock
    private SaltGenerator saltGenerator;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldGenerateHashedPasswordWithSalt() {
        // given
        when(saltGenerator.generateKey()).thenReturn(SALT);
        when(passwordEncoder.encode(any())).thenReturn(ENCODED_PASSWORD);

        Map<String, String> expected = Map.ofEntries(
                Map.entry("hashed_password", ENCODED_PASSWORD),
                Map.entry("salt", SALT)
        );
        final String expectedRawPasswordWithSalt = "%s%s".formatted(RAW_PASSWORD, SALT);

        // when
        Map<String, String> actual = passwordGenerator.generateHashedPasswordWithSalt(RAW_PASSWORD);

        // then
        assertEquals(expected, actual);
        verify(passwordEncoder).encode(expectedRawPasswordWithSalt);
    }
}