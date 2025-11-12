package com.quizapp.answer;

import com.quizapp.quiz.Quiz;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Answer Collector - Member 3
 * Collects and stores answers from clients
 * 
 * Network Concepts:
 * - Thread-safe answer storage
 * - Concurrent access management
 * - Timestamp tracking
 * 
 * @author Member 3
 */
public class AnswerCollector {
    // Map: ClientID -> Map of (QuestionID -> AnswerData)
    private Map<String, Map<String, AnswerData>> clientAnswers;
    private Map<String, Long> answerTimestamps;
    private long questionStartTime;
    private int questionTimeLimit; // in seconds
    
    public AnswerCollector() {
        this.clientAnswers = new ConcurrentHashMap<>();
        this.answerTimestamps = new ConcurrentHashMap<>();
        this.questionTimeLimit = 30; // default 30 seconds
    }
    
    public AnswerCollector(int timeLimit) {
        this();
        this.questionTimeLimit = timeLimit;
    }
    
    /**
     * Inner class to store answer data
     */
    public static class AnswerData {
        private String questionId;
        private String answer;
        private long submissionTime;
        private boolean isLate;
        
        public AnswerData(String questionId, String answer, long submissionTime, boolean isLate) {
            this.questionId = questionId;
            this.answer = answer;
            this.submissionTime = submissionTime;
            this.isLate = isLate;
        }
        
        public String getQuestionId() { return questionId; }
        public String getAnswer() { return answer; }
        public long getSubmissionTime() { return submissionTime; }
        public boolean isLate() { return isLate; }
    }
    
    /**
     * Start timing for a new question
     */
    public void startQuestionTimer() {
        questionStartTime = System.currentTimeMillis();
    }
    
    /**
     * Record an answer from a client
     * Format: ANSWER|QuestionID|Answer
     */
    public boolean recordAnswer(String clientId, String questionId, String answer) {
        if (clientId == null || questionId == null || answer == null) {
            return false;
        }
        
        // Calculate if answer is late
        long currentTime = System.currentTimeMillis();
        long elapsedSeconds = (currentTime - questionStartTime) / 1000;
        boolean isLate = elapsedSeconds > questionTimeLimit;
        
        // Get or create client's answer map
        Map<String, AnswerData> answers = clientAnswers.computeIfAbsent(
            clientId, k -> new ConcurrentHashMap<>()
        );
        
        // Store the answer
        AnswerData answerData = new AnswerData(questionId, answer.toUpperCase(), currentTime, isLate);
        answers.put(questionId, answerData);
        
        // Record timestamp
        answerTimestamps.put(clientId + "_" + questionId, currentTime);
        
        System.out.println(String.format("📝 Answer recorded: Client=%s, Q=%s, Answer=%s, Late=%s",
            clientId, questionId, answer, isLate ? "YES" : "NO"));
        
        return true;
    }
    
    /**
     * Get answer for specific client and question
     */
    public AnswerData getAnswer(String clientId, String questionId) {
        Map<String, AnswerData> answers = clientAnswers.get(clientId);
        if (answers != null) {
            return answers.get(questionId);
        }
        return null;
    }
    
    /**
     * Check if client has answered a question
     */
    public boolean hasAnswered(String clientId, String questionId) {
        Map<String, AnswerData> answers = clientAnswers.get(clientId);
        return answers != null && answers.containsKey(questionId);
    }
    
    /**
     * Get all answers for a client
     */
    public Map<String, AnswerData> getClientAnswers(String clientId) {
        return clientAnswers.getOrDefault(clientId, new HashMap<>());
    }
    
    /**
     * Get number of answers for a question
     */
    public int getAnswerCount(String questionId) {
        int count = 0;
        for (Map<String, AnswerData> answers : clientAnswers.values()) {
            if (answers.containsKey(questionId)) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Get all clients who answered a specific question
     */
    public List<String> getRespondents(String questionId) {
        List<String> respondents = new ArrayList<>();
        for (Map.Entry<String, Map<String, AnswerData>> entry : clientAnswers.entrySet()) {
            if (entry.getValue().containsKey(questionId)) {
                respondents.add(entry.getKey());
            }
        }
        return respondents;
    }
    
    /**
     * Clear all answers (for new quiz)
     */
    public void clearAllAnswers() {
        clientAnswers.clear();
        answerTimestamps.clear();
        System.out.println("✓ All answers cleared");
    }
    
    /**
     * Clear answers for specific client
     */
    public void clearClientAnswers(String clientId) {
        clientAnswers.remove(clientId);
        System.out.println("✓ Cleared answers for client: " + clientId);
    }
    
    /**
     * Get statistics for a question
     */
    public String getQuestionStats(String questionId) {
        Map<String, Integer> answerDistribution = new HashMap<>();
        int totalAnswers = 0;
        int lateAnswers = 0;
        
        for (Map<String, AnswerData> answers : clientAnswers.values()) {
            AnswerData data = answers.get(questionId);
            if (data != null) {
                totalAnswers++;
                if (data.isLate()) lateAnswers++;
                
                String ans = data.getAnswer();
                answerDistribution.put(ans, answerDistribution.getOrDefault(ans, 0) + 1);
            }
        }
        
        StringBuilder stats = new StringBuilder();
        stats.append(String.format("\n📊 Stats for %s:%n", questionId));
        stats.append(String.format("   Total Answers: %d%n", totalAnswers));
        stats.append(String.format("   Late Answers: %d%n", lateAnswers));
        stats.append("   Distribution: ");
        answerDistribution.forEach((ans, count) -> 
            stats.append(String.format("%s=%d ", ans, count))
        );
        
        return stats.toString();
    }
    
    /**
     * Get time limit for questions
     */
    public int getQuestionTimeLimit() {
        return questionTimeLimit;
    }
    
    /**
     * Set time limit for questions
     */
    public void setQuestionTimeLimit(int seconds) {
        this.questionTimeLimit = seconds;
    }
    
    /**
     * Get total number of clients who submitted answers
     */
    public int getTotalRespondents() {
        return clientAnswers.size();
    }
    
    /**
     * Export answers summary
     */
    public String getAnswersSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("\n╔════════════════════════════════════════╗\n");
        summary.append("║      Answer Collection Summary         ║\n");
        summary.append("╠════════════════════════════════════════╣\n");
        summary.append(String.format("║ Total Respondents: %-19d║%n", clientAnswers.size()));
        summary.append(String.format("║ Time Limit: %-27d║%n", questionTimeLimit));
        summary.append("╚════════════════════════════════════════╝\n");
        return summary.toString();
    }
}
