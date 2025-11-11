package com.quizapp.client;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * QuizClient.java - Member 3 Backend
 * Demonstrates: Socket programming, Client-Server communication, Threading
 * 
 * Client application that connects to the quiz server:
 * 1. Connect to server
 * 2. Receive questions
 * 3. Send answers
 */
public class QuizClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String studentId;
    private String studentName;
    private boolean connected;
    private List<ClientListener> listeners;
    
    public interface ClientListener {
        void onQuestionReceived(Map<String, Object> question);
        void onScoreReceived(Map<String, Object> score);
        void onLeaderboardReceived(Map<String, Object> leaderboard);
        void onMessageReceived(String message);
        void onConnectionLost();
    }
    
    public QuizClient() {
        this.listeners = new ArrayList<>();
        this.connected = false;
    }
    
    /**
     * Connect to the quiz server
     */
    public boolean connectToServer(String serverAddress, int port, String studentId, String studentName) {
        try {
            this.socket = new Socket(serverAddress, port);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.studentId = studentId;
            this.studentName = studentName;
            this.connected = true;
            
            System.out.println("Connected to server at " + serverAddress + ":" + port);
            
            // Send registration
            Map<String, String> registration = new HashMap<>();
            registration.put("type", "REGISTER");
            registration.put("studentId", studentId);
            registration.put("studentName", studentName);
            sendMessage(registration);
            
            // Start listening for messages
            startListening();
            
            return true;
        } catch (IOException e) {
            System.err.println("Connection failed: " + e.getMessage());
            connected = false;
            return false;
        }
    }
    
    /**
     * Start listening for messages from server
     */
    private void startListening() {
        new Thread(() -> {
            try {
                while (connected) {
                    String message = in.readLine();
                    if (message != null) {
                        handleMessage(message);
                    } else {
                        connected = false;
                        notifyConnectionLost();
                        break;
                    }
                }
            } catch (IOException e) {
                if (connected) {
                    System.err.println("Error reading from server: " + e.getMessage());
                }
                connected = false;
                notifyConnectionLost();
            }
        }).start();
    }
    
    /**
     * Handle received message
     */
    private void handleMessage(String message) {
        // Parse JSON-like message
        if (message.startsWith("{")) {
            Map<String, Object> data = parseMessage(message);
            String type = (String) data.get("type");
            
            if ("QUESTION".equals(type)) {
                notifyQuestionReceived(data);
            } else if ("SCORE".equals(type)) {
                notifyScoreReceived(data);
            } else if ("LEADERBOARD".equals(type)) {
                notifyLeaderboardReceived(data);
            } else {
                notifyMessageReceived(message);
            }
        } else {
            notifyMessageReceived(message);
        }
    }
    
    /**
     * Parse simple message format
     */
    private Map<String, Object> parseMessage(String message) {
        Map<String, Object> data = new HashMap<>();
        // Simple parsing - can be enhanced with JSON library
        data.put("type", message);
        return data;
    }
    
    /**
     * Send a message to server
     */
    public void sendMessage(Map<String, String> data) {
        if (connected && out != null) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                sb.append(entry.getKey()).append(":").append(entry.getValue()).append("|");
            }
            out.println(sb.toString());
        }
    }
    
    /**
     * Submit an answer
     */
    public void submitAnswer(int questionId, int selectedOption) {
        Map<String, String> answer = new HashMap<>();
        answer.put("type", "ANSWER");
        answer.put("studentId", studentId);
        answer.put("questionId", String.valueOf(questionId));
        answer.put("selectedOption", String.valueOf(selectedOption));
        sendMessage(answer);
    }
    
    /**
     * Disconnect from server
     */
    public void disconnect() {
        connected = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    /**
     * Check if connected
     */
    public boolean isConnected() {
        return connected;
    }
    
    /**
     * Add listener
     */
    public void addListener(ClientListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Remove listener
     */
    public void removeListener(ClientListener listener) {
        listeners.remove(listener);
    }
    
    // Notification methods
    private void notifyQuestionReceived(Map<String, Object> question) {
        for (ClientListener listener : listeners) {
            listener.onQuestionReceived(question);
        }
    }
    
    private void notifyScoreReceived(Map<String, Object> score) {
        for (ClientListener listener : listeners) {
            listener.onScoreReceived(score);
        }
    }
    
    private void notifyLeaderboardReceived(Map<String, Object> leaderboard) {
        for (ClientListener listener : listeners) {
            listener.onLeaderboardReceived(leaderboard);
        }
    }
    
    private void notifyMessageReceived(String message) {
        for (ClientListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }
    
    private void notifyConnectionLost() {
        for (ClientListener listener : listeners) {
            listener.onConnectionLost();
        }
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        QuizClient client = new QuizClient();
        client.addListener(new ClientListener() {
            @Override
            public void onQuestionReceived(Map<String, Object> question) {
                System.out.println("Question received: " + question);
            }
            
            @Override
            public void onScoreReceived(Map<String, Object> score) {
                System.out.println("Score received: " + score);
            }
            
            @Override
            public void onLeaderboardReceived(Map<String, Object> leaderboard) {
                System.out.println("Leaderboard received: " + leaderboard);
            }
            
            @Override
            public void onMessageReceived(String message) {
                System.out.println("Message: " + message);
            }
            
            @Override
            public void onConnectionLost() {
                System.out.println("Connection lost!");
            }
        });
        
        if (client.connectToServer("localhost", 5000, "STU001", "John Doe")) {
            System.out.println("Connected successfully");
        } else {
            System.out.println("Failed to connect");
        }
    }
}
