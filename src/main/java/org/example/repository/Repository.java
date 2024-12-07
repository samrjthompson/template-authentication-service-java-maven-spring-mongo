package org.example.repository;

import static org.example.Main.NAMESPACE;

import org.example.exception.BadGatewayException;
import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class Repository {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);

    private final MongoTemplate mongoTemplate;

    public Repository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void insert(User user) {
        try {
            mongoTemplate.insert(user);
            LOGGER.info("Credentials successfully inserted into MongoDB");
        } catch (TransientDataAccessException ex) {
            LOGGER.error("Failed to insert credentials to DB. Recoverable error when accessing MongoDB.", ex);
            throw new BadGatewayException(
                    "Failed to insert credentials to DB. Recoverable error when accessing MongoDB.");
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to insert credentials to DB. Non-recoverable error when accessing MongoDB.", ex);
            throw new BadGatewayException(
                    "Failed to insert credentials to DB. Non-recoverable error when accessing MongoDB.");
        }
    }

    public void update(final String id, Update update) {
        try {
            Query query = new Query(Criteria.where("_id").is(id));
            mongoTemplate.updateFirst(query, update, User.class);
        } catch (TransientDataAccessException ex) {
            LOGGER.error("Failed to update credentials to DB. Recoverable error when accessing MongoDB.", ex);
            throw new BadGatewayException(
                    "Failed to update credentials to DB. Recoverable error when accessing MongoDB.");
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to update credentials to DB. Non-recoverable error when accessing MongoDB.", ex);
            throw new BadGatewayException(
                    "Failed to update credentials to DB. Non-recoverable error when accessing MongoDB.");
        }
    }

    public User findById(final String id) {
        try {
            return mongoTemplate.findById(id, User.class);
        } catch (TransientDataAccessException ex) {
            LOGGER.error("Failed to find user in DB. Recoverable error when accessing MongoDB.", ex);
            throw new BadGatewayException("Failed to find user in DB. Recoverable error when accessing MongoDB.");
        } catch (DataAccessException ex) {
            LOGGER.error("Failed to find user in DB. Non-recoverable error when accessing MongoDB.", ex);
            throw new BadGatewayException("Failed to find user in DB. Non-recoverable error when accessing MongoDB.");
        }
    }
}
