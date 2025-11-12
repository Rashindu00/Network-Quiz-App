package com.quizapp.results;

import com.quizapp.score.ScoreManager;
import com.quizapp.score.ScoreManager.ClientScore;
import com.quizapp.quiz.QuizManager;
import com.quizapp.answer.AnswerCollector;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Results Generator - Member 5
 * Compiles and formats final quiz results
 * 
 * Network Concepts:
 * - Data compilation
 * - Result formatting
 * - Broadcasting final results
 * 
 * @author Member 5
 */
public class ResultsGenerator {
    private ScoreManager scoreManager;
    private QuizManager quizManager;
    private AnswerCollector answerCollector;
    private long quizStartTime;
    private long quizEndTime;
    private String quizId;
    
    public ResultsGenerator(ScoreManager scoreManager, QuizManager quizManager, 
                           AnswerCollector answerCollector) {
        this.scoreManager = scoreManager;
        this.quizManager = quizManager;
        this.answerCollector = answerCollector;
        this.quizStartTime = System.currentTimeMillis();
        this.quizId = "QUIZ_" + quizStartTime;
    }
    
    /**
     * Mark quiz start time
     */
    public void startQuiz() {
        this.quizStartTime = System.currentTimeMillis();
        System.out.println("✓ Quiz started at: " + formatTime(quizStartTime));
    }
    
    /**
     * Mark quiz end time
     */
    public void endQuiz() {
        this.quizEndTime = System.currentTimeMillis();
        System.out.println("✓ Quiz ended at: " + formatTime(quizEndTime));
    }
    
    /**
     * Generate complete results report
     */
    public QuizResults generateResults() {
        if (quizEndTime == 0) {
            endQuiz();
        }
        
        List<ClientScore> sortedScores = scoreManager.getSortedScores();
        ClientScore winner = sortedScores.isEmpty() ? null : sortedScores.get(0);
        
        QuizResults results = new QuizResults();
        results.quizId = this.quizId;
        results.startTime = this.quizStartTime;
        results.endTime = this.quizEndTime;
        results.duration = (quizEndTime - quizStartTime) / 1000; // in seconds
        results.totalQuestions = quizManager.getTotalQuestions();
        results.totalParticipants = sortedScores.size();
        results.winner = winner;
        results.topScorers = scoreManager.getTopScorers(3);
        results.allScores = sortedScores;
        results.averageScore = calculateAverageScore(sortedScores);
        results.highestScore = winner != null ? winner.getTotalScore() : 0;
        results.lowestScore = sortedScores.isEmpty() ? 0 : 
            sortedScores.get(sortedScores.size() - 1).getTotalScore();
        
        System.out.println("✓ Results generated successfully");
        return results;
    }
    
    /**
     * Results data class
     */
    public static class QuizResults {
        public String quizId;
        public long startTime;
        public long endTime;
        public long duration; // in seconds
        public int totalQuestions;
        public int totalParticipants;
        public ClientScore winner;
        public List<ClientScore> topScorers;
        public List<ClientScore> allScores;
        public double averageScore;
        public int highestScore;
        public int lowestScore;
        
        public String getFormattedDuration() {
            long minutes = duration / 60;
            long seconds = duration % 60;
            return String.format("%d:%02d", minutes, seconds);
        }
    }
    
    /**
     * Generate winner announcement
     */
    public String generateWinnerAnnouncement() {
        ClientScore winner = scoreManager.getWinner();
        
        if (winner == null) {
            return "No participants in quiz";
        }
        
        StringBuilder announcement = new StringBuilder();
        announcement.append("\n");
        announcement.append("╔════════════════════════════════════════════════════════════╗\n");
        announcement.append("║                                                            ║\n");
        announcement.append("║            🎉  QUIZ RESULTS ANNOUNCEMENT  🎉               ║\n");
        announcement.append("║                                                            ║\n");
        announcement.append("╠════════════════════════════════════════════════════════════╣\n");
        announcement.append("║                                                            ║\n");
        announcement.append("║                    👑  WINNER  👑                          ║\n");
        announcement.append("║                                                            ║\n");
        announcement.append(String.format("║              🥇  %-37s  ║%n", winner.getClientName()));
        announcement.append(String.format("║                     SCORE: %-4d POINTS                  ║%n", 
            winner.getTotalScore()));
        announcement.append(String.format("║               ACCURACY: %.1f%% (%d/%d correct)            ║%n",
            winner.getAccuracy(), winner.getCorrectAnswers(), winner.getQuestionsAttempted()));
        announcement.append("║                                                            ║\n");
        announcement.append("╚════════════════════════════════════════════════════════════╝\n");
        
        return announcement.toString();
    }
    
