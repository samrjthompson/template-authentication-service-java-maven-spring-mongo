package org.example.security;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

public class Encryptor implements TextEncryptor {

    private final String salt;
    private final String password;

    public Encryptor(String salt, String password) {
        this.salt = salt;
        this.password = password;
    }

    @Override
    public String encrypt(String text) {
        return Encryptors.text(password, salt).encrypt(text);
    }

    @Override
    public String decrypt(String encryptedText) {
        return Encryptors.text(password, salt).decrypt(encryptedText);
    }
}
