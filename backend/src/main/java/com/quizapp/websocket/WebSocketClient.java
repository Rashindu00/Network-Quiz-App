package com.quizapp.websocket;

import org.java_websocket.WebSocket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a WebSocket client (student) in the quiz
 */
public class WebSocketClient {
    private String clientId;
    private WebSocket connection;
    private String studentName;
    private boolean registered;
    private boolean isAdmin;  // Flag to distinguish admin from students
    private Map<Integer, String> answers; // questionId -> answer
    private int score;
    private long registrationTime;
    
    public WebSocketClient(String clientId, WebSocket connection) {
        this.clientId = clientId;
        this.connection = connection;
        this.registered = false;
        this.isAdmin = false;  // Default to student
        this.answers = new ConcurrentHashMap<>();
        this.score = 0;
        this.registrationTime = System.currentTimeMillis();
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public WebSocket getConnection() {
        return connection;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public boolean isRegistered() {
        return registered;
    }
    
    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
    
    public boolean isAdmin() {
        return isAdmin;
    }
    
    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }
    
    public void submitAnswer(int questionId, String answer) {
        answers.put(questionId, answer);
    }
    
    public String getAnswer(int questionId) {
        return answers.get(questionId);
    }
    
    public Map<Integer, String> getAllAnswers() {
        return new HashMap<>(answers);
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public void addScore(int points) {
        this.score += points;
    }
    
    public long getRegistrationTime() {
        return registrationTime;
    }
    
    @Override
    public String toString() {
        return "WebSocketClient{" +
                "clientId='" + clientId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", registered=" + registered +
                ", score=" + score +
                ", answersSubmitted=" + answers.size() +
                '}';
    }
}
