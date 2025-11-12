package com.quizapp.score;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Score Manager - Member 4
 * Tracks and manages scores for all clients
 * 
 * Network Concepts:
 * - Thread-safe score tracking
 * - Real-time score updates
 * - Concurrent access management
 * 
 * @author Member 4
 */
public class ScoreManager {
    // Map: ClientID -> ClientScore
    private Map<String, ClientScore> clientScores;
    private Map<String, String> clientNames; // ClientID -> Name
    
    public ScoreManager() {
        this.clientScores = new ConcurrentHashMap<>();
        this.clientNames = new ConcurrentHashMap<>();
    }
    
    /**
     * Inner class to track individual client score
     */
    public static class ClientScore {
        private String clientId;
        private String clientName;
        private int totalScore;
        private int correctAnswers;
        private int wrongAnswers;
        private int questionsAttempted;
        private long lastUpdateTime;
        
        public ClientScore(String clientId, String clientName) {
            this.clientId = clientId;
            this.clientName = clientName;
            this.totalScore = 0;
            this.correctAnswers = 0;
            this.wrongAnswers = 0;
            this.questionsAttempted = 0;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public void addScore(int points, boolean isCorrect) {
            this.totalScore += points;
            if (isCorrect) {
                this.correctAnswers++;
            } else {
                this.wrongAnswers++;
            }
            this.questionsAttempted++;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        public double getAccuracy() {
            if (questionsAttempted == 0) return 0.0;
            return (correctAnswers * 100.0) / questionsAttempted;
        }
        
        // Getters
        public String getClientId() { return clientId; }
        public String getClientName() { return clientName; }
        public int getTotalScore() { return totalScore; }
        public int getCorrectAnswers() { return correctAnswers; }
        public int getWrongAnswers() { return wrongAnswers; }
        public int getQuestionsAttempted() { return questionsAttempted; }
        public long getLastUpdateTime() { return lastUpdateTime; }
        
        @Override
        public String toString() {
            return String.format("%s: %d points (%d/%d correct)", 
                clientName, totalScore, correctAnswers, questionsAttempted);
        }
    }
    
    /**
     * Register a client
     */
    public void registerClient(String clientId, String clientName) {
        clientNames.put(clientId, clientName);
        clientScores.putIfAbsent(clientId, new ClientScore(clientId, clientName));
        System.out.println("✓ Registered client for scoring: " + clientName);
    }
    
    /**
     * Update score for a client
     */
    public void updateScore(String clientId, int points, boolean isCorrect) {
        ClientScore score = clientScores.get(clientId);
        if (score != null) {
            score.addScore(points, isCorrect);
            System.out.println(String.format("📊 Score updated: %s now has %d points",
                score.getClientName(), score.getTotalScore()));
        } else {
            System.err.println("⚠ Cannot update score for unregistered client: " + clientId);
        }
    }
    
    /**
     * Get score for a client
     */
    public ClientScore getScore(String clientId) {
        return clientScores.get(clientId);
    }
    
    /**
     * Get total score for a client
     */
    public int getTotalScore(String clientId) {
        ClientScore score = clientScores.get(clientId);
        return score != null ? score.getTotalScore() : 0;
    }
    
    /**
     * Get all client scores
     */
    public Collection<ClientScore> getAllScores() {
        return clientScores.values();
    }
    
    /**
     * Get sorted scores (descending order)
     */
    public List<ClientScore> getSortedScores() {
        List<ClientScore> scores = new ArrayList<>(clientScores.values());
        scores.sort((a, b) -> {
            // Sort by total score (descending)
            int scoreCompare = Integer.compare(b.getTotalScore(), a.getTotalScore());
            if (scoreCompare != 0) return scoreCompare;
            
            // If scores are equal, sort by accuracy
            int accuracyCompare = Double.compare(b.getAccuracy(), a.getAccuracy());
            if (accuracyCompare != 0) return accuracyCompare;
            
            // If still equal, sort by name
            return a.getClientName().compareTo(b.getClientName());
        });
        return scores;
    }
    
    /**
     * Get rank for a client (1-based)
     */
    public int getRank(String clientId) {
        List<ClientScore> sorted = getSortedScores();
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getClientId().equals(clientId)) {
                return i + 1;
            }
        }
        return -1; // Not found
    }
    
