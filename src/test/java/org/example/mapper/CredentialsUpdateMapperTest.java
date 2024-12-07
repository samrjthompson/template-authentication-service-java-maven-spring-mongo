package org.example.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.example.model.Updated;
import org.example.model.request.CredentialsRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class CredentialsUpdateMapperTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SALT = "salt";
    private static final String AUTHORITY = "read";
    private static final Instant NOW = Instant.now();

    @InjectMocks
    private CredentialsUpdateMapper credentialsUpdateMapper;

    @Mock
    private PasswordGenerator passwordGenerator;
    @Mock
    private Supplier<Instant> instantSupplier;

    @BeforeAll
    public static void setUp() {
        MDC.put("request_id", "request_id");
    }

    @ParameterizedTest
    @MethodSource("updatePasswordArguments")
    void shouldReturnUpdateObjectWhenUpdatingPassword(UpdateArguments arguments) {
        // given
        when(passwordGenerator.generateHashedPasswordWithSalt(anyString())).thenReturn(Map.of(
                "hashed_password", PASSWORD,
                "salt", SALT
        ));
        when(instantSupplier.get()).thenReturn(NOW);

        // when
        Update actual = credentialsUpdateMapper.mapUpdate(arguments.credentialsRequest());

        // then
        assertEquals(arguments.expectedUpdate(), actual);
    }

    @ParameterizedTest
    @MethodSource("nonPasswordUpdateArguments")
    void shouldReturnUpdateObjectWhenNotUpdatingPassword(UpdateArguments arguments) {
        // given
        when(instantSupplier.get()).thenReturn(NOW);

        // when
        Update actual = credentialsUpdateMapper.mapUpdate(arguments.credentialsRequest());

        // then
        assertEquals(arguments.expectedUpdate(), actual);
    }

    private static Stream<Arguments> updatePasswordArguments() {
        return Stream.of(
                Arguments.of(Named.of("Update password, authority, and isEnabled",
                                UpdateArguments.builder()
                                        .expectedUpdate(new Update()
                                                .set("password", PASSWORD)
                                                .set("salt", SALT)
                                                .set("authority", AUTHORITY)
                                                .set("isEnabled", true)
                                                .set("updated", new Updated()
                                                        .at(NOW)
                                                        .by("request_id")))
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .password(PASSWORD)
                                                .authority(AUTHORITY)
                                                .isEnabled(true)
                                                .build())
                                        .build()
                        )
                ),
                Arguments.of(Named.of("Update password and authority",
                                UpdateArguments.builder()
                                        .expectedUpdate(new Update()
                                                .set("password", PASSWORD)
                                                .set("salt", SALT)
                                                .set("authority", AUTHORITY)
                                                .set("isEnabled", true)
                                                .set("updated", new Updated()
                                                        .at(NOW)
                                                        .by("request_id")))
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .password(PASSWORD)
                                                .authority(AUTHORITY)
                                                .build())
                                        .build()
                        )
                ),
                Arguments.of(Named.of("Update password",
                                UpdateArguments.builder()
                                        .expectedUpdate(new Update()
                                                .set("password", PASSWORD)
                                                .set("salt", SALT)
                                                .set("isEnabled", true)
                                                .set("updated", new Updated()
                                                        .at(NOW)
                                                        .by("request_id")))
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .password(PASSWORD)
                                                .build())
                                        .build()
                        )
                ),
                Arguments.of(Named.of("Update password and isEnabled",
                                UpdateArguments.builder()
                                        .expectedUpdate(new Update()
                                                .set("password", PASSWORD)
                                                .set("salt", SALT)
                                                .set("isEnabled", false)
                                                .set("updated", new Updated()
                                                        .at(NOW)
                                                        .by("request_id")))
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .isEnabled(false)
                                                .password(PASSWORD)
                                                .build())
                                        .build()
                        )
                )
        );
    }

    private static Stream<Arguments> nonPasswordUpdateArguments() {
        return Stream.of(
                Arguments.of(Named.of("Update authority and isEnabled",
                                UpdateArguments.builder()
                                        .expectedUpdate(new Update()
                                                .set("authority", AUTHORITY)
                                                .set("isEnabled", true)
                                                .set("updated", new Updated()
                                                        .at(NOW)
                                                        .by("request_id")))
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .authority(AUTHORITY)
                                                .isEnabled(true)
                                                .build())
                                        .build()
                        )
                ),
                Arguments.of(Named.of("Update authority",
                                UpdateArguments.builder()
                                        .expectedUpdate(new Update()
                                                .set("authority", AUTHORITY)
                                                .set("isEnabled", true)
                                                .set("updated", new Updated()
                                                        .at(NOW)
                                                        .by("request_id")))
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .authority(AUTHORITY)
                                                .build())
                                        .build()
                        )
                ),
                Arguments.of(Named.of("Update isEnabled",
                                UpdateArguments.builder()
                                        .expectedUpdate(new Update()
                                                .set("isEnabled", false)
                                                .set("updated", new Updated()
                                                        .at(NOW)
                                                        .by("request_id")))
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .isEnabled(false)
                                                .build())
                                        .build()
                        )
                )
        );
    }

    private record UpdateArguments(Update expectedUpdate, CredentialsRequest credentialsRequest) {

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private Update expectedUpdate;
            private CredentialsRequest credentialsRequest;

            public Builder expectedUpdate(Update update) {
                this.expectedUpdate = update;
                return this;
            }

            public Builder credentialsRequest(CredentialsRequest credentialsRequest) {
                this.credentialsRequest = credentialsRequest;
                return this;
            }

            public UpdateArguments build() {
                return new UpdateArguments(expectedUpdate, credentialsRequest);
            }
        }
    }
}