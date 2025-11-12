package com.quizapp.score;

import com.quizapp.score.ScoreManager.ClientScore;
import java.util.*;

/**
 * Leaderboard - Member 4
 * Generates and formats leaderboard displays
 * 
 * Network Concepts:
 * - Real-time ranking updates
 * - Broadcast messaging
 * - Data formatting for transmission
 * 
 * @author Member 4
 */
public class Leaderboard {
    private ScoreManager scoreManager;
    private int displayLimit;
    
    public Leaderboard(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
        this.displayLimit = 10; // default: show top 10
    }
    
    public Leaderboard(ScoreManager scoreManager, int displayLimit) {
        this.scoreManager = scoreManager;
        this.displayLimit = displayLimit;
    }
    
    /**
     * Generate leaderboard display
     */
    public String generateLeaderboard() {
        List<ClientScore> sortedScores = scoreManager.getSortedScores();
        
        if (sortedScores.isEmpty()) {
            return "\n╔════════════════════════════════════════╗\n" +
                   "║        No scores available             ║\n" +
                   "╚════════════════════════════════════════╝\n";
        }
        
        StringBuilder leaderboard = new StringBuilder();
        leaderboard.append("\n");
        leaderboard.append("╔════════════════════════════════════════════════════════════╗\n");
        leaderboard.append("║                    🏆 LEADERBOARD 🏆                       ║\n");
        leaderboard.append("╠══════╦════════════════════════╦═══════╦═════════╦═════════╣\n");
        leaderboard.append("║ Rank ║ Player Name            ║ Score ║ Correct ║ Accuracy║\n");
        leaderboard.append("╠══════╬════════════════════════╬═══════╬═════════╬═════════╣\n");
        
        int limit = Math.min(displayLimit, sortedScores.size());
        for (int i = 0; i < limit; i++) {
            ClientScore score = sortedScores.get(i);
            String rankEmoji = getRankEmoji(i + 1);
            
            leaderboard.append(String.format("║ %-4s ║ %-22s ║ %5d ║   %2d/%2d  ║  %5.1f%% ║%n",
                rankEmoji,
                truncate(score.getClientName(), 22),
                score.getTotalScore(),
                score.getCorrectAnswers(),
                score.getQuestionsAttempted(),
                score.getAccuracy()
            ));
        }
        
        leaderboard.append("╚══════╩════════════════════════╩═══════╩═════════╩═════════╝\n");
        
        return leaderboard.toString();
    }
    
    /**
     * Generate compact leaderboard (for broadcast)
     * Format: LEADERBOARD|Name1:Score1|Name2:Score2|...
     */
    public String generateCompactLeaderboard() {
        List<ClientScore> sortedScores = scoreManager.getSortedScores();
        
        StringBuilder compact = new StringBuilder("LEADERBOARD");
        int limit = Math.min(displayLimit, sortedScores.size());
        
        for (int i = 0; i < limit; i++) {
            ClientScore score = sortedScores.get(i);
            compact.append("|");
            compact.append(score.getClientName());
            compact.append(":");
            compact.append(score.getTotalScore());
        }
        
        return compact.toString();
    }
    
