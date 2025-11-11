package com.quizapp.question;

import com.quizapp.model.QuizQuestion;
import com.quizapp.server.ClientHandler;
import com.quizapp.server.ConnectedClientsManager;
import java.util.*;

/**
 * QuestionBroadcaster.java - Member 2 Backend
 * Demonstrates: Threading, Socket Communication, Broadcasting
 * 
 * Broadcasts questions to all connected clients sequentially
 */
public class QuestionBroadcaster {
    private QuestionManager questionManager;
    private ConnectedClientsManager clientsManager;
    private int currentQuestionIndex;
    private boolean quizInProgress;
    
    public QuestionBroadcaster(QuestionManager questionManager, 
                              ConnectedClientsManager clientsManager) {
        this.questionManager = questionManager;
        this.clientsManager = clientsManager;
        this.currentQuestionIndex = 0;
        this.quizInProgress = false;
    }
    
    /**
     * Start broadcasting questions to all clients
     */
    public void startQuiz() {
        quizInProgress = true;
        currentQuestionIndex = 0;
        
        new Thread(() -> {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║         Quiz Started                   ║");
            System.out.println("║  Total Questions: " + questionManager.getTotalQuestions());
            System.out.println("║  Total Marks: " + questionManager.getTotalMarks());
            System.out.println("╚════════════════════════════════════════╝\n");
            
            broadcastAllQuestions();
        }).start();
    }
    
    /**
     * Broadcast all questions sequentially
     */
    private void broadcastAllQuestions() {
        List<QuizQuestion> allQuestions = questionManager.getAllQuestions();
        
        for (QuizQuestion question : allQuestions) {
            broadcastQuestion(question);
            
            // Wait for students to answer (configurable time)
            try {
                Thread.sleep(30000); // 30 seconds per question
            } catch (InterruptedException e) {
                System.err.println("Question broadcast interrupted: " + e.getMessage());
                Thread.currentThread().interrupt();
                break;
            }
            
            currentQuestionIndex++;
        }
        
        quizInProgress = false;
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         All Questions Sent             ║");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    /**
     * Broadcast a single question to all clients
     */
    private void broadcastQuestion(QuizQuestion question) {
        System.out.println("Broadcasting Question " + question.getQuestionId() + 
                          " to " + clientsManager.getConnectedClientsCount() + " clients");
        
        Map<String, Object> questionData = new HashMap<>();
        questionData.put("type", "QUESTION");
        questionData.put("questionId", question.getQuestionId());
        questionData.put("questionText", question.getQuestionText());
        questionData.put("options", question.getOptions());
        questionData.put("marks", question.getMarks());
        questionData.put("totalQuestions", questionManager.getTotalQuestions());
        questionData.put("currentQuestion", currentQuestionIndex + 1);
        
        clientsManager.broadcastToAll(questionData);
    }
    
    /**
     * Broadcast a specific question to a specific client
     */
    public void sendQuestionToClient(String clientId, int questionId) {
        QuizQuestion question = questionManager.getQuestionById(questionId);
        if (question != null) {
            Map<String, Object> questionData = new HashMap<>();
            questionData.put("type", "QUESTION");
            questionData.put("questionId", question.getQuestionId());
            questionData.put("questionText", question.getQuestionText());
            questionData.put("options", question.getOptions());
            questionData.put("marks", question.getMarks());
            questionData.put("totalQuestions", questionManager.getTotalQuestions());
            questionData.put("currentQuestion", currentQuestionIndex + 1);
            
            clientsManager.sendToClient(clientId, questionData);
        }
    }
    
    /**
     * Check if quiz is in progress
     */
    public boolean isQuizInProgress() {
        return quizInProgress;
    }
    
    /**
     * Get current question index
     */
    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }
    
    /**
     * Get total questions
     */
    public int getTotalQuestions() {
        return questionManager.getTotalQuestions();
    }
}
