package com.quizapp.results;

import com.quizapp.score.ScoreManager;
import com.quizapp.score.ScoreManager.ClientScore;
import com.quizapp.quiz.QuizManager;
import com.quizapp.answer.AnswerCollector;
import java.util.*;

/**
 * Quiz Statistics - Member 5
 * Generates statistical analysis of quiz performance
 * 
 * Network Concepts:
 * - Data aggregation
 * - Statistical calculations
 * - Performance analytics
 * 
 * @author Member 5
 */
public class QuizStatistics {
    private ScoreManager scoreManager;
    private QuizManager quizManager;
    private AnswerCollector answerCollector;
    
    public QuizStatistics(ScoreManager scoreManager, QuizManager quizManager, 
                         AnswerCollector answerCollector) {
        this.scoreManager = scoreManager;
        this.quizManager = quizManager;
        this.answerCollector = answerCollector;
    }
    
    /**
     * Calculate overall statistics
     */
    public OverallStatistics calculateOverallStatistics() {
        Collection<ClientScore> allScores = scoreManager.getAllScores();
        
        if (allScores.isEmpty()) {
            return new OverallStatistics();
        }
        
        OverallStatistics stats = new OverallStatistics();
        stats.totalParticipants = allScores.size();
        stats.totalQuestions = quizManager.getTotalQuestions();
        
        int totalScore = 0;
        int totalCorrect = 0;
        int totalAttempted = 0;
        int maxScore = 0;
        int minScore = Integer.MAX_VALUE;
        
        for (ClientScore score : allScores) {
            int clientScore = score.getTotalScore();
            totalScore += clientScore;
            totalCorrect += score.getCorrectAnswers();
            totalAttempted += score.getQuestionsAttempted();
            
            maxScore = Math.max(maxScore, clientScore);
            minScore = Math.min(minScore, clientScore);
        }
        
        stats.averageScore = totalScore / (double) allScores.size();
        stats.highestScore = maxScore;
        stats.lowestScore = minScore;
        stats.averageAccuracy = totalAttempted > 0 ? 
            (totalCorrect * 100.0) / totalAttempted : 0;
        stats.totalAnswers = totalAttempted;
        stats.totalCorrectAnswers = totalCorrect;
        
        return stats;
    }
    
    /**
     * Overall statistics data class
     */
    public static class OverallStatistics {
        public int totalParticipants = 0;
        public int totalQuestions = 0;
        public double averageScore = 0.0;
        public int highestScore = 0;
        public int lowestScore = 0;
        public double averageAccuracy = 0.0;
        public int totalAnswers = 0;
        public int totalCorrectAnswers = 0;
        
        public String getFormattedReport() {
            StringBuilder report = new StringBuilder();
            report.append("\n╔════════════════════════════════════════════════════════════╗\n");
            report.append("║              OVERALL QUIZ STATISTICS                       ║\n");
            report.append("╠════════════════════════════════════════════════════════════╣\n");
            report.append(String.format("║ Total Participants: %-38d║%n", totalParticipants));
            report.append(String.format("║ Total Questions: %-41d║%n", totalQuestions));
            report.append("╠════════════════════════════════════════════════════════════╣\n");
            report.append(String.format("║ Average Score: %-42.2f║%n", averageScore));
            report.append(String.format("║ Highest Score: %-43d║%n", highestScore));
            report.append(String.format("║ Lowest Score: %-44d║%n", lowestScore));
            report.append(String.format("║ Average Accuracy: %-38.2f%%║%n", averageAccuracy));
            report.append("╠════════════════════════════════════════════════════════════╣\n");
            report.append(String.format("║ Total Answers Submitted: %-33d║%n", totalAnswers));
            report.append(String.format("║ Total Correct Answers: %-35d║%n", totalCorrectAnswers));
            report.append("╚════════════════════════════════════════════════════════════╝\n");
            return report.toString();
        }
    }
    
    /**
     * Calculate performance distribution
     */
    public PerformanceDistribution calculatePerformanceDistribution() {
        Collection<ClientScore> allScores = scoreManager.getAllScores();
        
        PerformanceDistribution dist = new PerformanceDistribution();
        
        for (ClientScore score : allScores) {
            double accuracy = score.getAccuracy();
            
            if (accuracy >= 90) {
                dist.excellent++;
            } else if (accuracy >= 70) {
                dist.good++;
            } else if (accuracy >= 50) {
                dist.average++;
            } else {
                dist.needsImprovement++;
            }
        }
        
        return dist;
    }
    
    /**
     * Performance distribution data class
     */
    public static class PerformanceDistribution {
        public int excellent = 0;      // 90-100%
        public int good = 0;            // 70-89%
        public int average = 0;         // 50-69%
        public int needsImprovement = 0;// <50%
        
        public String getFormattedReport() {
            int total = excellent + good + average + needsImprovement;
            
            StringBuilder report = new StringBuilder();
            report.append("\n╔════════════════════════════════════════════════════════════╗\n");
            report.append("║           PERFORMANCE DISTRIBUTION                         ║\n");
            report.append("╠════════════════════════════════════════════════════════════╣\n");
            report.append(String.format("║ 🟢 Excellent (90-100%%): %-13d (%5.1f%%)        ║%n", 
                excellent, getPercentage(excellent, total)));
            report.append(String.format("║ 🟡 Good (70-89%%): %-18d (%5.1f%%)        ║%n", 
                good, getPercentage(good, total)));
            report.append(String.format("║ 🟠 Average (50-69%%): %-15d (%5.1f%%)        ║%n", 
                average, getPercentage(average, total)));
            report.append(String.format("║ 🔴 Needs Improvement (<50%%): %-7d (%5.1f%%)        ║%n", 
                needsImprovement, getPercentage(needsImprovement, total)));
            report.append("╚════════════════════════════════════════════════════════════╝\n");
            return report.toString();
        }
        
