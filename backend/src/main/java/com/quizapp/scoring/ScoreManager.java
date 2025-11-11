package com.quizapp.scoring;

import com.quizapp.model.Score;
import com.quizapp.server.ConnectedClientsManager;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * ScoreManager.java - Member 4 Backend
 * Demonstrates: Thread-safe collections, Synchronization, Concurrent operations
 * 
 * Manages quiz scores:
 * 1. Store scores
 * 2. Calculate rankings
 * 3. Generate leaderboard
 */
public class ScoreManager {
    private ConcurrentHashMap<String, Score> scores;
    private final Object scoreLock = new Object();
    private ConnectedClientsManager clientsManager;
    
    public ScoreManager(ConnectedClientsManager clientsManager) {
        this.scores = new ConcurrentHashMap<>();
        this.clientsManager = clientsManager;
    }
    
    /**
     * Store a student score (thread-safe)
     */
    public void storeScore(Score score) {
        synchronized (scoreLock) {
            scores.put(score.getStudentId(), score);
            System.out.println("Score stored for: " + score.getStudentName() + 
                             " - " + score.getObtainedMarks() + "/" + score.getTotalMarks());
        }
    }
    
    /**
     * Get score for a specific student
     */
    public Score getScore(String studentId) {
        return scores.get(studentId);
    }
    
    /**
     * Get all scores
     */
    public Map<String, Score> getAllScores() {
        return new ConcurrentHashMap<>(scores);
    }
    
    /**
     * Get count of stored scores
     */
    public int getScoreCount() {
        return scores.size();
    }
    
    /**
     * Calculate and set rankings (thread-safe)
     */
    public void calculateRankings() {
        synchronized (scoreLock) {
            List<Score> sortedScores = scores.values().stream()
                .sorted((s1, s2) -> Double.compare(s2.getPercentage(), s1.getPercentage()))
                .collect(Collectors.toList());
            
            int rank = 1;
            for (Score score : sortedScores) {
                score.setRank(rank);
                scores.put(score.getStudentId(), score);
                rank++;
            }
            
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         Rankings Calculated            ║");
            System.out.println("╠════════════════════════════════════════╣");
            for (Score score : sortedScores) {
                System.out.println("║ " + score.getRank() + ". " + score.getStudentName() + 
                                 " - " + score.getPercentage() + "%");
            }
            System.out.println("╚════════════════════════════════════════╝\n");
        }
    }
    
    /**
     * Get leaderboard (sorted by scores)
     */
    public List<Score> getLeaderboard() {
        synchronized (scoreLock) {
            return scores.values().stream()
                .sorted((s1, s2) -> {
                    int compareMarks = Integer.compare(s2.getObtainedMarks(), s1.getObtainedMarks());
                    if (compareMarks != 0) return compareMarks;
                    return s1.getStudentName().compareTo(s2.getStudentName());
                })
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Get top N students
     */
    public List<Score> getTopStudents(int n) {
        synchronized (scoreLock) {
            return getLeaderboard().stream()
                .limit(n)
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Get highest score
     */
    public Score getHighestScore() {
        synchronized (scoreLock) {
            return scores.values().stream()
                .max((s1, s2) -> Integer.compare(s1.getObtainedMarks(), s2.getObtainedMarks()))
                .orElse(null);
        }
    }
    
    /**
     * Get lowest score
     */
    public Score getLowestScore() {
        synchronized (scoreLock) {
            return scores.values().stream()
                .min((s1, s2) -> Integer.compare(s1.getObtainedMarks(), s2.getObtainedMarks()))
                .orElse(null);
        }
    }
    
    /**
     * Get average score
     */
    public double getAverageScore() {
        synchronized (scoreLock) {
            if (scores.isEmpty()) return 0;
            return scores.values().stream()
                .mapToDouble(Score::getObtainedMarks)
                .average()
                .orElse(0);
        }
    }
    
    /**
     * Get class statistics
     */
    public Map<String, Object> getClassStatistics() {
        synchronized (scoreLock) {
            Map<String, Object> stats = new HashMap<>();
            
            if (scores.isEmpty()) {
                stats.put("totalStudents", 0);
                stats.put("averageScore", 0);
                stats.put("highestScore", 0);
                stats.put("lowestScore", 0);
                return stats;
            }
            
            Score highest = getHighestScore();
            Score lowest = getLowestScore();
            double average = getAverageScore();
            
            stats.put("totalStudents", scores.size());
            stats.put("averageScore", average);
            stats.put("highestScore", highest != null ? highest.getObtainedMarks() : 0);
            stats.put("lowestScore", lowest != null ? lowest.getObtainedMarks() : 0);
            stats.put("highestScoringStudent", highest != null ? highest.getStudentName() : "N/A");
            stats.put("lowestScoringStudent", lowest != null ? lowest.getStudentName() : "N/A");
            
            return stats;
        }
    }
    
    /**
     * Send scores to all connected students
     */
    public void broadcastScores() {
        synchronized (scoreLock) {
            for (Score score : scores.values()) {
                Map<String, Object> scoreData = new HashMap<>();
                scoreData.put("type", "SCORE");
                scoreData.put("studentId", score.getStudentId());
                scoreData.put("studentName", score.getStudentName());
                scoreData.put("obtainedMarks", score.getObtainedMarks());
                scoreData.put("totalMarks", score.getTotalMarks());
                scoreData.put("percentage", score.getPercentage());
                scoreData.put("rank", score.getRank());
                
                clientsManager.sendToClient(score.getStudentId(), scoreData);
            }
        }
    }
    
    /**
     * Clear all scores
     */
    public synchronized void clearAll() {
        scores.clear();
    }
    
    /**
     * Display leaderboard in console
     */
    public void displayLeaderboard() {
        synchronized (scoreLock) {
            System.out.println("\n╔════════════════════════════════════════════════════════╗");
            System.out.println("║              QUIZ LEADERBOARD                          ║");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            
            List<Score> leaderboard = getLeaderboard();
            if (leaderboard.isEmpty()) {
                System.out.println("║ No scores yet                                          ║");
            } else {
                int rank = 1;
                for (Score score : leaderboard) {
                    System.out.printf("║ %d. %-30s %d/%d (%.1f%%)        ║%n",
                        rank, score.getStudentName(), 
                        score.getObtainedMarks(), score.getTotalMarks(),
                        score.getPercentage());
                    rank++;
                }
            }
            
            System.out.println("╚════════════════════════════════════════════════════════╝\n");
        }
    }
}
