package com.quizapp.websocket;

import com.quizapp.quiz.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Bridge between WebSocket server and IntegratedQuizServer
 * Manages quiz flow for WebSocket clients
 */
public class WebSocketQuizBridge {
    
    private WebSocketQuizServer wsServer;
    private QuizManager quizManager;
    private ScheduledExecutorService scheduler;
    
    private Quiz currentQuestion;
    private int currentQuestionNumber = 0;
    private int totalQuestions = 5;
    private int questionTimeLimit = 30; // seconds
    
    private volatile boolean quizStarted = false;
    private volatile boolean quizEnded = false;
    
    public WebSocketQuizBridge(WebSocketQuizServer wsServer, String questionsFile) {
        this.wsServer = wsServer;
        this.quizManager = new QuizManager(questionsFile);
        this.quizManager.loadQuestions();
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        // Register this bridge with the server so admin can trigger quiz
        wsServer.setQuizBridge(this);
        
        System.out.println("🔗 WebSocket Quiz Bridge initialized");
        System.out.println("   Questions loaded: " + quizManager.getTotalQuestions());
    }
    
    /**
     * Start the quiz for all connected WebSocket students
     */
    public synchronized boolean startQuiz() {
        if (quizStarted) {
            System.out.println("⚠ Quiz already started!");
            System.out.println("   Resetting quiz to start fresh...");
            resetQuiz();
        }
        
        int studentCount = wsServer.getRegisteredCount();
        if (studentCount == 0) {
            System.out.println("⚠ Cannot start quiz: No students registered!");
            return false;
        }
        
        quizStarted = true;
        currentQuestionNumber = 0;
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    WEBSOCKET QUIZ STARTING NOW!        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Total participants: " + studentCount);
        
        // Prepare quiz
        quizManager.prepareQuiz(totalQuestions);
        
        // Broadcast quiz start
        wsServer.broadcastQuizStart(totalQuestions);
        
        // Start sending questions after 3 seconds
        scheduler.schedule(() -> sendNextQuestion(), 3, TimeUnit.SECONDS);
        
        return true;
    }
    
    /**
     * Send the next question to all students
     */
    private void sendNextQuestion() {
        if (!quizManager.hasMoreQuestions()) {
            endQuiz();
            return;
        }
        
        currentQuestion = quizManager.getNextQuestion();
        currentQuestionNumber++;
        
        System.out.println("\n📤 Sending Question " + currentQuestionNumber + "...");
        System.out.println("   " + currentQuestion.getQuestionText());
        
        // Extract question details
        String questionText = currentQuestion.getQuestionText();
        String[] options = currentQuestion.getOptions();
        
        // Broadcast to all students
        wsServer.broadcastQuestion(
            currentQuestionNumber,
            questionText,
            options,
            questionTimeLimit
        );
        
        // Schedule next question after time limit + 5 seconds buffer
        int delay = questionTimeLimit + 5;
        scheduler.schedule(() -> processAnswersAndContinue(), delay, TimeUnit.SECONDS);
    }
    
    /**
     * Process answers and send next question
     */
    private void processAnswersAndContinue() {
        System.out.println("\n⏰ Time's up for Question " + currentQuestionNumber);
        
        // Process answers for current question
        String correctAnswer = currentQuestion.getCorrectAnswer();
        int pointsPerQuestion = 10;
        
        List<WebSocketClient> students = wsServer.getRegisteredStudents();
        int correctCount = 0;
        
        for (WebSocketClient student : students) {
            String answer = student.getAnswer(currentQuestionNumber);
            
            if (answer != null && answer.equals(correctAnswer)) {
                // Correct answer
                student.addScore(pointsPerQuestion);
                wsServer.sendResult(student.getConnection(), currentQuestionNumber, true, student.getScore());
                correctCount++;
            } else {
                // Wrong or no answer
                wsServer.sendResult(student.getConnection(), currentQuestionNumber, false, student.getScore());
            }
        }
        
        System.out.println("   Correct answers: " + correctCount + "/" + students.size());
        System.out.println("   Correct answer was: " + correctAnswer);
        
        // Show current leaderboard
        showLeaderboard();
        
        // Send leaderboard update to admins
        wsServer.broadcastLeaderboardToAdmins(students);
        
        // Wait 5 seconds before next question
        scheduler.schedule(() -> sendNextQuestion(), 5, TimeUnit.SECONDS);
    }
    
