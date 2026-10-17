# MINI-PROJECT REPORT
## PASSWORD STRENGTH CHECKER USING JAVA

---

### 1. PROJECT TITLE
**CyberShield: An Intelligent Password Strength Checker and Secure Credential Generator Using Pure Java and Modern Web Technologies**

---

### 2. ABSTRACT
In today’s digital landscape, weak and predictable passwords remain the primary entry point for credential stuffing, automated dictionary attacks, and unauthorized system access. This mini-project presents **Password Strength Checker**, a lightweight, self-contained cybersecurity software application built using pure **Java** for the core evaluation engine, coupled with an ultra-modern, pure-black cybersecurity web user interface.

The application rigorously inspects user-supplied passwords across five fundamental security dimensions: minimum length, uppercase characters, lowercase characters, numeric digits, and special symbols. Each satisfied criterion contributes toward an overall score (0–5), which maps directly to a discrete rating category (`WEAK`, `MEDIUM`, or `STRONG`). The system provides dynamic visual feedback through a colored segmented meter, an interactive requirements checklist, and actionable improvement suggestions tailored to the specific missing criteria. Additionally, the application integrates a cryptographically secure random password generator to provide users with tamper-proof, 5/5 rated credentials. The backend relies solely on standard Java SE libraries (`jdk.httpserver`, `Character`, `String`, `SecureRandom`) without demanding third-party frameworks, external build tools, or databases, making it an ideal, high-performing educational project for college demonstrations and viva examinations.

---

### 3. INTRODUCTION
Authentication mechanisms predominantly rely on secret passwords to verify user identity. Despite extensive warnings, users frequently gravitate toward easily remembered, low-complexity passwords such as sequential digits, dictionary words, or personal names. Consequently, client-side and server-side password strength meters have become an indispensable security requirement across modern web portals, banking systems, and enterprise networks.

This project bridges theoretical computer science concepts (string manipulation, ASCII character sets, algorithmic evaluation, and finite-state logic) with practical software engineering and UI design. Developed using standard Java SE, the project illustrates how clean, modular object-oriented programming can be harmonized with contemporary frontend engineering to deliver an engaging, educational, and secure end-user experience.

---

### 4. PROBLEM STATEMENT
A significant percentage of data breaches stem directly from compromised credentials caused by weak password hygiene. End-users frequently make common mistakes, including:
1. Selecting short passwords (under 8 characters).
2. Omitting numeric digits or special symbols.
3. Using easily guessable dictionary words or repeated character sequences.
4. Failing to realize *which* specific components their passwords lack due to vague or non-existent error messages.

There is a distinct need for a transparent, educational tool that not only evaluates password complexity in real time but also explicitly diagnoses missing elements and guides users toward robust password creation while guaranteeing that sensitive input is never persisted or logged.

---

### 5. OBJECTIVES
The core objectives of this mini-project are:
1. **Accurate Complexity Analysis**: To implement a robust Java evaluation engine evaluating five core criteria:
   - Length $\ge 8$ characters.
   - At least one uppercase letter ($A–Z$).
   - At least one lowercase letter ($a–z$).
   - At least one digit ($0–9$).
   - At least one special symbol (`!@#$%^&*...`).
2. **Deterministic Scoring**: To calculate an integer score from 0 to 5 and classify passwords into three distinct categories: `WEAK` (0–2), `MEDIUM` (3–4), and `STRONG` (5).
3. **Actionable Remediation**: To generate clear, user-friendly suggestions indicating exactly which rules were missed.
4. **Dictionary/Common Password Alert**: To warn users when entering notoriously vulnerable passwords (e.g., `password123`, `admin`, `12345678`).
5. **Secure Credential Generation**: To provide a built-in generator that constructs random, high-entropy passwords guaranteed to achieve a perfect 5/5 score.
6. **Zero External Dependencies**: To eliminate cumbersome frameworks (like Spring Boot, Maven, or MySQL) by utilizing standard JDK built-ins.
7. **Premium Cybersecurity UI**: To present the tool via a high-contrast, pure-black aesthetic accented with glowing cyan, purple, and green elements suitable for technical demonstrations.

---

### 6. SOFTWARE AND HARDWARE REQUIREMENTS

#### 6.1 Software Requirements
* **Operating System**: Windows 10/11, macOS, or Linux (cross-platform).
* **Programming Language**: Java Development Kit (JDK) version 11 or higher (OpenJDK 21 LTS recommended).
* **Development Environment (IDE)**: Visual Studio Code, Eclipse, or IntelliJ IDEA.
* **Web Browser**: Any modern HTML5/CSS3 compliant browser (Google Chrome, Microsoft Edge, Mozilla Firefox, Brave, Safari).
* **Command Line Shell**: PowerShell, Command Prompt, or Bash.