        private double getPercentage(int value, int total) {
            return total > 0 ? (value * 100.0) / total : 0;
        }
    }
    
    /**
     * Calculate score ranges
     */
    public Map<String, Integer> calculateScoreRanges() {
        Collection<ClientScore> allScores = scoreManager.getAllScores();
        Map<String, Integer> ranges = new LinkedHashMap<>();
        
        ranges.put("0-20", 0);
        ranges.put("21-40", 0);
        ranges.put("41-60", 0);
        ranges.put("61-80", 0);
        ranges.put("81-100", 0);
        
        for (ClientScore score : allScores) {
            int points = score.getTotalScore();
            
            if (points <= 20) {
                ranges.put("0-20", ranges.get("0-20") + 1);
            } else if (points <= 40) {
                ranges.put("21-40", ranges.get("21-40") + 1);
            } else if (points <= 60) {
                ranges.put("41-60", ranges.get("41-60") + 1);
            } else if (points <= 80) {
                ranges.put("61-80", ranges.get("61-80") + 1);
            } else {
                ranges.put("81-100", ranges.get("81-100") + 1);
            }
        }
        
        return ranges;
    }
    
    /**
     * Get participation rate
     */
    public double getParticipationRate() {
        int registered = scoreManager.getAllScores().size();
        // Assuming all registered clients participated
        return 100.0;
    }
    
    /**
     * Get completion rate
     */
    public double getCompletionRate() {
        Collection<ClientScore> allScores = scoreManager.getAllScores();
        int totalQuestions = quizManager.getTotalQuestions();
        
        if (allScores.isEmpty() || totalQuestions == 0) {
            return 0.0;
        }
        
        int totalCompleted = 0;
        for (ClientScore score : allScores) {
            if (score.getQuestionsAttempted() == totalQuestions) {
                totalCompleted++;
            }
        }
        
        return (totalCompleted * 100.0) / allScores.size();
    }
    
    /**
     * Generate comprehensive statistics report
     */
    public String generateComprehensiveReport() {
        OverallStatistics overall = calculateOverallStatistics();
        PerformanceDistribution distribution = calculatePerformanceDistribution();
        Map<String, Integer> scoreRanges = calculateScoreRanges();
        
        StringBuilder report = new StringBuilder();
        
        // Overall statistics
        report.append(overall.getFormattedReport());
        report.append("\n");
        
        // Performance distribution
        report.append(distribution.getFormattedReport());
        report.append("\n");
        
        // Score ranges
        report.append("╔════════════════════════════════════════════════════════════╗\n");
        report.append("║              SCORE RANGE DISTRIBUTION                      ║\n");
        report.append("╠════════════════════════════════════════════════════════════╣\n");
        for (Map.Entry<String, Integer> entry : scoreRanges.entrySet()) {
            String bar = generateBar(entry.getValue(), overall.totalParticipants);
            report.append(String.format("║ %-10s: %3d │%-36s║%n",
                entry.getKey(), entry.getValue(), bar));
        }
        report.append("╚════════════════════════════════════════════════════════════╝\n");
        
        // Additional metrics
        report.append("\n╔════════════════════════════════════════════════════════════╗\n");
        report.append("║              ADDITIONAL METRICS                            ║\n");
        report.append("╠════════════════════════════════════════════════════════════╣\n");
        report.append(String.format("║ Participation Rate: %-38.1f%%║%n", getParticipationRate()));
        report.append(String.format("║ Completion Rate: %-41.1f%%║%n", getCompletionRate()));
        report.append("╚════════════════════════════════════════════════════════════╝\n");
        
        return report.toString();
    }
    
    /**
     * Generate bar for visualization
     */
    private String generateBar(int value, int max) {
        if (max == 0) return "";
        
        int barLength = (int) ((value * 30.0) / max);
        StringBuilder bar = new StringBuilder(" ");
        for (int i = 0; i < barLength; i++) {
            bar.append("█");
        }
        return bar.toString();
    }
    
    /**
     * Export statistics as CSV format
     */
    public String exportAsCSV() {
        Collection<ClientScore> allScores = scoreManager.getAllScores();
        
        StringBuilder csv = new StringBuilder();
        csv.append("Rank,Name,Score,Correct,Wrong,Total,Accuracy\n");
        
        List<ClientScore> sorted = new ArrayList<>(allScores);
        sorted.sort((a, b) -> Integer.compare(b.getTotalScore(), a.getTotalScore()));
        
        int rank = 1;
        for (ClientScore score : sorted) {
            csv.append(String.format("%d,%s,%d,%d,%d,%d,%.2f%%\n",
                rank++,
                score.getClientName(),
                score.getTotalScore(),
                score.getCorrectAnswers(),
                score.getWrongAnswers(),
                score.getQuestionsAttempted(),
                score.getAccuracy()
            ));
        }
        
        return csv.toString();
    }
}
