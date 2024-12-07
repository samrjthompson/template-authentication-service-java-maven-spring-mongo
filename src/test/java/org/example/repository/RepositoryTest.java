package org.example.repository;

import static org.mockito.Mockito.verify;

import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class RepositoryTest {

    private static final String ID = "id";

    @InjectMocks
    private Repository repository;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private User user;

    @Test
    void shouldInsertNewRecordIntoMongo() {
        // given

        // when
        repository.insert(user);

        // then
        verify(mongoTemplate).insert(user);
    }

    @Test
    void shouldFindRecordInMongoById() {
        // given

        // when
        repository.findById(ID);

        // then
        verify(mongoTemplate).findById(ID, User.class);
    }
}