    /**
     * Generate detailed results report
     */
    public String generateDetailedReport() {
        QuizResults results = generateResults();
        
        StringBuilder report = new StringBuilder();
        report.append("\n");
        report.append("╔════════════════════════════════════════════════════════════╗\n");
        report.append("║              QUIZ RESULTS - DETAILED REPORT                ║\n");
        report.append("╠════════════════════════════════════════════════════════════╣\n");
        report.append(String.format("║ Quiz ID: %-49s║%n", results.quizId));
        report.append(String.format("║ Start Time: %-46s║%n", formatTime(results.startTime)));
        report.append(String.format("║ End Time: %-48s║%n", formatTime(results.endTime)));
        report.append(String.format("║ Duration: %-48s║%n", results.getFormattedDuration()));
        report.append("╠════════════════════════════════════════════════════════════╣\n");
        report.append(String.format("║ Total Questions: %-41d║%n", results.totalQuestions));
        report.append(String.format("║ Total Participants: %-38d║%n", results.totalParticipants));
        report.append("╠════════════════════════════════════════════════════════════╣\n");
        report.append(String.format("║ Highest Score: %-43d║%n", results.highestScore));
        report.append(String.format("║ Lowest Score: %-44d║%n", results.lowestScore));
        report.append(String.format("║ Average Score: %-42.1f║%n", results.averageScore));
        report.append("╠════════════════════════════════════════════════════════════╣\n");
        report.append("║                      TOP 3 PERFORMERS                      ║\n");
        report.append("╠════════════════════════════════════════════════════════════╣\n");
        
        for (int i = 0; i < Math.min(3, results.topScorers.size()); i++) {
            ClientScore score = results.topScorers.get(i);
            String medal = i == 0 ? "🥇" : i == 1 ? "🥈" : "🥉";
            report.append(String.format("║ %s #%d %-30s %6d pts ║%n",
                medal, i + 1, truncate(score.getClientName(), 30), score.getTotalScore()));
        }
        
        report.append("╚════════════════════════════════════════════════════════════╝\n");
        
        return report.toString();
    }
    
    /**
     * Generate summary for broadcasting
     * Format: RESULTS|WinnerName|WinnerScore|TotalParticipants|Duration
     */
    public String generateBroadcastSummary() {
        QuizResults results = generateResults();
        
        String winnerName = results.winner != null ? results.winner.getClientName() : "None";
        int winnerScore = results.winner != null ? results.winner.getTotalScore() : 0;
        
        return String.format("RESULTS|%s|%d|%d|%s",
            winnerName, winnerScore, results.totalParticipants, results.getFormattedDuration());
    }
    
    /**
     * Generate personal result for specific client
     */
    public String generatePersonalResult(String clientId) {
        ClientScore score = scoreManager.getScore(clientId);
        if (score == null) {
            return "No results found for client: " + clientId;
        }
        
        int rank = scoreManager.getRank(clientId);
        int totalParticipants = scoreManager.getAllScores().size();
        
        StringBuilder personal = new StringBuilder();
        personal.append("\n");
        personal.append("╔════════════════════════════════════════════════════════════╗\n");
        personal.append("║                YOUR QUIZ RESULTS                           ║\n");
        personal.append("╠════════════════════════════════════════════════════════════╣\n");
        personal.append(String.format("║ Player: %-50s║%n", score.getClientName()));
        personal.append(String.format("║ Final Rank: #%d of %d%-37s║%n", rank, totalParticipants, ""));
        personal.append("╠════════════════════════════════════════════════════════════╣\n");
        personal.append(String.format("║ Total Score: %-45d║%n", score.getTotalScore()));
        personal.append(String.format("║ Correct Answers: %-41d║%n", score.getCorrectAnswers()));
        personal.append(String.format("║ Wrong Answers: %-43d║%n", score.getWrongAnswers()));
        personal.append(String.format("║ Accuracy: %-46.1f%%║%n", score.getAccuracy()));
        personal.append("╠════════════════════════════════════════════════════════════╣\n");
        
        // Performance message
        String message;
        if (rank == 1) {
            message = "🏆 EXCELLENT! You are the WINNER! 🏆";
        } else if (rank <= 3) {
            message = "🎖️  GREAT JOB! You're in the Top 3!";
        } else if (score.getAccuracy() >= 80) {
            message = "👍 WELL DONE! Great performance!";
        } else if (score.getAccuracy() >= 60) {
            message = "📚 GOOD EFFORT! Keep practicing!";
        } else {
            message = "💪 Keep learning and try again!";
        }
        
        personal.append(String.format("║ %s%-51s║%n", "", message));
        personal.append("╚════════════════════════════════════════════════════════════╝\n");
        
        return personal.toString();
    }
    
    /**
     * Calculate average score
     */
    private double calculateAverageScore(List<ClientScore> scores) {
        if (scores.isEmpty()) return 0.0;
        
        int total = 0;
        for (ClientScore score : scores) {
            total += score.getTotalScore();
        }
        return total / (double) scores.size();
    }
    
    /**
     * Format timestamp
     */
    private String formatTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * Truncate string
     */
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 2) + "..";
    }
    
    /**
     * Get quiz duration in minutes
     */
    public double getQuizDurationMinutes() {
        return (quizEndTime - quizStartTime) / 60000.0;
    }
    
    /**
     * Get quiz ID
     */
    public String getQuizId() {
        return quizId;
    }
}
