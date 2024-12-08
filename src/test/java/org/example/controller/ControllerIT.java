package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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

    private static final String HEALTHCHECK_ENDPOINT = "/healthcheck";
    private static final String REGISTER_ENDPOINT = "/register";

    @Value("${spring.data.mongodb.database}")
    private String collectionName;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:8.0.3");

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    void setUp() throws Exception{
        mongoTemplate.dropCollection(collectionName);
        mongoTemplate.createCollection(collectionName);

        // Set up admin User
        final String adminUser = IOUtils.resourceToString("/document/admin_mongo_doc.json", StandardCharsets.UTF_8);
        mongoTemplate.insert(objectMapper.readValue(adminUser, User.class));
    }

    @Test
    void healthcheck() throws Exception {
        // given

        // when
        ResultActions result = mockMvc.perform(get(HEALTHCHECK_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void shouldSuccessfullyRegisterNewUserCredentials() throws Exception {
        // given
        final String requestBody = IOUtils.resourceToString("/request/post_register_request_body.json",
                StandardCharsets.UTF_8);

        // when
        ResultActions result = mockMvc.perform(post(REGISTER_ENDPOINT)
                .header("x-request-id", "ITEST")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQ=")
                .content(requestBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk());
        User actual = mongoTemplate.findAll(User.class).getFirst();
        User expected = new User();
        // TODO: Finish tests
        assertEquals(expected, actual);
    }
}
