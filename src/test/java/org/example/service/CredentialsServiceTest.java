package org.example.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.example.exception.ConflictException;
import org.example.exception.InvalidAuthorityException;
import org.example.mapper.CredentialsRequestMapper;
import org.example.model.User;
import org.example.model.request.CredentialsRequest;
import org.example.repository.Repository;
import org.example.util.EncoderUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CredentialsServiceTest {

    private static final String AUTHORITY = "read";
    private static final String INVALID_AUTHORITY = "invalid_authority";
    private static final String USERNAME = "bill@example.com";
    private static final String ENCODED_USERNAME = EncoderUtils.encodeUsernameIntoMongoId(USERNAME);

    @InjectMocks
    private CredentialsService service;

    @Mock
    private CredentialsRequestMapper mapper;
    @Mock
    private Repository repository;

    @Mock
    private CredentialsRequest requestBody;
    @Mock
    private User user;

    @Test
    void shouldInsertNewCredentials() {
        // given
        when(requestBody.authority()).thenReturn(AUTHORITY);
        when(requestBody.username()).thenReturn(USERNAME);
        when(repository.findById(anyString())).thenReturn(null);
        when(mapper.mapNewUser(any())).thenReturn(user);

        // when
        service.insertCredentials(requestBody);

        // then
        verify(repository).insert(user);
    }

    @Test
    void shouldThrowConflictExceptionWhenUsernameAlreadyExists() {
        // given
        when(requestBody.authority()).thenReturn(AUTHORITY);
        when(requestBody.username()).thenReturn(USERNAME);
        when(repository.findById(anyString())).thenReturn(user);

        // when
        Executable executable = () -> service.insertCredentials(requestBody);

        // then
        assertThrows(ConflictException.class, executable);
        verify(repository).findById(ENCODED_USERNAME);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void shouldFailAuthorityValidationWhenAuthorityIsInvalid() {
        // given
        when(requestBody.authority()).thenReturn(INVALID_AUTHORITY);

        // when
        Executable executable = () -> service.insertCredentials(requestBody);

        // then
        assertThrows(InvalidAuthorityException.class, executable);
        verifyNoInteractions(repository);
    }
}