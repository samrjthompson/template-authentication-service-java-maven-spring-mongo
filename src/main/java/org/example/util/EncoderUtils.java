package org.example.util;

import java.util.Base64;

public class EncoderUtils {

    private EncoderUtils() {}

    public static String encodeUsernameIntoMongoId(String username) {
        return new String(Base64.getUrlEncoder()
                .withoutPadding()
                .encode(username.getBytes()));
    }
}