    /**
     * Generate top 3 display
     */
    public String generateTop3() {
        List<ClientScore> top3 = scoreManager.getTopScorers(3);
        
        if (top3.isEmpty()) {
            return "No scores available";
        }
        
        StringBuilder display = new StringBuilder();
        display.append("\n");
        display.append("        ╔══════════════════════════════╗\n");
        display.append("        ║       🎖️  TOP 3  🎖️          ║\n");
        display.append("        ╚══════════════════════════════╝\n\n");
        
        // Second place (if exists)
        if (top3.size() >= 2) {
            ClientScore second = top3.get(1);
            display.append("              ┌────────────┐\n");
            display.append(String.format("              │ 🥈 #2      │%n"));
            display.append(String.format("              │ %-10s │%n", truncate(second.getClientName(), 10)));
            display.append(String.format("              │ %4d pts   │%n", second.getTotalScore()));
            display.append("              └────────────┘\n\n");
        }
        
        // First place
        if (!top3.isEmpty()) {
            ClientScore first = top3.get(0);
            display.append("        ┌──────────────────┐\n");
            display.append(String.format("        │ 🥇 WINNER! #1    │%n"));
            display.append(String.format("        │ %-16s │%n", truncate(first.getClientName(), 16)));
            display.append(String.format("        │ %6d pts       │%n", first.getTotalScore()));
            display.append("        └──────────────────┘\n\n");
        }
        
        // Third place (if exists)
        if (top3.size() >= 3) {
            ClientScore third = top3.get(2);
            display.append("              ┌────────────┐\n");
            display.append(String.format("              │ 🥉 #3      │%n"));
            display.append(String.format("              │ %-10s │%n", truncate(third.getClientName(), 10)));
            display.append(String.format("              │ %4d pts   │%n", third.getTotalScore()));
            display.append("              └────────────┘\n");
        }
        
        return display.toString();
    }
    
    /**
     * Generate mini leaderboard for specific client
     */
    public String generatePersonalRanking(String clientId) {
        ClientScore clientScore = scoreManager.getScore(clientId);
        if (clientScore == null) {
            return "Score not found";
        }
        
        int rank = scoreManager.getRank(clientId);
        List<ClientScore> allScores = scoreManager.getSortedScores();
        
        StringBuilder display = new StringBuilder();
        display.append("\n╔════════════════════════════════════════╗\n");
        display.append("║      Your Ranking & Nearby Players     ║\n");
        display.append("╠════════════════════════════════════════╣\n");
        
        // Show nearby players (1 before, current, 1 after)
        int start = Math.max(0, rank - 2);
        int end = Math.min(allScores.size(), rank + 1);
        
        for (int i = start; i < end; i++) {
            ClientScore score = allScores.get(i);
            boolean isCurrent = score.getClientId().equals(clientId);
            String marker = isCurrent ? "►" : " ";
            
            display.append(String.format("║ %s #%-2d %-20s %6d pts ║%n",
                marker,
                i + 1,
                truncate(score.getClientName(), 20),
                score.getTotalScore()
            ));
        }
        
        display.append("╚════════════════════════════════════════╝\n");
        
        return display.toString();
    }
    
    /**
     * Broadcast leaderboard update message
     */
    public String getBroadcastMessage() {
        return generateCompactLeaderboard();
    }
    
    /**
     * Get rank change message
     */
    public String getRankChangeMessage(String clientId, int oldRank, int newRank) {
        if (oldRank == newRank) {
            return String.format("You remain at rank #%d", newRank);
        } else if (newRank < oldRank) {
            int change = oldRank - newRank;
            return String.format("🎉 You moved up %d position%s! Now rank #%d",
                change, change > 1 ? "s" : "", newRank);
        } else {
            int change = newRank - oldRank;
            return String.format("You dropped %d position%s. Now rank #%d",
                change, change > 1 ? "s" : "", newRank);
        }
    }
    
    /**
     * Get emoji for rank
     */
    private String getRankEmoji(int rank) {
        switch (rank) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return "#" + rank;
        }
    }
    
    /**
     * Truncate string to specified length
     */
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 2) + "..";
    }
    
    /**
     * Set display limit
     */
    public void setDisplayLimit(int limit) {
        this.displayLimit = limit;
    }
    
    /**
     * Get display limit
     */
    public int getDisplayLimit() {
        return displayLimit;
    }
    
    /**
     * Check if client is in top N
     */
    public boolean isInTopN(String clientId, int n) {
        int rank = scoreManager.getRank(clientId);
        return rank > 0 && rank <= n;
    }
    
    /**
     * Get total participants
     */
    public int getTotalParticipants() {
        return scoreManager.getAllScores().size();
    }
}
