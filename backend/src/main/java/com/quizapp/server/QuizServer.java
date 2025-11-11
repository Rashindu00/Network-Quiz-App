package com.quizapp.server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * Main Quiz Server - Member 1 Backend
 * Demonstrates: ServerSocket, Socket, Multithreading
 * 
 * This server:
 * 1. Initializes ServerSocket on port 5000
 * 2. Accepts multiple client connections
 * 3. Creates a new thread for each client
 * 4. Maintains a list of connected clients
 */
public class QuizServer {
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private ConnectedClientsManager clientsManager;
    private ExecutorService threadPool;
    private volatile boolean running = false;
    private volatile boolean quizStarted = false;
    private RestApiServer restApiServer; // REST API for admin dashboard
    
    public QuizServer() {
        this.clientsManager = new ConnectedClientsManager();
        // Thread pool to handle multiple clients efficiently
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    /**
     * Initialize the server and start accepting connections
     */
    public void start() {
        try {
            // Start REST API Server for admin dashboard
            restApiServer = new RestApiServer(clientsManager, this);
            restApiServer.start();
            
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║   Quiz Server Started Successfully!   ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("Server is listening on port: " + PORT);
            System.out.println("Waiting for students to connect...\n");
            
            // Accept client connections in a loop
            acceptConnections();
            
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Accept incoming client connections
     * Each connection is handled in a separate thread
     */
    private void acceptConnections() {
        while (running) {
            try {
                // Wait for client connection (blocking call)
                Socket clientSocket = serverSocket.accept();
                
                // Log the connection
                String clientAddress = clientSocket.getInetAddress().getHostAddress();
                int clientPort = clientSocket.getPort();
                System.out.println("┌─────────────────────────────────────┐");
                System.out.println("│ New Connection Attempt              │");
                System.out.println("├─────────────────────────────────────┤");
                System.out.println("│ IP Address: " + clientAddress);
                System.out.println("│ Port: " + clientPort);
                System.out.println("└─────────────────────────────────────┘\n");
                
                // Create a new thread to handle this client
                ClientHandler clientHandler = new ClientHandler(
                    clientSocket, 
                    clientsManager, 
                    this
                );
                
                // Submit the client handler to the thread pool
                threadPool.execute(clientHandler);
                
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Start the quiz for all connected clients
     */
    public synchronized boolean startQuiz() {
        if (quizStarted) {
            System.out.println("Quiz has already been started!");
            return false;
        }
        
        if (clientsManager.getConnectedClientsCount() == 0) {
            System.out.println("Cannot start quiz: No clients connected!");
            return false;
        }
        
        quizStarted = true;
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        QUIZ STARTING NOW!              ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Total participants: " + clientsManager.getConnectedClientsCount());
        
        // Broadcast quiz start message to all clients
        broadcastMessage("QUIZ_START");
        return true;
    }
    
    /**
     * Broadcast a message to all connected clients
     */
    public void broadcastMessage(String message) {
        clientsManager.broadcastToAll(message);
    }
    
    /**
     * Check if quiz has started
     */
    public boolean isQuizStarted() {
        return quizStarted;
    }
    
    /**
     * Get the clients manager
     */
    public ConnectedClientsManager getClientsManager() {
        return clientsManager;
    }
    
    /**
     * Stop the server and cleanup resources
     */
    public void stop() {
        running = false;
        quizStarted = false;
        
        try {
            // Stop REST API server
            if (restApiServer != null) {
                restApiServer.stop();
            }
            
            // Close all client connections
            clientsManager.disconnectAll();
            
            // Shutdown thread pool
            threadPool.shutdown();
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
            
            // Close server socket
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║      Server Stopped Successfully       ║");
            System.out.println("╚════════════════════════════════════════╝");
            
        } catch (IOException | InterruptedException e) {
            System.err.println("Error stopping server: " + e.getMessage());
        }
    }
    
    /**
     * Display server statistics
     */
    public void displayStats() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Server Statistics              ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Port: " + PORT);
        System.out.println("║ Status: " + (running ? "Running" : "Stopped"));
        System.out.println("║ Quiz Started: " + (quizStarted ? "Yes" : "No"));
        System.out.println("║ Connected Clients: " + clientsManager.getConnectedClientsCount());
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    /**
     * Main method to start the server
     */
    public static void main(String[] args) {
        QuizServer server = new QuizServer();
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down server...");
            server.stop();
        }));
        
        // Start the server
        server.start();
    }
}