#### 6.2 Hardware Requirements
* **Processor**: Dual-Core x86/x64 or ARM processor (1.6 GHz or faster).
* **RAM**: Minimum 2 GB (4 GB recommended).
* **Storage Space**: Under 50 MB available disk space.
* **Display Resolution**: $1024 \times 768$ minimum (Responsive design adapts to desktop, tablet, and mobile displays).

---

### 7. SYSTEM DESIGN & ARCHITECTURE

The application adopts a clean, layered architecture separating the **Presentation Layer (Web UI)**, the **Communication Layer (Embedded Java HTTP Server)**, and the **Business Logic Layer (Pure Java Analysis & Generator)**.

```mermaid
graph TD
    A["Web Browser (Frontend UI)<br>Pure Black Cyber Interface"] -->|"HTTP POST /api/check<br>{password: '...'}"| B["Java Embedded Server<br>(WebServer.java)"]
    A -->|"HTTP GET /api/generate"| B
    B -->|"Invokes analyze()"| C["PasswordAnalyzer.java<br>(Core 5-Rule Logic)"]
    B -->|"Invokes generate()"| D["PasswordGenerator.java<br>(SecureRandom)"]
    C -->|"Returns PasswordResult"| B
    D -->|"Returns Random Password"| B
    B -->|"Sends JSON Response"| A
    E["ConsoleApp.java<br>(Interactive CLI)"] -->|"Direct Java Call"| C
    E -->|"Direct Java Call"| D
```

#### Layer Responsibilities:
1. **Business Logic Layer**:
   - `PasswordAnalyzer.java`: Evaluates inputs against the 5 criteria, tallies points, assigns ratings, and compiles improvement suggestions.
   - `PasswordGenerator.java`: Assembles cryptographic random characters using `java.security.SecureRandom`.
   - `PasswordResult.java`: Encapsulates result state and outputs JSON.
2. **Service / Communication Layer**:
   - `WebServer.java`: Listens on port 8080 using `com.sun.net.httpserver.HttpServer`. Routes requests, serves static frontend assets (`HTML`, `CSS`, `JS`), and delivers JSON API responses.
3. **Presentation Layer**:
   - `index.html`, `style.css`, `app.js`: Displays interactive cards, live score indicators, glowing status meters, and checklist items.
   - `ConsoleApp.java`: Provides a secondary interactive terminal CLI with `Scanner`.

---

### 8. ALGORITHM

#### Algorithm: `PasswordAnalyzer(password)`

```
Input:  A string 'password'
Output: PasswordResult object containing score, rating, flags, suggestions, feedback

Step 1:  Initialize boolean flags:
         hasMinLength = false
         hasUppercase = false
         hasLowercase = false
         hasDigit     = false
         hasSpecial   = false

Step 2:  If password is NULL or empty string after trimming:
             Set isEmpty = true, score = 0, rating = "WEAK"
             Set feedbackMessage = "Password cannot be empty."
             Return result.

Step 3:  Check length:
             If length(password) >= 8, then set hasMinLength = true.

Step 4:  Iterate through each character 'ch' at index i from 0 to length(password) - 1:
             a. If Character.isUpperCase(ch) is TRUE:
                    Set hasUppercase = true
             b. Else if Character.isLowerCase(ch) is TRUE:
                    Set hasLowercase = true
             c. Else if Character.isDigit(ch) is TRUE:
                    Set hasDigit = true
             d. Else if ch exists in SPECIAL_CHARACTERS set:
                    Set hasSpecial = true

Step 5:  Calculate score:
             score = 0
             If hasMinLength is true, score = score + 1
             If hasUppercase is true, score = score + 1
             If hasLowercase is true, score = score + 1
             If hasDigit is true,     score = score + 1
             If hasSpecial is true,   score = score + 1

Step 6:  Classify rating based on score:
             If score <= 2:
                 rating = "WEAK"
             Else if score <= 4:
                 rating = "MEDIUM"
             Else:
                 rating = "STRONG"

Step 7:  Check if password matches dictionary of known common passwords:
             If password.toLowerCase() is in COMMON_PASSWORDS:
                 Set isCommonPassword = true

Step 8:  Generate specific suggestions:
             If hasMinLength is false, add "Add at least 8 characters."
             If hasUppercase is false, add "Include at least one uppercase letter (A–Z)."
             If hasLowercase is false, add "Include at least one lowercase letter (a–z)."
             If hasDigit is false,     add "Add at least one number (0–9)."
             If hasSpecial is false,   add "Include at least one special character."

Step 9:  Construct and return PasswordResult.
```

---

### 9. FLOWCHART

