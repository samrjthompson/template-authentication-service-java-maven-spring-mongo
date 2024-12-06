package org.example.repository;

import static org.example.Main.NAMESPACE;

import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private final MongoTemplate mongoTemplate;

    public Repository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void save(User user) {
        try {
            mongoTemplate.save(user);
            LOGGER.info("Credentials successfully saved to MongoDB");
        } catch (TransientDataAccessException ex) {
            LOGGER.error("Failed to save credentials to DB. Recoverable error when accessing MongoDB.", ex);
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to save credentials to DB. Non-recoverable error when accessing MongoDB.", ex);
        }
    }

    public User findByUsername(String username) {
        Query query = new Query()
                .addCriteria(Criteria.where("username").is(username));

        return mongoTemplate.findOne(query, User.class);
    }
}
