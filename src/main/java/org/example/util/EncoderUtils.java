package org.example.util;

import java.util.Base64;

public class EncoderUtils {

    private EncoderUtils() {}

    public static String urlSafeBase64Encode(String input) {
        return new String(Base64.getUrlEncoder()
                .withoutPadding()
                .encode(input.getBytes()));
    }
}