```mermaid
flowchart TD
    Start([User Enters Password]) --> CheckEmpty{Is input empty?}
    CheckEmpty -- Yes --> AlertEmpty[Display Empty Warning<br>Score = 0/5, WEAK]
    AlertEmpty --> End([Wait for User Action])

    CheckEmpty -- No --> InitFlags[Initialize 5 Flags to False<br>hasMinLength, hasUpper, hasLower, hasDigit, hasSpecial]
    InitFlags --> CheckLen{Length >= 8?}
    CheckLen -- Yes --> SetLen[hasMinLength = True]
    CheckLen -- No --> LoopStart[Iterate through Characters]
    SetLen --> LoopStart

    LoopStart --> CharEval{Examine Character ch}
    CharEval -->|Character.isUpperCase| SetUpper[hasUpper = True]
    CharEval -->|Character.isLowerCase| SetLower[hasLower = True]
    CharEval -->|Character.isDigit| SetDigit[hasDigit = True]
    CharEval -->|Special Character| SetSpecial[hasSpecial = True]

    SetUpper --> NextChar{More Characters?}
    SetLower --> NextChar
    SetDigit --> NextChar
    SetSpecial --> NextChar

    NextChar -- Yes --> LoopStart
    NextChar -- No --> ComputeScore[Calculate Score = Sum of True Flags<br>Max: 5]

    ComputeScore --> ScoreClass{Evaluate Score}
    ScoreClass -->|0 to 2| WeakRating[Rating = WEAK<br>Color = Red]
    ScoreClass -->|3 to 4| MediumRating[Rating = MEDIUM<br>Color = Orange/Amber]
    ScoreClass -->|5| StrongRating[Rating = STRONG<br>Color = Green]

    WeakRating --> GenSuggestions[Compile Missing Rule Suggestions]
    MediumRating --> GenSuggestions
    StrongRating --> ShieldSuccess[Display Shield Activated Banner]

    GenSuggestions --> CommonCheck{Is common password?}
    ShieldSuccess --> CommonCheck

    CommonCheck -- Yes --> CommonAlert[Add Critical Common Password Warning]
    CommonCheck -- No --> UpdateUI[Update Checklist, Meter & UI]
    CommonAlert --> UpdateUI

    UpdateUI --> End
```

---

### 10. IMPLEMENTATION DETAILS

The implementation is structured around beginner-friendly, clean Java conventions:

| Component | Technology / Mechanism | Purpose |
|-----------|------------------------|---------|
| **Core Analysis** | `src.PasswordAnalyzer` | Contains static analysis method performing linear traversal over character arrays. |
| **Data Container** | `src.PasswordResult` | Encapsulates scores, status flags, and custom JSON serialization. |
| **Credential Generator** | `src.PasswordGenerator` | Utilizes `java.security.SecureRandom` to generate 5/5 compliant passwords. |
| **Embedded Web Server** | `src.WebServer` (`jdk.httpserver`) | Zero-dependency HTTP server handling REST endpoints (`/api/check`, `/api/generate`). |
| **CLI Console Runner** | `src.ConsoleApp` (`java.util.Scanner`) | Interactive terminal tool for command-line demonstrations. |
| **User Interface** | Semantic HTML5, CSS3, ES6 JS | Cyberpunk-themed pure black UI with live DOM updates and smooth animations. |

---

### 11. SOURCE CODE EXPLANATION (KEY JAVA CONCEPTS)

To help beginners understand and explain the codebase during college presentations, here is a breakdown of the core Java concepts utilized:

#### 11.1 Character Traversal with `charAt()` and `length()`
```java
for (int i = 0; i < password.length(); i++) {
    char ch = password.charAt(i);
    // inspect character
}
```
* **Explanation**: The `length()` method returns the total number of characters in the `String`. The `charAt(i)` method retrieves the single character located at index `i` (starting from zero). A simple `for` loop inspects each character one by one in $O(N)$ time complexity.

#### 11.2 Character Classification Methods (`Character` Wrapper Class)
* `Character.isUpperCase(ch)`: Returns `true` if the character is an uppercase letter ($A$ through $Z$).
* `Character.isLowerCase(ch)`: Returns `true` if the character is a lowercase letter ($a$ through $z$).
* `Character.isDigit(ch)`: Returns `true` if the character is a numeric digit ($0$ through $9$).
* **Benefit**: Beginners do not need to memorize ASCII ranges (such as 65–90 or 97–122); Java’s built-in methods handle Unicode character checks automatically and reliably.

#### 11.3 Boolean Flags
```java
boolean hasMinLength = (password.length() >= 8);
boolean hasUppercase = false;
boolean hasLowercase = false;
boolean hasDigit = false;
boolean hasSpecial = false;
```
* **Explanation**: Five independent boolean variables keep track of whether each specific security criterion has been met. This allows the system to easily report *which* criteria failed and supply exact remediation tips.

