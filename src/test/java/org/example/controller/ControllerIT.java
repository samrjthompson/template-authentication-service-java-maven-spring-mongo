package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.example.model.Created;
import org.example.model.Updated;
import org.example.model.User;
import org.example.model.request.CredentialsRequest;
import org.example.security.SaltGenerator;
import org.example.util.EncoderUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
@WireMockTest(httpPort = 8888)
class ControllerIT {

    static {
        try {
            ADMIN_USER = IOUtils.resourceToString("/document/admin_mongo_doc.json", StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String ADMIN_USER;
    private static final String COLLECTION_NAME = "account";
    private static final String REQUEST_ID = "ITEST";
    private static final DateTimeFormatter INSTANT_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
    private static final Instant NOW = Instant.parse(Instant.now().atOffset(ZoneOffset.UTC).format(INSTANT_FORMATTER));
    private static final String REGISTER_ENDPOINT = "/register";
    private static final String USERNAME = "bob@example.com";
    private static final String ENCODED_USERNAME = EncoderUtils.urlSafeBase64Encode(USERNAME);
    private static final String RAW_PASSWORD = "password";
    private static final String UPDATED_RAW_PASSWORD = "new_password";
    private static final String SALT = "salt";
    private static final String VALID_ADMIN_AUTH = encodeBasicAuth("admin@example.com", RAW_PASSWORD);
    private static final String NO_PERMISSIONS_ADMIN_AUTH = encodeBasicAuth("non_admin@example.com", RAW_PASSWORD);
    private static final String UNKNOWN_USERNAME_ADMIN_AUTH = encodeBasicAuth("fake@example.com", RAW_PASSWORD);
    private static final String UNKNOWN_PASSWORD_ADMIN_AUTH = encodeBasicAuth("non_admin@example.com", "fake_password");
    private static final String UNKNOWN_USERNAME_AND_PASSWORD_ADMIN_AUTH = encodeBasicAuth("fake@example.com",
            "fake_password");

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0.3");

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String hashedPassword;

    @MockitoBean
    private Supplier<Instant> instantSupplier;
    @MockitoBean
    private SaltGenerator saltGenerator;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws Exception {
        // Mockito Stubbing
        when(instantSupplier.get()).thenReturn(NOW);
        when(saltGenerator.generateKey()).thenReturn(SALT);

        // Clean up
        mongoTemplate.dropCollection(COLLECTION_NAME);
        mongoTemplate.createCollection(COLLECTION_NAME);

        // Set up admin User
        mongoTemplate.insert(objectMapper.readValue(ADMIN_USER, User.class));

        // Prepare stubbing for hashed password
        hashedPassword = passwordEncoder.encode(RAW_PASSWORD + SALT);
    }

    @Test
    void shouldSuccessfullyRegisterNewUserCredentials() throws Exception {
        // given
        final String requestBody = IOUtils.resourceToString("/request/post_register_request_body.json",
                StandardCharsets.UTF_8);
        final String expectedDocument = IOUtils.resourceToString("/document/expected_user_doc.json",
                        StandardCharsets.UTF_8)
                .replaceAll("<instant_now>", NOW.toString())
                .replaceAll("<password>", hashedPassword);

        // when
        ResultActions result = mockMvc.perform(
                post(REGISTER_ENDPOINT)
                        .header("x-request-id", REQUEST_ID)
                        .header("Authorization", VALID_ADMIN_AUTH)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        User actual = mongoTemplate.findById(ENCODED_USERNAME, User.class);
        User expected = objectMapper.readValue(expectedDocument, User.class);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("unauthorisedSources")
    void shouldFailRegisteringNewUserCredentialsWhenAdminCredentialsNotRecognised(final String invalidAuth)
            throws Exception {
        // given
        final String requestBody = IOUtils.resourceToString("/request/post_register_request_body.json",
                StandardCharsets.UTF_8);
        // when
        ResultActions result = mockMvc.perform(
                post(REGISTER_ENDPOINT)
                        .header("x-request-id", REQUEST_ID)
                        .header("Authorization", invalidAuth)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(MockMvcResultMatchers.status().isUnauthorized());
        assertNull(mongoTemplate.findById(ENCODED_USERNAME, User.class));
    }

    @Test
    void shouldFailRegisteringNewUserCredentialsWhenUserDoesNotHaveAdminPermissions() throws Exception {
        // given
        final String nonAdminUser = IOUtils.resourceToString("/document/non_admin_mongo_doc.json",
                StandardCharsets.UTF_8);
        mongoTemplate.insert(objectMapper.readValue(nonAdminUser, User.class));

        final String requestBody = IOUtils.resourceToString("/request/post_register_request_body.json",
                StandardCharsets.UTF_8);
        // when
        ResultActions result = mockMvc
                .perform(post(REGISTER_ENDPOINT)
                        .header("x-request-id", REQUEST_ID)
                        .header("Authorization", NO_PERMISSIONS_ADMIN_AUTH)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(MockMvcResultMatchers.status().isForbidden());
        assertNull(mongoTemplate.findById(ENCODED_USERNAME, User.class));
    }

    @ParameterizedTest
    @MethodSource("validPatchRequests")
    void shouldSuccessfullyUpdateExistingUserCredentials(PatchRequestArgs args) throws Exception {
        // given
        mongoTemplate.insert(new User()
                .id(EncoderUtils.urlSafeBase64Encode(USERNAME))
                .username(USERNAME)
                .enabled(true)
                .authority("read")
                .salt(SALT)
                .password(hashedPassword)
                .version(1L)
                .created(new Created()
                        .by(REQUEST_ID)
                        .at(NOW))
                .updated(new Updated()
                        .by(REQUEST_ID)
                        .at(NOW)));

        // This changes the expected document's password from its raw form to its hashed form
        final String rawExistingPassword = args.expectedDocument.getPassword();
        final String expectedHashedPassword = passwordEncoder.encode(rawExistingPassword + SALT);
        args.expectedDocument.password(expectedHashedPassword);

        final String requestBody = objectMapper.writeValueAsString(args.requestBody);

        // when
        ResultActions result = mockMvc
                .perform(patch(REGISTER_ENDPOINT)
                        .header("x-request-id", REQUEST_ID)
                        .header("Authorization", VALID_ADMIN_AUTH)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        User actual = mongoTemplate.findById(ENCODED_USERNAME, User.class);
        assertEquals(args.expectedDocument, actual);
    }

    private static String encodeBasicAuth(final String username, final String password) {
        final String combinedDetails = "%s:%s".formatted(username, password);
        return "Basic %s".formatted(EncoderUtils.urlSafeBase64Encode(combinedDetails));
    }

    private static Stream<Arguments> unauthorisedSources() {
        return Stream.of(Arguments.of(Named.of("Unknown username", UNKNOWN_USERNAME_ADMIN_AUTH)),
                Arguments.of(Named.of("Unknown password", UNKNOWN_PASSWORD_ADMIN_AUTH)),
                Arguments.of(Named.of("Unknown username and password", UNKNOWN_USERNAME_AND_PASSWORD_ADMIN_AUTH)));
    }

    private static Stream<Arguments> validPatchRequests() {
        return Stream.of(
                Arguments.of(
                        Named.of("Change all details",
                                PatchRequestArgs.builder()
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .password(UPDATED_RAW_PASSWORD)
                                                .authority("write")
                                                .build())
                                        .expectedDocument(new User()
                                                .id(EncoderUtils.urlSafeBase64Encode(USERNAME))
                                                .username(USERNAME)
                                                .enabled(true)
                                                .authority("write")
                                                .salt(SALT)
                                                .password(UPDATED_RAW_PASSWORD) // Hashed and changed during the test
                                                .version(2L)
                                                .created(new Created()
                                                        .by(REQUEST_ID)
                                                        .at(NOW))
                                                .updated(new Updated()
                                                        .by(REQUEST_ID)
                                                        .at(NOW)))
                                        .build())),
                Arguments.of(
                        Named.of("Change password only",
                                PatchRequestArgs.builder()
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .password(UPDATED_RAW_PASSWORD)
                                                .build())
                                        .expectedDocument(new User()
                                                .id(EncoderUtils.urlSafeBase64Encode(USERNAME))
                                                .username(USERNAME)
                                                .enabled(true)
                                                .authority("read")
                                                .salt(SALT)
                                                .password(UPDATED_RAW_PASSWORD) // Hashed and changed during the test
                                                .version(2L)
                                                .created(new Created()
                                                        .by(REQUEST_ID)
                                                        .at(NOW))
                                                .updated(new Updated()
                                                        .by(REQUEST_ID)
                                                        .at(NOW)))
                                        .build())),
                Arguments.of(
                        Named.of("Change authority only",
                                PatchRequestArgs.builder()
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .authority("write")
                                                .build())
                                        .expectedDocument(new User()
                                                .id(EncoderUtils.urlSafeBase64Encode(USERNAME))
                                                .username(USERNAME)
                                                .enabled(true)
                                                .authority("write")
                                                .salt(SALT)
                                                .password(RAW_PASSWORD) // Hashed and changed during the test
                                                .version(2L)
                                                .created(new Created()
                                                        .by(REQUEST_ID)
                                                        .at(NOW))
                                                .updated(new Updated()
                                                        .by(REQUEST_ID)
                                                        .at(NOW)))
                                        .build())),
                Arguments.of(
                        Named.of("Change isEnabled only",
                                PatchRequestArgs.builder()
                                        .credentialsRequest(CredentialsRequest.builder()
                                                .username(USERNAME)
                                                .isEnabled(false)
                                                .build())
                                        .expectedDocument(new User()
                                                .id(EncoderUtils.urlSafeBase64Encode(USERNAME))
                                                .username(USERNAME)
                                                .enabled(false)
                                                .authority("read")
                                                .salt(SALT)
                                                .password(RAW_PASSWORD) // Hashed and changed during the test
                                                .version(2L)
                                                .created(new Created()
                                                        .by(REQUEST_ID)
                                                        .at(NOW))
                                                .updated(new Updated()
                                                        .by(REQUEST_ID)
                                                        .at(NOW)))
                                        .build())));
    }

    private record PatchRequestArgs(CredentialsRequest requestBody, User expectedDocument) {

        public static Builder builder() {
            return new Builder();
        }

        private static class Builder {

            private CredentialsRequest requestBody;
            private User expectedDocument;

            private Builder() {
            }

            private Builder credentialsRequest(CredentialsRequest credentialsRequest) {
                this.requestBody = credentialsRequest;
                return this;
            }

            private Builder expectedDocument(User expectedDocument) {
                this.expectedDocument = expectedDocument;
                return this;
            }

            public PatchRequestArgs build() {
                return new PatchRequestArgs(requestBody, expectedDocument);
            }
        }
    }
}
