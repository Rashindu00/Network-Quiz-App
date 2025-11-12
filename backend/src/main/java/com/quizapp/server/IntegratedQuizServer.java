package com.quizapp.server;

import com.quizapp.quiz.*;
import com.quizapp.answer.*;
import com.quizapp.score.*;
import com.quizapp.results.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Integrated Quiz Server - All Members
 * Demonstrates complete quiz system with all components
 * 
 * Components:
 * - Member 1: Server & Client Management
 * - Member 2: Quiz Question Management
 * - Member 3: Answer Collection & Validation
 * - Member 4: Score Management & Leaderboard
 * - Member 5: Results & Statistics
 */
public class IntegratedQuizServer {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private volatile boolean running = false;
    
    // Member 1: Client Management
    private IntegratedClientsManager clientsManager;
    
    // Member 2: Quiz Management
    private QuizManager quizManager;
    private Quiz currentQuestion;
    private int currentQuestionNumber = 0;
    
    // Member 3: Answer Processing
    private AnswerCollector answerCollector;
    private AnswerValidator answerValidator;
    
    // Member 4: Scoring
    private ScoreManager scoreManager;
    private Leaderboard leaderboard;
    
    // Member 5: Results
    private ResultsGenerator resultsGenerator;
    private QuizStatistics quizStatistics;
    
    private volatile boolean quizStarted = false;
    private volatile boolean quizEnded = false;
    
    public IntegratedQuizServer() {
        // Initialize all components
        this.clientsManager = new IntegratedClientsManager();
        this.threadPool = Executors.newCachedThreadPool();
        
        // Member 2: Initialize quiz with questions
        this.quizManager = new QuizManager("questions.txt");
        quizManager.loadQuestions();
        
        // Member 3: Initialize answer processing
        this.answerCollector = new AnswerCollector(30); // 30 seconds per question
        this.answerValidator = new AnswerValidator(answerCollector);
        
        // Member 4: Initialize scoring
        this.scoreManager = new ScoreManager();
        this.leaderboard = new Leaderboard(scoreManager, 10);
        
        // Member 5: Initialize results
        this.resultsGenerator = new ResultsGenerator(scoreManager, quizManager, answerCollector);
        this.quizStatistics = new QuizStatistics(scoreManager, quizManager, answerCollector);
    }
    
    /**
     * Start the server
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            
            printBanner();
            System.out.println("Server is listening on port: " + PORT);
            System.out.println("Waiting for students to connect...\n");
            
            // Display quiz information
            System.out.println("📚 Quiz Ready:");
            System.out.println("   - Total Questions Available: " + quizManager.getTotalQuestions());
            System.out.println("   - Time per Question: " + answerCollector.getQuestionTimeLimit() + " seconds");
            System.out.println();
            
            // Accept client connections
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    
                    // Create handler for this client
                    IntegratedClientHandler handler = new IntegratedClientHandler(clientSocket, clientsManager, this);
                    threadPool.execute(handler);
                    
                } catch (SocketException e) {
                    if (!running) {
                        break; // Server is shutting down
                    }
                }
            }
            
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Start the quiz with all connected clients
     */
    public synchronized boolean startQuiz() {
        if (quizStarted) {
            System.out.println("⚠ Quiz already started!");
            return false;
        }
        
        if (clientsManager.getConnectedClientsCount() == 0) {
            System.out.println("⚠ Cannot start quiz: No clients connected!");
            return false;
        }
        
        quizStarted = true;
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        QUIZ STARTING NOW!              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Total participants: " + clientsManager.getConnectedClientsCount());
        
        // Prepare quiz with 5 questions for demo
        quizManager.prepareQuiz(5);
        
        // Register all clients in score manager
        for (IntegratedClientHandler client : clientsManager.getAllClients()) {
            String clientName = client.getStudentName();
            if (clientName != null && !clientName.isEmpty()) {
                scoreManager.registerClient(client.getClientId(), clientName);
            } else {
                // Use client ID as fallback name
                scoreManager.registerClient(client.getClientId(), "Student-" + client.getClientId());
            }
        }
        
        // Mark quiz start time
        resultsGenerator.startQuiz();
        
        // Broadcast quiz start message
        clientsManager.broadcastToAll("QUIZ_START|" + quizManager.getTotalQuestions());
        
        // Start sending questions
        new Thread(() -> runQuiz()).start();
        
        return true;
    }
    