    /**
     * Get top N scorers
     */
    public List<ClientScore> getTopScorers(int n) {
        List<ClientScore> sorted = getSortedScores();
        return sorted.subList(0, Math.min(n, sorted.size()));
    }
    
    /**
     * Get winner (highest score)
     */
    public ClientScore getWinner() {
        List<ClientScore> sorted = getSortedScores();
        return sorted.isEmpty() ? null : sorted.get(0);
    }
    
    /**
     * Reset all scores
     */
    public void resetAllScores() {
        clientScores.clear();
        // Re-register all clients with zero scores
        for (Map.Entry<String, String> entry : clientNames.entrySet()) {
            clientScores.put(entry.getKey(), new ClientScore(entry.getKey(), entry.getValue()));
        }
        System.out.println("✓ All scores reset");
    }
    
    /**
     * Remove client from scoring
     */
    public void removeClient(String clientId) {
        clientScores.remove(clientId);
        clientNames.remove(clientId);
        System.out.println("✓ Removed client from scoring: " + clientId);
    }
    
    /**
     * Get statistics summary
     */
    public String getScoreStatistics() {
        if (clientScores.isEmpty()) {
            return "No scores available";
        }
        
        int totalClients = clientScores.size();
        int totalPoints = 0;
        int totalCorrect = 0;
        int totalAttempted = 0;
        
        for (ClientScore score : clientScores.values()) {
            totalPoints += score.getTotalScore();
            totalCorrect += score.getCorrectAnswers();
            totalAttempted += score.getQuestionsAttempted();
        }
        
        double avgScore = totalPoints / (double) totalClients;
        double avgAccuracy = totalAttempted > 0 ? (totalCorrect * 100.0) / totalAttempted : 0;
        
        StringBuilder stats = new StringBuilder();
        stats.append("\n╔════════════════════════════════════════╗\n");
        stats.append("║       Score Statistics                 ║\n");
        stats.append("╠════════════════════════════════════════╣\n");
        stats.append(String.format("║ Total Participants: %-18d║%n", totalClients));
        stats.append(String.format("║ Average Score: %-23.1f║%n", avgScore));
        stats.append(String.format("║ Average Accuracy: %-19.1f%%║%n", avgAccuracy));
        stats.append(String.format("║ Total Questions Attempted: %-11d║%n", totalAttempted));
        stats.append("╚════════════════════════════════════════╝\n");
        
        return stats.toString();
    }
    
    /**
     * Format score update for broadcasting
     * Format: SCORE_UPDATE|ClientID|Score|Rank
     */
    public String formatScoreUpdate(String clientId) {
        ClientScore score = getScore(clientId);
        if (score == null) return null;
        
        int rank = getRank(clientId);
        return String.format("SCORE_UPDATE|%s|%s|%d|%d",
            clientId, score.getClientName(), score.getTotalScore(), rank);
    }
    
    /**
     * Get detailed score display for a client
     */
    public String getClientScoreDisplay(String clientId) {
        ClientScore score = getScore(clientId);
        if (score == null) {
            return "Score not found for client: " + clientId;
        }
        
        int rank = getRank(clientId);
        
        StringBuilder display = new StringBuilder();
        display.append("\n╔════════════════════════════════════════╗\n");
        display.append(String.format("║ Player: %-30s║%n", score.getClientName()));
        display.append("╠════════════════════════════════════════╣\n");
        display.append(String.format("║ Rank: #%-32d║%n", rank));
        display.append(String.format("║ Total Score: %-25d║%n", score.getTotalScore()));
        display.append(String.format("║ Correct Answers: %-21d║%n", score.getCorrectAnswers()));
        display.append(String.format("║ Wrong Answers: %-23d║%n", score.getWrongAnswers()));
        display.append(String.format("║ Accuracy: %-27.1f%%║%n", score.getAccuracy()));
        display.append("╚════════════════════════════════════════╝\n");
        
        return display.toString();
    }
}
