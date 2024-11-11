package swd392.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.security.SecureRandom;

public final class RandomUtils {

    private static final int DEFAULT_LENGTH = 20;

    private static final SecureRandom SECURE_RANDOM;

    static {
        SECURE_RANDOM = new SecureRandom();
        SECURE_RANDOM.nextBytes(new byte[64]);
    }
    private RandomUtils() {}

    public static String generateRandomAlphanumericString() {
        return generateRandomAlphanumericString(DEFAULT_LENGTH);
    }

    public static String generateRandomAlphanumericString(int length) {
        return RandomStringUtils.random(length, 0, 0, true, true,
                null, SECURE_RANDOM);
    }
}
