package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import org.example.model.request.CredentialsRequest;
import org.example.service.CredentialsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ControllerTest {

    @InjectMocks
    private Controller controller;

    @Mock
    private CredentialsService credentialsService;

    @Mock
    private CredentialsRequest requestBody;

    @Test
    void shouldSuccessfullyInsertCredentials() {
        // given
        ResponseEntity<Void> expected = ResponseEntity.ok().build();

        // when
        ResponseEntity<Void> actual = controller.insertCredentials(requestBody);

        // then
        assertEquals(expected, actual);
        verify(credentialsService).insertCredentials(requestBody);
    }

    @Test
    void shouldSuccessfullyUpdateExistingCredentials() {
        // given
        ResponseEntity<Void> expected = ResponseEntity.ok().build();

        // when
        ResponseEntity<Void> actual = controller.updateCredentials(requestBody);

        // then
        assertEquals(expected, actual);
        verify(credentialsService).updateCredentials(requestBody);
    }
}