    /**
     * Run the quiz - send questions one by one
     */
    private void runQuiz() {
        try {
            Thread.sleep(2000); // Wait 2 seconds before first question
            
            while (quizManager.hasMoreQuestions()) {
                currentQuestion = quizManager.getNextQuestion();
                currentQuestionNumber++;
                
                System.out.println("\n📤 Sending Question " + currentQuestionNumber + "...");
                System.out.println("   " + currentQuestion.getQuestionText());
                
                // Start timer for this question
                answerCollector.startQuestionTimer();
                
                // Broadcast question to all clients with question number and time limit
                int timeLimit = answerCollector.getQuestionTimeLimit();
                String questionMsg = currentQuestion.formatForClient(currentQuestionNumber, timeLimit);
                clientsManager.broadcastToAll(questionMsg);
                
                // Wait for answers (30 seconds + 5 seconds buffer)
                Thread.sleep(35000);
                
                // Process all answers
                processAnswers();
                
                // Show current leaderboard
                showLeaderboard();
                
                // Wait before next question
                Thread.sleep(5000);
            }
            
            // Quiz completed
            endQuiz();
            
        } catch (InterruptedException e) {
            System.err.println("Quiz interrupted: " + e.getMessage());
        }
    }
    
    /**
     * Process answers for current question
     */
    private void processAnswers() {
        System.out.println("\n✓ Processing answers for Question " + currentQuestionNumber + "...");
        
        int answeredCount = 0;
        int correctCount = 0;
        
        for (IntegratedClientHandler client : clientsManager.getAllClients()) {
            String clientId = client.getClientId();
            
            if (answerCollector.hasAnswered(clientId, currentQuestion.getQuestionId())) {
                answeredCount++;
                
                // Validate answer
                AnswerValidator.ValidationResult result = 
                    answerValidator.validateAnswer(clientId, currentQuestion);
                
                if (result.isCorrect()) {
                    correctCount++;
                }
                
                // Update score
                scoreManager.updateScore(clientId, result.getPointsEarned(), result.isCorrect());
                
                // Send feedback to client
                clientsManager.sendToClient(clientId, result.formatForClient());
            } else {
                // No answer submitted
                scoreManager.updateScore(clientId, 0, false);
                clientsManager.sendToClient(clientId, "RESULT|TIMEOUT|0|Time's up!");
            }
        }
        
        System.out.println("   Answered: " + answeredCount + "/" + clientsManager.getConnectedClientsCount());
        System.out.println("   Correct: " + correctCount);
    }
    
    /**
     * Show current leaderboard
     */
    private void showLeaderboard() {
        System.out.println(leaderboard.generateLeaderboard());
        
        // Broadcast leaderboard to all clients
        String leaderboardMsg = leaderboard.getBroadcastMessage();
        clientsManager.broadcastToAll(leaderboardMsg);
    }
    
    /**
     * End the quiz and show results
     */
    private void endQuiz() {
        quizEnded = true;
        resultsGenerator.endQuiz();
        
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          QUIZ COMPLETED!               ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // Generate and display results
        System.out.println(resultsGenerator.generateWinnerAnnouncement());
        System.out.println(resultsGenerator.generateDetailedReport());
        System.out.println(leaderboard.generateTop3());
        System.out.println(quizStatistics.generateComprehensiveReport());
        
        // Broadcast results to all clients
        String resultsMsg = resultsGenerator.generateBroadcastSummary();
        clientsManager.broadcastToAll(resultsMsg);
        
        System.out.println("\n✓ Quiz statistics saved");
        System.out.println("✓ Results sent to all participants\n");
    }
    
    /**
     * Record answer from a client
     */
    public void recordClientAnswer(String clientId, String questionId, String answer) {
        if (!quizStarted || quizEnded) {
            return;
        }
        
        answerCollector.recordAnswer(clientId, questionId, answer);
    }
    
    /**
     * Stop the server
     */
    public void stop() {
        try {
            running = false;
            System.out.println("\nShutting down server...");
            
            if (quizStarted && !quizEnded) {
                System.out.println("Quiz was in progress. Generating results...");
                endQuiz();
            }
            
            // Disconnect all clients
            System.out.println("Disconnecting all clients...");
            clientsManager.disconnectAll();
            
            // Shutdown thread pool
            threadPool.shutdown();
            
            // Close server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║      Server Stopped Successfully       ║");
            System.out.println("╚════════════════════════════════════════╝\n");
            
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    
    /**
     * Print server banner
     */
    private void printBanner() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                            ║");
        System.out.println("║        INTEGRATED QUIZ SERVER - ALL MEMBERS                ║");
        System.out.println("║                                                            ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  Member 1: Server & Client Management         ✓           ║");
        System.out.println("║  Member 2: Quiz Question Management           ✓           ║");
        System.out.println("║  Member 3: Answer Collection & Validation     ✓           ║");
        System.out.println("║  Member 4: Score Management & Leaderboard     ✓           ║");
        System.out.println("║  Member 5: Results & Statistics               ✓           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");
    }
    
    // Getters
    public IntegratedClientsManager getClientsManager() { return clientsManager; }
    public boolean isQuizStarted() { return quizStarted; }
    public boolean isQuizEnded() { return quizEnded; }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        IntegratedQuizServer server = new IntegratedQuizServer();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop();
        }));
        
        // Start server
        server.start();
    }
}