#### 11.4 Score Calculation & `if-else` Classification
```java
int score = 0;
if (hasMinLength) score++;
if (hasUppercase) score++;
if (hasLowercase) score++;
if (hasDigit) score++;
if (hasSpecial) score++;

if (score <= 2) {
    rating = "WEAK";
} else if (score <= 4) {
    rating = "MEDIUM";
} else {
    rating = "STRONG";
}
```
* **Explanation**: Each satisfied rule increments the integer `score` by 1. A clean `if-else if-else` structure classifies the score into three standard cybersecurity tiers.

#### 11.5 Secure Random Generation (`SecureRandom`)
```java
SecureRandom random = new SecureRandom();
```
* **Explanation**: Unlike standard `java.util.Random`, `SecureRandom` produces non-deterministic, cryptographically strong random values that cannot be predicted by an adversary.

---

### 12. TEST CASES AND EXPECTED OUTPUTS

The following comprehensive test matrix was executed using both the automated unit test runner (`TestRunner.java`) and the real-time web application:

| Test ID | Input Password | Expected Score | Expected Rating | Length $\ge$ 8 | Upper (A-Z) | Lower (a-z) | Digit (0-9) | Special Symbol | Actionable Suggestions Generated |
|:-------:|:---------------|:--------------:|:---------------:|:--------------:|:-----------:|:-----------:|:-----------:|:--------------:|:---------------------------------|
| **TC-01** | `""` (Empty) | `0/5` | `WEAK` | ✗ | ✗ | ✗ | ✗ | ✗ | Warning: Password input is empty. |
| **TC-02** | `"hello"` | `1/5` | `WEAK` | ✗ | ✗ | ✓ | ✗ | ✗ | Add length $\ge 8$, Uppercase, Digit, Special char. |
| **TC-03** | `"Hello123"` | `4/5` | `MEDIUM` | ✓ | ✓ | ✓ | ✓ | ✗ | Include at least one special character. |
| **TC-04** | `"P@ssw0rd2024!"` | `5/5` | `STRONG` | ✓ | ✓ | ✓ | ✓ | ✓ | None. (Shield Activated Success Banner). |
| **TC-05** | `"ADMIN12345"` | `3/5` | `MEDIUM` | ✓ | ✓ | ✗ | ✓ | ✗ | Include lowercase letter and special character. |
| **TC-06** | `"Ab1!"` | `4/5` | `MEDIUM` | ✗ | ✓ | ✓ | ✓ | ✓ | Add at least 8 characters (current: 4). |
| **TC-07** | `"password123"` | `3/5` | `MEDIUM` | ✓ | ✗ | ✓ | ✓ | ✗ | Critical warning: Common dictionary password! |
| **TC-08** | Generated (16) | `5/5` | `STRONG` | ✓ | ✓ | ✓ | ✓ | ✓ | Perfect score guaranteed. |

---

### 13. ADVANTAGES
1. **Lightweight & Fast**: Boots in under 1 second; performs checks in sub-millisecond execution time.
2. **Zero Configuration**: No database, no Node.js, and no external Maven/Gradle repository downloads required.
3. **High Educational Value**: Simple, clean Java code that students can easily explain in vivas and interviews.
4. **Enhanced Privacy**: Passwords reside only in transient memory during analysis; nothing is logged or persisted to disk.
5. **Modern, Responsive UI**: High visual appeal with glowing cybersecurity aesthetics, animated meters, and instant feedback.
6. **Dual Operating Modes**: Can be run as a full modern web dashboard or directly in the console terminal.

---

### 14. LIMITATIONS
1. **Static Dictionary Size**: The current common-password detector checks a curated set of known weak passwords rather than a multi-gigabyte HaveIBeenPwned database.
2. **Context-Insensitive Entropy**: Score relies on 5 rule satisfaction rather than Shannon mathematical entropy.
3. **Local Scope**: Runs on localhost; requires network port forwarding or hosting for multi-user internet access.

---

### 15. FUTURE ENHANCEMENTS
1. **HIBP API Integration**: Integrating HaveIBeenPwned k-Anonymity API to check passwords against billions of compromised credentials.
2. **Crack Time Estimation**: Computing estimated brute-force crack time based on common GPU hash cracking speeds (e.g., RTX 4090 Hashcat benchmarks).
3. **Password Policy Customizer**: Allowing administrators to customize minimum length and required character sets for corporate environments.
4. **Multi-Language Support**: Translating UI suggestions and tips into regional languages.

---

### 16. CONCLUSION
The **Password Strength Checker Using Java** successfully demonstrates that critical cybersecurity principles can be effectively delivered through elegant, beginner-friendly software design. By combining Java’s strong character processing capabilities with an ultra-modern, pure-black web UI, the project provides real-time, constructive feedback that empowers users to adopt strong authentication habits. The project completely satisfies all functional, architectural, design, and academic requirements for a high-scoring college mini-project.

---
*Report Prepared for College Mini-Project Submission & Viva Voce.*
