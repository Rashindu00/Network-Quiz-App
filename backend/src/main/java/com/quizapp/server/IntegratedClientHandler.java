package com.quizapp.server;

import java.io.*;
import java.net.*;

/**
 * Handles communication with a single client (student)
 * Integrated with all quiz components
 */
public class IntegratedClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private IntegratedClientsManager clientsManager;
    private IntegratedQuizServer server;
    
    private String clientId;
    private String studentName;
    private volatile boolean running = true;
    
    public IntegratedClientHandler(Socket socket, IntegratedClientsManager clientsManager, IntegratedQuizServer server) {
        this.socket = socket;
        this.clientsManager = clientsManager;
        this.server = server;
        this.clientId = "CLIENT_" + System.currentTimeMillis();
    }
    
    @Override
    public void run() {
        try {
            // Setup I/O streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            
            // Register with client manager
            clientsManager.addClient(this);
            
            // Client handshake
            out.println("CONNECTED|" + clientId + "|Enter your name:");
            
            // Wait for student name
            String nameMsg = in.readLine();
            if (nameMsg != null && nameMsg.startsWith("NAME|")) {
                studentName = nameMsg.substring(5).trim();
                
                // Send welcome message
                out.println("WELCOME|" + studentName + "|Waiting for quiz to start...");
                
                System.out.println("✓ " + studentName + " joined the quiz (" + 
                    clientsManager.getRegisteredClientsCount() + " participants registered)");
                
                // Broadcast new participant
                clientsManager.broadcastToOthers(
                    this, 
                    "INFO|" + studentName + " joined the quiz"
                );
                
                // Auto-start quiz when 3+ clients registered with names
                int registeredCount = clientsManager.getRegisteredClientsCount();
                if (registeredCount >= 3 && !server.isQuizStarted()) {
                    System.out.println("\n[AUTO-START] " + registeredCount + " participants ready! Starting quiz in 5 seconds...\n");
                    new Thread(() -> {
                        try {
                            Thread.sleep(5000);
                            if (!server.isQuizStarted()) {
                                server.startQuiz();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
            
            // Handle client messages
            String message;
            while (running && (message = in.readLine()) != null) {
                handleMessage(message);
            }
            
        } catch (IOException e) {
            System.err.println("Client error (" + studentName + "): " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    /**
     * Handle incoming message from client
     */
    private void handleMessage(String message) {
        if (message.startsWith("ANSWER|")) {
            // Format: ANSWER|questionId|answer
            String[] parts = message.split("\\|");
            if (parts.length >= 3) {
                String questionId = parts[1];
                String answer = parts[2];
                
                // Record answer in the server
                server.recordClientAnswer(clientId, questionId, answer);
                
                // Send acknowledgment
                out.println("ACK|Answer recorded");
            }
        }
        else if (message.equals("PING")) {
            out.println("PONG");
        }
        else if (message.equals("STATUS")) {
            if (server.isQuizEnded()) {
                out.println("STATUS|ENDED");
            } else if (server.isQuizStarted()) {
                out.println("STATUS|IN_PROGRESS");
            } else {
                out.println("STATUS|WAITING");
            }
        }
    }
    
    /**
     * Send message to this client
     */
    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
    
    /**
     * Disconnect this client
     */
    public void disconnect() {
        try {
            running = false;
            
            if (studentName != null) {
                System.out.println("✗ " + studentName + " left the quiz");
                clientsManager.broadcastToOthers(
                    this,
                    "INFO|" + studentName + " left the quiz"
                );
            }
            
            clientsManager.removeClient(this);
            
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
        } catch (IOException e) {
            System.err.println("Error disconnecting client: " + e.getMessage());
        }
    }
    
    // Getters
    public String getClientId() { return clientId; }
    public String getStudentName() { return studentName; }
    public boolean isConnected() { return running && !socket.isClosed(); }
}
