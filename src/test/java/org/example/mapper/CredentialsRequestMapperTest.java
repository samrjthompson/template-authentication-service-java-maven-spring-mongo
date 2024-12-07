package org.example.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.function.Supplier;
import org.example.model.Created;
import org.example.model.Updated;
import org.example.model.User;
import org.example.model.request.CredentialsRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class CredentialsRequestMapperTest {

    private static final String USERNAME = "bill@example.com";
    private static final String ENCODED_USERNAME = encodeUsername();
    private static final String AUTHORITY = "read";
    private static final String RAW_PASSWORD = "raw_password";
    private static final String HASHED_PASSWORD = "hashed_password";
    private static final String SALT = "salt";
    private static final Map<String, String> CREDENTIALS_MAP = Map.of(
            "hashed_password", HASHED_PASSWORD,
            "salt", SALT);
    private static final String REQUEST_ID_KEY = "request_id";
    private static final String REQUEST_ID_VALUE = "mapper_test";

    @InjectMocks
    private CredentialsRequestMapper mapper;

    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private Supplier<Instant> instantSupplier;

    @Mock
    private CredentialsRequest credentialsRequest;

    @BeforeAll
    static void setUp() {
        MDC.put(REQUEST_ID_KEY, REQUEST_ID_VALUE);
    }

    @Test
    void shouldMapNewUser() {
        // given
        when(credentialsRequest.username()).thenReturn(USERNAME);
        when(credentialsRequest.authority()).thenReturn(AUTHORITY);
        when(credentialsRequest.password()).thenReturn(RAW_PASSWORD);
        when(passwordGenerator.generateHashedPasswordWithSalt(anyString())).thenReturn(CREDENTIALS_MAP);
        Instant now = Instant.now();
        when(instantSupplier.get()).thenReturn(now);

        User expected = new User()
                .id(ENCODED_USERNAME)
                .username(USERNAME)
                .authority(AUTHORITY)
                .password(HASHED_PASSWORD)
                .salt(SALT)
                .enabled(true)
                .created(new Created()
                        .at(now)
                        .by(REQUEST_ID_VALUE))
                .updated(new Updated()
                        .at(now)
                        .by(REQUEST_ID_VALUE));

        // when
        User actual = mapper.mapNewUser(credentialsRequest);

        // then
        assertEquals(expected, actual);
        verify(passwordGenerator).generateHashedPasswordWithSalt(RAW_PASSWORD);
        verify(instantSupplier).get();
    }

    private static String encodeUsername() {
        return new String(Base64.getUrlEncoder()
                .withoutPadding()
                .encode(USERNAME.getBytes()));
    }
}