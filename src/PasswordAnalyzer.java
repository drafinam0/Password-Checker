package src;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * PasswordAnalyzer implements the core password strength evaluation logic.
 * It strictly adheres to beginner-friendly Java conventions using:
 * - loops and charAt()
 * - Character.isUpperCase()
 * - Character.isLowerCase()
 * - Character.isDigit()
 * - Explicit boolean flags for the 5 rules
 * - if-else scoring and classification
 */
public class PasswordAnalyzer {

    // Set of common known weak passwords to alert users
    private static final Set<String> COMMON_PASSWORDS = new HashSet<>(Arrays.asList(
        "password", "123456", "12345678", "123456789", "qwerty", "password123",
        "admin", "admin123", "welcome", "iloveyou", "abc12345", "monkey", "dragon"
    ));

    // String containing allowed special characters
    private static final String SPECIAL_CHARACTERS = "!@#$%^&*()_+-=[]{}|;':\",./<>?~`";

    /**
     * Analyzes the given password against the five security criteria.
     *
     * @param password the password string to evaluate
     * @return a PasswordResult containing score, rating, checklist status, and suggestions
     */
    public static PasswordResult analyze(String password) {
        PasswordResult result = new PasswordResult();

        // 1. Check for empty or null password
        if (password == null || password.trim().isEmpty()) {
            result.setEmpty(true);
            result.setScore(0);
            result.setRating("WEAK");
            result.setFeedbackMessage("Password cannot be empty. Please enter a password to evaluate.");
            result.addSuggestion("Enter a password with at least 8 characters.");
            return result;
        }

        // 2. Initialize boolean variables to track the five rules
        boolean hasMinLength = false;
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // Rule 1: Password contains at least 8 characters
        if (password.length() >= 8) {
            hasMinLength = true;
        }

        // Examine each character in the password using a loop and charAt()
        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);

            // Rule 2: Contains at least one uppercase letter (A-Z)
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            }
            // Rule 3: Contains at least one lowercase letter (a-z)
            else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            }
            // Rule 4: Contains at least one digit (0-9)
            else if (Character.isDigit(ch)) {
                hasDigit = true;
            }
            // Rule 5: Contains at least one special character
            else if (isSpecialChar(ch)) {
                hasSpecial = true;
            }
        }

        // Record boolean flags in the result object
        result.setHasMinLength(hasMinLength);
        result.setHasUppercase(hasUppercase);
        result.setHasLowercase(hasLowercase);
        result.setHasDigit(hasDigit);
        result.setHasSpecial(hasSpecial);

        // 3. Score calculation: Award 1 point for each satisfied rule
        int score = 0;
        if (hasMinLength) {
            score++;
        }
        if (hasUppercase) {
            score++;
        }
        if (hasLowercase) {
            score++;
        }
        if (hasDigit) {
            score++;
        }
        if (hasSpecial) {
            score++;
        }

        result.setScore(score);

        // 4. Classify password based on score
        // Score 0-2: WEAK | Score 3-4: MEDIUM | Score 5: STRONG
        if (score <= 2) {
            result.setRating("WEAK");
        } else if (score <= 4) {
            result.setRating("MEDIUM");
        } else {
            result.setRating("STRONG");
        }

        // 5. Check if password is among common weak passwords
        boolean isCommon = COMMON_PASSWORDS.contains(password.toLowerCase().trim());
        result.setCommonPassword(isCommon);

        // 6. Generate missing requirements and actionable suggestions
        if (!hasMinLength) {
            result.addSuggestion("Add at least 8 characters (currently " + password.length() + ").");
        }
        if (!hasUppercase) {
            result.addSuggestion("Include at least one uppercase letter (A–Z).");
        }
        if (!hasLowercase) {
            result.addSuggestion("Include at least one lowercase letter (a–z).");
        }
        if (!hasDigit) {
            result.addSuggestion("Add at least one number (0–9).");
        }
        if (!hasSpecial) {
            result.addSuggestion("Include at least one special character (e.g. @, #, $, !, %).");
        }

        // Contextual feedback messages
        if (isCommon) {
            result.setFeedbackMessage("Warning: This is a widely used, easily guessed password! Change it immediately.");
        } else if (score == 5) {
            result.setFeedbackMessage("Shield Activated: Excellent password! All 5 security requirements are met.");
        } else if (score >= 3) {
            result.setFeedbackMessage("Good progress: Follow the suggestions below to achieve a Strong rating.");
        } else {
            result.setFeedbackMessage("Security Alert: This password is weak and vulnerable to brute-force attacks.");
        }

        return result;
    }

    /**
     * Helper method to determine if a character is a recognized special character.
     * Uses String.indexOf() to check character presence.
     */
    private static boolean isSpecialChar(char ch) {
        return SPECIAL_CHARACTERS.indexOf(ch) >= 0;
    }
}
