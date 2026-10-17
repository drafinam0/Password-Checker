package test;

import src.PasswordAnalyzer;
import src.PasswordGenerator;
import src.PasswordResult;

/**
 * Automated test suite to verify all requirements of Password Strength Checker.
 * Validates edge cases, scoring logic, rule detection, and generator output.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   PASSWORD STRENGTH CHECKER - UNIT TEST SUITE    ");
        System.out.println("==================================================");

        int passed = 0;
        int total = 0;

        // Test 1: Empty password
        total++;
        PasswordResult t1 = PasswordAnalyzer.analyze("");
        if (t1.getScore() == 0 && "WEAK".equals(t1.getRating()) && t1.isEmpty()) {
            printPass("Test 1: Empty password handled correctly (Score 0, WEAK, isEmpty=true)");
            passed++;
        } else {
            printFail("Test 1: Empty password failed");
        }

        // Test 2: "hello" (only lowercase, length 5)
        total++;
        PasswordResult t2 = PasswordAnalyzer.analyze("hello");
        if (t2.getScore() == 1 && "WEAK".equals(t2.getRating()) 
                && !t2.isHasMinLength() && !t2.isHasUppercase() && t2.isHasLowercase() 
                && !t2.isHasDigit() && !t2.isHasSpecial()) {
            printPass("Test 2: \"hello\" (Score 1/5, WEAK, only lowercase passed)");
            passed++;
        } else {
            printFail("Test 2: \"hello\" evaluation failed (Score: " + t2.getScore() + ")");
        }

        // Test 3: "Hello123" (Length 8, Upper, Lower, Digit -> Score 4, MEDIUM)
        total++;
        PasswordResult t3 = PasswordAnalyzer.analyze("Hello123");
        if (t3.getScore() == 4 && "MEDIUM".equals(t3.getRating()) 
                && t3.isHasMinLength() && t3.isHasUppercase() && t3.isHasLowercase() 
                && t3.isHasDigit() && !t3.isHasSpecial()) {
            printPass("Test 3: \"Hello123\" (Score 4/5, MEDIUM, length/upper/lower/digit)");
            passed++;
        } else {
            printFail("Test 3: \"Hello123\" evaluation failed (Score: " + t3.getScore() + ")");
        }

        // Test 4: All 5 characteristics ("P@ssw0rd2024!")
        total++;
        PasswordResult t4 = PasswordAnalyzer.analyze("P@ssw0rd2024!");
        if (t4.getScore() == 5 && "STRONG".equals(t4.getRating()) 
                && t4.isHasMinLength() && t4.isHasUppercase() && t4.isHasLowercase() 
                && t4.isHasDigit() && t4.isHasSpecial()) {
            printPass("Test 4: \"P@ssw0rd2024!\" (Score 5/5, STRONG, all 5 rules satisfied)");
            passed++;
        } else {
            printFail("Test 4: \"P@ssw0rd2024!\" evaluation failed (Score: " + t4.getScore() + ")");
        }

        // Test 5: Uppercase and digits only ("ADMIN12345", length 10)
        total++;
        PasswordResult t5 = PasswordAnalyzer.analyze("ADMIN12345");
        if (t5.getScore() == 3 && "MEDIUM".equals(t5.getRating()) 
                && t5.isHasMinLength() && t5.isHasUppercase() && !t5.isHasLowercase() 
                && t5.isHasDigit() && !t5.isHasSpecial()) {
            printPass("Test 5: \"ADMIN12345\" (Score 3/5, MEDIUM, length/upper/digit)");
            passed++;
        } else {
            printFail("Test 5: \"ADMIN12345\" evaluation failed (Score: " + t5.getScore() + ")");
        }

        // Test 6: Shorter than 8 characters with 4 rules ("Ab1!")
        total++;
        PasswordResult t6 = PasswordAnalyzer.analyze("Ab1!");
        if (t6.getScore() == 4 && "MEDIUM".equals(t6.getRating()) 
                && !t6.isHasMinLength() && t6.isHasUppercase() && t6.isHasLowercase() 
                && t6.isHasDigit() && t6.isHasSpecial()) {
            printPass("Test 6: \"Ab1!\" (Score 4/5, MEDIUM, missed length rule < 8)");
            passed++;
        } else {
            printFail("Test 6: \"Ab1!\" evaluation failed (Score: " + t6.getScore() + ")");
        }

        // Test 7: Common weak password detection
        total++;
        PasswordResult t7 = PasswordAnalyzer.analyze("password123");
        if (t7.isCommonPassword()) {
            printPass("Test 7: Common password detection flagged \"password123\" successfully");
            passed++;
        } else {
            printFail("Test 7: Failed to flag common password \"password123\"");
        }

        // Test 8: Password Generator produces 5/5 strong passwords
        total++;
        boolean genAllStrong = true;
        for (int i = 0; i < 15; i++) {
            String gen = PasswordGenerator.generate(16);
            PasswordResult genRes = PasswordAnalyzer.analyze(gen);
            if (genRes.getScore() != 5 || !"STRONG".equals(genRes.getRating()) || gen.length() != 16) {
                genAllStrong = false;
                break;
            }
        }
        if (genAllStrong) {
            printPass("Test 8: PasswordGenerator verified (15/15 generated passwords scored 5/5 STRONG)");
            passed++;
        } else {
            printFail("Test 8: PasswordGenerator produced non-conforming password");
        }

        System.out.println("--------------------------------------------------");
        System.out.println("TEST RESULTS: " + passed + " / " + total + " PASSED.");
        if (passed == total) {
            System.out.println(">>> ALL UNIT TESTS PASSED SUCCESSFULLY! <<<");
        } else {
            System.err.println(">>> SOME TESTS FAILED! <<<");
            System.exit(1);
        }
        System.out.println("==================================================");
    }

    private static void printPass(String message) {
        System.out.println("  [PASS] " + message);
    }

    private static void printFail(String message) {
        System.out.println("  [FAIL] " + message);
    }
}
