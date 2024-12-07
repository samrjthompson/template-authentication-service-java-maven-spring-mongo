package org.example.model;

import static org.example.Main.NAMESPACE;

import java.util.HashMap;
import java.util.Map;
import org.example.exception.InvalidAuthorityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum UserAuthorities {

    READ("read"),
    WRITE("write"),
    ADMIN("admin");

    private static final Logger LOGGER = LoggerFactory.getLogger(NAMESPACE);
    private static final Map<String, UserAuthorities> BY_AUTHORITY_TYPE = new HashMap<>();

    static {
        for (UserAuthorities value : values()) {
            BY_AUTHORITY_TYPE.put(value.userAuthority, value);
        }
    }

    private final String userAuthority;

    UserAuthorities(String userAuthority) {
        this.userAuthority = userAuthority;
    }

    public static UserAuthorities fromString(String authority) {
        return BY_AUTHORITY_TYPE.computeIfAbsent(authority, k -> {
            LOGGER.error("Invalid authority type: [{}]", authority);
            throw new InvalidAuthorityException("Invalid authority type");
        });
    }

    public static String fromValue(UserAuthorities userAuthorities) {
        return userAuthorities.userAuthority;
    }

    public static void validate(String authority) {
        BY_AUTHORITY_TYPE.computeIfAbsent(authority, k -> {
            LOGGER.error("Invalid authority type: [{}]", authority);
            throw new InvalidAuthorityException("Invalid authority type");
        });
    }
}
