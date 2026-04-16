package blinov_first.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class PasswordHasher {

    private static final String ALGORITHM = "SHA-256";

    private PasswordHasher() {
        // Private constructor to prevent instantiation
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: Hashing algorithm " + ALGORITHM + " not found", e);
        }
    }
}