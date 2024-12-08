package org.example.security;

import static org.example.Main.NAMESPACE;

import java.util.Optional;
import org.example.model.User;
import org.example.util.EncoderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class InMongoUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private static final String USERNAME_NOT_FOUND = "Could not find username: [%s]";

    private final MongoTemplate mongoTemplate;

    public InMongoUserDetailsService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public User loadUserByUsername(final String username) throws UsernameNotFoundException {
        final String id = EncoderUtils.urlSafeBase64Encode(username);
        return Optional.ofNullable(mongoTemplate.findById(id, User.class))
                .orElseGet(() -> {
                    final String msg = USERNAME_NOT_FOUND.formatted(username);
                    LOGGER.error(msg);
                    throw new UsernameNotFoundException(msg);
                });
    }
}
