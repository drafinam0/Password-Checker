package src;

import java.util.ArrayList;
import java.util.List;

/**
 * PasswordResult encapsulates the outcome of a password strength evaluation.
 * Stores the score (0-5), classification, boolean flags for the 5 criteria,
 * specific actionable suggestions, and status flags.
 */
public class PasswordResult {
    private int score;
    private String rating; // WEAK, MEDIUM, STRONG
    private boolean hasMinLength;
    private boolean hasUppercase;
    private boolean hasLowercase;
    private boolean hasDigit;
    private boolean hasSpecial;
    private boolean isCommonPassword;
    private boolean isEmpty;
    private List<String> suggestions;
    private String feedbackMessage;

    public PasswordResult() {
        this.suggestions = new ArrayList<>();
    }

    // Getters and Setters
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public boolean isHasMinLength() {
        return hasMinLength;
    }

    public void setHasMinLength(boolean hasMinLength) {
        this.hasMinLength = hasMinLength;
    }

    public boolean isHasUppercase() {
        return hasUppercase;
    }

    public void setHasUppercase(boolean hasUppercase) {
        this.hasUppercase = hasUppercase;
    }

    public boolean isHasLowercase() {
        return hasLowercase;
    }

    public void setHasLowercase(boolean hasLowercase) {
        this.hasLowercase = hasLowercase;
    }

    public boolean isHasDigit() {
        return hasDigit;
    }

    public void setHasDigit(boolean hasDigit) {
        this.hasDigit = hasDigit;
    }

    public boolean isHasSpecial() {
        return hasSpecial;
    }

    public void setHasSpecial(boolean hasSpecial) {
        this.hasSpecial = hasSpecial;
    }

    public boolean isCommonPassword() {
        return isCommonPassword;
    }

    public void setCommonPassword(boolean commonPassword) {
        isCommonPassword = commonPassword;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }

    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }

    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    /**
     * Serializes this result object into a clean JSON string
     * without requiring any third-party external JSON libraries.
     */
    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"score\":").append(score).append(",");
        json.append("\"rating\":\"").append(escapeJson(rating)).append("\",");
        json.append("\"hasMinLength\":").append(hasMinLength).append(",");
        json.append("\"hasUppercase\":").append(hasUppercase).append(",");
        json.append("\"hasLowercase\":").append(hasLowercase).append(",");
        json.append("\"hasDigit\":").append(hasDigit).append(",");
        json.append("\"hasSpecial\":").append(hasSpecial).append(",");
        json.append("\"isCommonPassword\":").append(isCommonPassword).append(",");
        json.append("\"isEmpty\":").append(isEmpty).append(",");
        json.append("\"feedbackMessage\":\"").append(escapeJson(feedbackMessage)).append("\",");
        
        json.append("\"suggestions\":[");
        for (int i = 0; i < suggestions.size(); i++) {
            json.append("\"").append(escapeJson(suggestions.get(i))).append("\"");
            if (i < suggestions.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }
}
