package src;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PasswordGenerator creates cryptographically secure random passwords
 * that are guaranteed to satisfy all 5 requirements:
 * 1. At least 8 characters (default 14-16 characters)
 * 2. At least one uppercase letter (A-Z)
 * 3. At least one lowercase letter (a-z)
 * 4. At least one digit (0-9)
 * 5. At least one special character (!@#$%^&*)
 */
public class PasswordGenerator {

    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Generates a strong password of the specified length (minimum 8, default 14).
     *
     * @param length desired password length
     * @return strong random password meeting all 5 security criteria
     */
    public static String generate(int length) {
        if (length < 8) {
            length = 8;
        }

        List<Character> charList = new ArrayList<>();

        // Ensure at least one character from each required category
        charList.add(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        charList.add(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        charList.add(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        charList.add(SPECIAL.charAt(RANDOM.nextInt(SPECIAL.length())));

        // Fill remaining slots from the combined character pool
        for (int i = 4; i < length; i++) {
            charList.add(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the characters so the guaranteed positions are randomized
        Collections.shuffle(charList, RANDOM);

        // Build final password string
        StringBuilder password = new StringBuilder();
        for (char c : charList) {
            password.append(c);
        }

        return password.toString();
    }
}
