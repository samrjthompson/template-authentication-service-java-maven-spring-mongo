package org.example.repository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.example.exception.BadGatewayException;
import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class RepositoryTest {

    private static final String ID = "id";

    @InjectMocks
    private Repository repository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private User user;
    @Mock
    private Update update;

    @Test
    void shouldInsertNewRecordIntoMongo() {
        // given

        // when
        repository.insert(user);

        // then
        verify(mongoTemplate).insert(user);
    }

    @ParameterizedTest
    @MethodSource("mongoExceptionArgs")
    void shouldThrowBadGatewayWhenMongoExceptionCaughtDuringInsert(RuntimeException exception) {
        // given
        when(mongoTemplate.insert(any(User.class))).thenThrow(exception);

        // when
        Executable executable = () -> repository.insert(user);

        // then
        assertThrows(BadGatewayException.class, executable);
    }

    @Test
    void shouldUpdateExistingRecordIntoMongo() {
        // given
        Query expectedQuery = new Query(Criteria.where("_id").is(ID));

        // when
        repository.update(ID, update);

        // then
        verify(mongoTemplate).updateFirst(expectedQuery, update, User.class);
    }

    @ParameterizedTest
    @MethodSource("mongoExceptionArgs")
    void shouldThrowBadGatewayWhenMongoExceptionCaughtDuringUpdate(RuntimeException exception) {
        // given
        when(mongoTemplate.updateFirst(any(), any(Update.class), any(Class.class))).thenThrow(exception);

        // when
        Executable executable = () -> repository.update(ID, update);

        // then
        assertThrows(BadGatewayException.class, executable);
    }

    @Test
    void shouldFindRecordInMongoById() {
        // given

        // when
        repository.findById(ID);

        // then
        verify(mongoTemplate).findById(ID, User.class);
    }

    @ParameterizedTest
    @MethodSource("mongoExceptionArgs")
    void shouldThrowBadGatewayWhenMongoExceptionCaughtDuringFind(RuntimeException exception) {
        // given
        when(mongoTemplate.findById(anyString(), any())).thenThrow(exception);

        // when
        Executable executable = () -> repository.findById(ID);

        // then
        assertThrows(BadGatewayException.class, executable);
    }

    private static Stream<Arguments> mongoExceptionArgs() {
        return Stream.of(
                Arguments.of(new TransientDataAccessException("...") {}),
                Arguments.of(new DataAccessException("...") {})
        );
    }
}