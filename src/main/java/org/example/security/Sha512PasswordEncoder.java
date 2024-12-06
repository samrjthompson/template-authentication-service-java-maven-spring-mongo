package org.example.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class Sha512PasswordEncoder implements PasswordEncoder {

    private static final String SHA_512 = "SHA-512";

    @Override
    public String encode(CharSequence rawPassword) {
        return hashWithSHA512(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        final String hashedPassword = encode(rawPassword);
        return encodedPassword.equals(hashedPassword);
    }

    private String hashWithSHA512(String input) {
        StringBuilder result = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_512);
            byte[] digested = md.digest(input.getBytes());
            for (byte b : digested) {
                result.append(Integer.toHexString(0xFF & b)); // NOSONAR
            }
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Bad algorithm");
        }
        return result.toString();
    }
}
