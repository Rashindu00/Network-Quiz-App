package com.quizapp.result;

import com.quizapp.model.Score;
import com.quizapp.scoring.ScoreManager;
import com.quizapp.server.ConnectedClientsManager;
import java.util.*;
import java.util.concurrent.*;

/**
 * ResultBroadcaster.java - Member 5 Backend
 * Demonstrates: Non-blocking communication, Broadcasting, Thread pools
 * 
 * Broadcasts results to all clients:
 * 1. Send individual scores to each student
 * 2. Broadcast final leaderboard to all
 * 3. Optional: Use Java NIO for non-blocking communication
 */
public class ResultBroadcaster {
    private ScoreManager scoreManager;
    private ConnectedClientsManager clientsManager;
    private ExecutorService executorService;
    private boolean broadcastComplete;
    
    public ResultBroadcaster(ScoreManager scoreManager, ConnectedClientsManager clientsManager) {
        this.scoreManager = scoreManager;
        this.clientsManager = clientsManager;
        this.executorService = Executors.newFixedThreadPool(5); // Thread pool for non-blocking operations
        this.broadcastComplete = false;
    }
    
    /**
     * Broadcast all results (scores + leaderboard)
     */
    public void broadcastAllResults() {
        executorService.execute(() -> {
            try {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║    Starting Results Distribution       ║");
                System.out.println("╚════════════════════════════════════════╝\n");
                
                // First, send individual scores
                broadcastIndividualScores();
                
                // Then, broadcast the leaderboard
                broadcastLeaderboard();
                
                broadcastComplete = true;
                
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("║    Results Distribution Complete       ║");
                System.out.println("╚════════════════════════════════════════╝\n");
            } catch (Exception e) {
                System.err.println("Error during result broadcast: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * Send individual scores to each student (non-blocking)
     */
    private void broadcastIndividualScores() {
        Map<String, Score> allScores = scoreManager.getAllScores();
        CountDownLatch latch = new CountDownLatch(allScores.size());
        
        for (Score score : allScores.values()) {
            executorService.submit(() -> {
                try {
                    sendScoreToStudent(score);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await(); // Wait for all scores to be sent
            System.out.println("All individual scores sent!");
        } catch (InterruptedException e) {
            System.err.println("Interrupted while sending individual scores: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Send score to a specific student
     */
    private void sendScoreToStudent(Score score) {
        try {
            Map<String, Object> scoreMessage = new HashMap<>();
            scoreMessage.put("type", "SCORE");
            scoreMessage.put("studentId", score.getStudentId());
            scoreMessage.put("studentName", score.getStudentName());
            scoreMessage.put("obtainedMarks", score.getObtainedMarks());
            scoreMessage.put("totalMarks", score.getTotalMarks());
            scoreMessage.put("percentage", score.getPercentage());
            scoreMessage.put("rank", score.getRank());
            
            clientsManager.sendToClient(score.getStudentId(), scoreMessage);
            System.out.println("Score sent to: " + score.getStudentName());
        } catch (Exception e) {
            System.err.println("Error sending score: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast the final leaderboard to all connected clients
     */
    private void broadcastLeaderboard() {
        executorService.submit(() -> {
            try {
                List<Score> leaderboard = scoreManager.getLeaderboard();
                
                Map<String, Object> leaderboardMessage = new HashMap<>();
                leaderboardMessage.put("type", "LEADERBOARD");
                leaderboardMessage.put("timestamp", System.currentTimeMillis());
                
                List<Map<String, Object>> rankings = new ArrayList<>();
                int rank = 1;
                for (Score score : leaderboard) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("rank", rank);
                    entry.put("studentId", score.getStudentId());
                    entry.put("studentName", score.getStudentName());
                    entry.put("obtainedMarks", score.getObtainedMarks());
                    entry.put("totalMarks", score.getTotalMarks());
                    entry.put("percentage", score.getPercentage());
                    rankings.add(entry);
                    rank++;
                }
                
                leaderboardMessage.put("rankings", rankings);
                leaderboardMessage.put("statistics", scoreManager.getClassStatistics());
                
                clientsManager.broadcastToAll(leaderboardMessage);
                System.out.println("Leaderboard broadcast to all clients!");
            } catch (Exception e) {
                System.err.println("Error broadcasting leaderboard: " + e.getMessage());
            }
        });
    }
    
    /**
     * Send leaderboard update to a specific client
     */
    public void sendLeaderboardToClient(String clientId) {
        executorService.submit(() -> {
            try {
                List<Score> leaderboard = scoreManager.getLeaderboard();
                
                Map<String, Object> leaderboardMessage = new HashMap<>();
                leaderboardMessage.put("type", "LEADERBOARD");
                
                List<Map<String, Object>> rankings = new ArrayList<>();
                int rank = 1;
                for (Score score : leaderboard) {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("rank", rank);
                    entry.put("studentName", score.getStudentName());
                    entry.put("obtainedMarks", score.getObtainedMarks());
                    entry.put("totalMarks", score.getTotalMarks());
                    entry.put("percentage", score.getPercentage());
                    rankings.add(entry);
                    rank++;
                }
                
                leaderboardMessage.put("rankings", rankings);
                clientsManager.sendToClient(clientId, leaderboardMessage);
            } catch (Exception e) {
                System.err.println("Error sending leaderboard to client: " + e.getMessage());
            }
        });
    }
    
    /**
     * Check if broadcast is complete
     */
    public boolean isBroadcastComplete() {
        return broadcastComplete;
    }
    
    /**
     * Shutdown the result broadcaster
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
