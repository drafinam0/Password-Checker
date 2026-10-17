package src;

import java.util.Scanner;

/**
 * ConsoleApp provides an interactive command-line interface for the
 * Password Strength Checker using Scanner(System.in).
 * Ideal for terminal execution, classroom viva demonstrations, and CLI testing.
 */
public class ConsoleApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        printHeader();

        while (running) {
            System.out.println("\n--------------------------------------------------");
            System.out.println("  1. Check Password Strength");
            System.out.println("  2. Generate a Secure Password (5/5)");
            System.out.println("  3. Run Built-in Test Suite");
            System.out.println("  4. Exit Application");
            System.out.print("  Select an option (1-4): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handlePasswordCheck(scanner);
                    break;
                case "2":
                    handlePasswordGeneration(scanner);
                    break;
                case "3":
                    runTestSuite();
                    break;
                case "4":
                    running = false;
                    System.out.println("\n[+] Thank you for using Password Strength Checker! Stay Secure.\n");
                    break;
                default:
                    System.out.println("[!] Invalid option. Please enter 1, 2, 3, or 4.");
            }
        }

        scanner.close();
    }

    private static void printHeader() {
        System.out.println("==================================================");
        System.out.println("   CYBERSECURITY LAB: PASSWORD STRENGTH CHECKER   ");
        System.out.println("               Pure Java Edition                  ");
        System.out.println("==================================================");
    }

    private static void handlePasswordCheck(Scanner scanner) {
        System.out.print("\n[?] Enter password to analyze: ");
        String password = scanner.nextLine();

        PasswordResult result = PasswordAnalyzer.analyze(password);
        displayAnalysis(password, result);
    }

    private static void handlePasswordGeneration(Scanner scanner) {
        System.out.print("\n[?] Enter desired length (8-32, press Enter for default 16): ");
        String input = scanner.nextLine().trim();
        int length = 16;
        if (!input.isEmpty()) {
            try {
                length = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[*] Invalid number. Using default length: 16.");
                length = 16;
            }
        }

        String generated = PasswordGenerator.generate(length);
        PasswordResult result = PasswordAnalyzer.analyze(generated);

        System.out.println("\n================ GENERATED PASSWORD ================");
        System.out.println(" Password: " + generated);
        System.out.println(" Length:   " + generated.length());
        System.out.println(" Score:    " + result.getScore() + "/5 (" + result.getRating() + ")");
        System.out.println("====================================================");
    }

    public static void displayAnalysis(String password, PasswordResult result) {
        System.out.println("\n================ ANALYSIS RESULTS ================");
        System.out.println("Password Length: " + (password != null ? password.length() : 0));
        System.out.println("Score:           " + result.getScore() + "/5");
        System.out.println("Rating:          [" + result.getRating() + "]");
        System.out.println("Feedback:        " + result.getFeedbackMessage());

        if (result.isCommonPassword()) {
            System.out.println("\n[!] CRITICAL WARNING: Password matches a common dictionary word!");
        }

        System.out.println("\n--- Requirement Checklist ---");
        printCheckItem("At least 8 characters", result.isHasMinLength());
        printCheckItem("Uppercase letter (A-Z)", result.isHasUppercase());
        printCheckItem("Lowercase letter (a-z)", result.isHasLowercase());
        printCheckItem("Number/Digit (0-9)", result.isHasDigit());
        printCheckItem("Special character (@, #, $, etc.)", result.isHasSpecial());

        if (!result.getSuggestions().isEmpty()) {
            System.out.println("\n--- Improvement Suggestions ---");
            for (int i = 0; i < result.getSuggestions().size(); i++) {
                System.out.println("  " + (i + 1) + ". " + result.getSuggestions().get(i));
            }
        } else {
            System.out.println("\n[+] All 5 rules satisfied! No suggestions needed.");
        }
        System.out.println("==================================================");
    }

    private static void printCheckItem(String label, boolean satisfied) {
        String symbol = satisfied ? "[PASS] [✓]" : "[FAIL] [✗]";
        System.out.println("  " + symbol + " " + label);
    }

    private static void runTestSuite() {
        System.out.println("\n>>> Running Predefined Test Suite <<<");
        String[] testPasswords = {
            "",
            "hello",
            "Hello123",
            "P@ssw0rd2024!",
            "ADMIN12345",
            "Ab1!"
        };

        for (String test : testPasswords) {
            System.out.println("\nTesting: \"" + test + "\"");
            PasswordResult res = PasswordAnalyzer.analyze(test);
            System.out.println("Score: " + res.getScore() + "/5 | Rating: " + res.getRating());
        }
    }
}
