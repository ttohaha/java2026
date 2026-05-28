package blinov_first.util;

import java.util.UUID;

/**
 * Generates cryptographically random tokens for email confirmation links.
 */
public final class TokenGenerator {

    private TokenGenerator() {}

    /**
     * Returns a 32-character lowercase hex string suitable for use as a
     * one-time confirmation token.
     */
    public static String generate() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * Alias for {@link #generate()} — kept for backward compatibility with
     * code that was written before the Spring migration.
     *
     * @deprecated Use {@link #generate()} instead.
     */
    @Deprecated
    public static String generateSecureToken() {
        return generate();
    }
}
