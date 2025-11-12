package com.quizapp.answer;

import com.quizapp.quiz.Quiz;
import com.quizapp.answer.AnswerCollector.AnswerData;

/**
 * Answer Validator - Member 3
 * Validates answers and calculates points
 * 
 * Network Concepts:
 * - Request-response protocol
 * - Instant feedback generation
 * - Data validation
 * 
 * @author Member 3
 */
public class AnswerValidator {
    private AnswerCollector answerCollector;
    private int penaltyForLateAnswer;
    private boolean allowLateAnswers;
    
    public AnswerValidator(AnswerCollector answerCollector) {
        this.answerCollector = answerCollector;
        this.penaltyForLateAnswer = 5; // default: -5 points for late answer
        this.allowLateAnswers = true; // default: accept late answers
    }
    
    /**
     * Result class for validation
     */
    public static class ValidationResult {
        private boolean isCorrect;
        private int pointsEarned;
        private String feedback;
        private String correctAnswer;
        private boolean wasLate;
        
        public ValidationResult(boolean isCorrect, int pointsEarned, String feedback, 
                               String correctAnswer, boolean wasLate) {
            this.isCorrect = isCorrect;
            this.pointsEarned = pointsEarned;
            this.feedback = feedback;
            this.correctAnswer = correctAnswer;
            this.wasLate = wasLate;
        }
        
        public boolean isCorrect() { return isCorrect; }
        public int getPointsEarned() { return pointsEarned; }
        public String getFeedback() { return feedback; }
        public String getCorrectAnswer() { return correctAnswer; }
        public boolean wasLate() { return wasLate; }
        
        /**
         * Format result for sending to client
         * Format: RESULT|CORRECT/WRONG|Points|Feedback
         */
        public String formatForClient() {
            String status = isCorrect ? "CORRECT" : "WRONG";
            return String.format("RESULT|%s|%d|%s", status, pointsEarned, feedback);
        }
        
        @Override
        public String toString() {
            return String.format("ValidationResult[correct=%s, points=%d, late=%s]",
                isCorrect, pointsEarned, wasLate);
        }
    }
    
    /**
     * Validate answer for a client
     */
    public ValidationResult validateAnswer(String clientId, Quiz question) {
        // Get the answer data
        AnswerData answerData = answerCollector.getAnswer(clientId, question.getQuestionId());
        
        if (answerData == null) {
            return new ValidationResult(false, 0, "No answer submitted", 
                question.getCorrectAnswer(), false);
        }
        
        // Check if answer is correct
        boolean isCorrect = question.isCorrectAnswer(answerData.getAnswer());
        boolean wasLate = answerData.isLate();
        
        // Calculate points
        int points = 0;
        String feedback;
        
        if (!allowLateAnswers && wasLate) {
            feedback = "❌ Answer submitted too late! No points awarded.";
            points = 0;
        } else if (isCorrect) {
            points = question.getPoints();
            if (wasLate) {
                points = Math.max(0, points - penaltyForLateAnswer);
                feedback = String.format("✓ Correct! (Late submission: -%d points)", penaltyForLateAnswer);
            } else {
                feedback = "✓ Correct! Well done!";
            }
        } else {
            feedback = String.format("✗ Wrong! The correct answer was: %s", 
                question.getCorrectAnswer());
            points = 0;
        }
        
        System.out.println(String.format("✓ Validated: Client=%s, Q=%s, Result=%s, Points=%d",
            clientId, question.getQuestionId(), isCorrect ? "CORRECT" : "WRONG", points));
        
        return new ValidationResult(isCorrect, points, feedback, 
            question.getCorrectAnswer(), wasLate);
    }
    
    /**
     * Quick validate - just check if answer is correct
     */
    public boolean isAnswerCorrect(String clientId, Quiz question) {
        AnswerData answerData = answerCollector.getAnswer(clientId, question.getQuestionId());
        if (answerData == null) {
            return false;
        }
        return question.isCorrectAnswer(answerData.getAnswer());
    }
    
    /**
     * Validate answer without late penalty
     */
    public ValidationResult validateAnswerNoPenalty(String clientId, Quiz question) {
        AnswerData answerData = answerCollector.getAnswer(clientId, question.getQuestionId());
        
        if (answerData == null) {
            return new ValidationResult(false, 0, "No answer submitted", 
                question.getCorrectAnswer(), false);
        }
        
        boolean isCorrect = question.isCorrectAnswer(answerData.getAnswer());
        int points = isCorrect ? question.getPoints() : 0;
        String feedback = isCorrect ? "✓ Correct!" : 
            String.format("✗ Wrong! Correct answer: %s", question.getCorrectAnswer());
        
        return new ValidationResult(isCorrect, points, feedback, 
            question.getCorrectAnswer(), answerData.isLate());
    }
    
    /**
     * Check if answer format is valid (A, B, C, or D)
     */
    public static boolean isValidAnswerFormat(String answer) {
        if (answer == null || answer.trim().isEmpty()) {
            return false;
        }
        String ans = answer.trim().toUpperCase();
        return ans.equals("A") || ans.equals("B") || ans.equals("C") || ans.equals("D");
    }
    
    /**
     * Generate instant feedback message
     */
    public String generateFeedback(boolean isCorrect, int points, boolean wasLate) {
        if (isCorrect) {
            if (wasLate) {
                return String.format("🟡 Correct! +%d points (Late submission)", points);
            } else {
                return String.format("🟢 Excellent! +%d points", points);
            }
        } else {
            return "🔴 Incorrect! 0 points";
        }
    }
    
    /**
     * Set penalty for late answers
     */
    public void setPenaltyForLateAnswer(int penalty) {
        this.penaltyForLateAnswer = penalty;
    }
    
    /**
     * Get penalty for late answers
     */
    public int getPenaltyForLateAnswer() {
        return penaltyForLateAnswer;
    }
    
    /**
     * Set whether to allow late answers
     */
    public void setAllowLateAnswers(boolean allow) {
        this.allowLateAnswers = allow;
    }
    
    /**
     * Check if late answers are allowed
     */
    public boolean isAllowLateAnswers() {
        return allowLateAnswers;
    }
    
    /**
     * Get validation settings summary
     */
    public String getValidationSettings() {
        StringBuilder settings = new StringBuilder();
        settings.append("\n╔════════════════════════════════════════╗\n");
        settings.append("║      Validation Settings               ║\n");
        settings.append("╠════════════════════════════════════════╣\n");
        settings.append(String.format("║ Allow Late Answers: %-18s║%n", 
            allowLateAnswers ? "YES" : "NO"));
        settings.append(String.format("║ Late Answer Penalty: %-17d║%n", 
            penaltyForLateAnswer));
        settings.append("╚════════════════════════════════════════╝\n");
        return settings.toString();
    }
}
