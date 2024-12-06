package org.example.security;

import java.util.HashMap;
import java.util.Map;

public enum UserAuthorities {

    READ("read"),
    WRITE("write");

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
            throw new RuntimeException("Invalid authority type");
        });
    }

    public static String fromValue(UserAuthorities userAuthorities) {
        return userAuthorities.userAuthority;
    }
}