    /**
     * End the quiz and send final results
     */
    private void endQuiz() {
        if (quizEnded) {
            return;
        }
        
        quizEnded = true;
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         QUIZ COMPLETED!                ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // Generate final results
        List<WebSocketClient> students = wsServer.getRegisteredStudents();
        
        // Sort by score (descending)
        students.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        // Create results map
        Map<String, Object> results = new HashMap<>();
        results.put("totalQuestions", totalQuestions);
        
        List<Map<String, Object>> rankings = new ArrayList<>();
        int rank = 1;
        for (WebSocketClient student : students) {
            Map<String, Object> studentResult = new HashMap<>();
            studentResult.put("rank", rank++);
            studentResult.put("name", student.getStudentName());
            studentResult.put("score", student.getScore());
            studentResult.put("correctAnswers", student.getScore() / 10); // 10 points per question
            rankings.add(studentResult);
        }
        results.put("rankings", rankings);
        
        // Broadcast final results
        wsServer.broadcastQuizEnd(results);
        
        // Show final leaderboard
        System.out.println("\n🏆 FINAL LEADERBOARD:");
        System.out.println("┌─────┬──────────────────────┬───────┬──────────┐");
        System.out.println("│ Rank│ Name                 │ Score │ Correct  │");
        System.out.println("├─────┼──────────────────────┼───────┼──────────┤");
        
        rank = 1;
        for (WebSocketClient student : students) {
            String name = student.getStudentName();
            int score = student.getScore();
            int correct = score / 10;
            
            System.out.printf("│  %2d │ %-20s │  %3d  │   %2d/%2d  │\n",
                rank++, truncate(name, 20), score, correct, totalQuestions);
        }
        System.out.println("└─────┴──────────────────────┴───────┴──────────┘");
        
        System.out.println("\n✨ Thank you for participating!");
    }
    
    /**
     * Show current leaderboard
     */
    private void showLeaderboard() {
        List<WebSocketClient> students = wsServer.getRegisteredStudents();
        students.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        System.out.println("\n📊 Current Leaderboard:");
        System.out.println("┌─────┬──────────────────────┬───────┐");
        System.out.println("│ Rank│ Name                 │ Score │");
        System.out.println("├─────┼──────────────────────┼───────┤");
        
        int rank = 1;
        for (int i = 0; i < Math.min(5, students.size()); i++) {
            WebSocketClient student = students.get(i);
            String name = student.getStudentName();
            int score = student.getScore();
            
            System.out.printf("│  %2d │ %-20s │  %3d  │\n",
                rank++, truncate(name, 20), score);
        }
        System.out.println("└─────┴──────────────────────┴───────┘");
    }
    
    /**
     * Truncate string to max length
     */
    private String truncate(String str, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * Get quiz status
     */
    public boolean isQuizStarted() {
        return quizStarted;
    }
    
    public boolean isQuizEnded() {
        return quizEnded;
    }
    
    public int getCurrentQuestionNumber() {
        return currentQuestionNumber;
    }
    
    public int getTotalQuestions() {
        return totalQuestions;
    }
    
    /**
     * Reset quiz to allow starting again
     */
    public synchronized void resetQuiz() {
        System.out.println("\n🔄 Resetting quiz...");
        
        // Reset flags
        quizStarted = false;
        quizEnded = false;
        currentQuestionNumber = 0;
        
        // Cancel any pending tasks
        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(2);
        
        // Reset all student scores
        List<WebSocketClient> students = wsServer.getRegisteredStudents();
        for (WebSocketClient student : students) {
            student.setScore(0);
            student.getAllAnswers().clear();
        }
        
        // Reload questions
        quizManager.loadQuestions();
        
        System.out.println("✅ Quiz reset complete! Ready to start fresh.");
    }
    
    /**
     * Shutdown the bridge
     */
    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }
}
