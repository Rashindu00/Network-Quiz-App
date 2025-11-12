package com.quizapp.quiz;

/**
 * Quiz Question Model - Member 2
 * Represents a single quiz question with multiple choice options
 * 
 * @author Member 2
 */
public class Quiz {
    private String questionId;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer;
    private int points;
    private String category;
    
    /**
     * Constructor for Quiz question
     */
    public Quiz(String questionId, String questionText, String optionA, String optionB, 
                String optionC, String optionD, String correctAnswer, int points, String category) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAnswer = correctAnswer.toUpperCase();
        this.points = points;
        this.category = category;
    }
    
    /**
     * Simplified constructor with default points (10)
     */
    public Quiz(String questionId, String questionText, String optionA, String optionB, 
                String optionC, String optionD, String correctAnswer) {
        this(questionId, questionText, optionA, optionB, optionC, optionD, correctAnswer, 10, "General");
    }
    
    // Getters
    public String getQuestionId() {
        return questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public String getOptionA() {
        return optionA;
    }
    
    public String getOptionB() {
        return optionB;
    }
    
    public String getOptionC() {
        return optionC;
    }
    
    public String getOptionD() {
        return optionD;
    }
    
    public String getCorrectAnswer() {
        return correctAnswer;
    }
    
    public int getPoints() {
        return points;
    }
    
    public String getCategory() {
        return category;
    }
    
    /**
     * Get all options as array
     */
    public String[] getOptions() {
        return new String[] { optionA, optionB, optionC, optionD };
    }
    
    /**
     * Check if given answer is correct
     */
    public boolean isCorrectAnswer(String answer) {
        return correctAnswer.equalsIgnoreCase(answer.trim());
    }
    
    /**
     * Format question for network transmission
     * Format: QUESTION|ID|QuestionNumber|Text|A|B|C|D|Points|TimeLimit
     */
    public String formatForClient(int questionNumber, int timeLimit) {
        return String.format("QUESTION|%s|%d|%s|%s|%s|%s|%s|%d|%d", 
            questionId, questionNumber, questionText, optionA, optionB, optionC, optionD, points, timeLimit);
    }
    
    /**
     * Format question for network transmission (with default time limit)
     * Format: QUESTION|ID|Text|A|B|C|D
     */
    public String formatForClient() {
        return formatForClient(1, 30);  // Default: question 1, 30 seconds
    }
    
    /**
     * Get question display string
     */
    public String getDisplayString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔════════════════════════════════════════╗\n");
        sb.append(String.format("║ Question %s (%d points)%n", questionId, points));
        sb.append("╠════════════════════════════════════════╣\n");
        sb.append(String.format("║ %s%n", questionText));
        sb.append("╠════════════════════════════════════════╣\n");
        sb.append(String.format("║ A) %s%n", optionA));
        sb.append(String.format("║ B) %s%n", optionB));
        sb.append(String.format("║ C) %s%n", optionC));
        sb.append(String.format("║ D) %s%n", optionD));
        sb.append("╚════════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return String.format("Quiz[id=%s, category=%s, points=%d]", 
            questionId, category, points);
    }
}
