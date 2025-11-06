package com.quizapp.server;

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * ClientHandler - Member 1 Backend (Multithreading)
 * Demonstrates: Thread, Socket Communication, InputStream/OutputStream
 * 
 * Each instance runs in a separate thread and handles one client connection.
 * Responsibilities:
 * 1. Receive client registration (student name)
 * 2. Maintain connection with the client
 * 3. Send/receive messages from client
 * 4. Handle client disconnection
 */
public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ConnectedClientsManager clientsManager;
    private QuizServer server;
    
    private BufferedReader input;
    private PrintWriter output;
    
    private String studentName;
    private String clientId;
    private boolean connected = false;
    
    public ClientHandler(Socket socket, ConnectedClientsManager manager, QuizServer server) {
        this.clientSocket = socket;
        this.clientsManager = manager;
        this.server = server;
        this.clientId = generateClientId();
    }
    
    /**
     * Generate a unique client ID
     */
    private String generateClientId() {
        return "CLIENT_" + System.currentTimeMillis() + "_" + 
               clientSocket.getPort();
    }
    
    /**
     * Main thread execution method
     */
    @Override
    public void run() {
        try {
            // Setup input/output streams
            setupStreams();
            
            // Handle client registration
            if (registerClient()) {
                connected = true;
                
                // Add this client to the manager
                clientsManager.addClient(this);
                
                // Send welcome message
                sendMessage("WELCOME|Welcome to the Quiz, " + studentName + "!");
                
                // Listen for messages from client
                listenForMessages();
            }
            
        } catch (IOException e) {
            System.err.println("Error handling client " + studentName + ": " + e.getMessage());
        } finally {
            disconnect();
        }
    }
    
    /**
     * Setup input and output streams
     */
    private void setupStreams() throws IOException {
        input = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())
        );
        output = new PrintWriter(
            clientSocket.getOutputStream(), 
            true // auto-flush
        );
    }
    
    /**
     * Register the client by receiving their name
     */
    private boolean registerClient() {
        try {
            // Send registration request
            sendMessage("REGISTER|Please enter your name:");
            
            // Wait for client response
            String response = input.readLine();
            
            if (response != null && response.startsWith("NAME|")) {
                studentName = response.substring(5).trim();
                
                if (studentName.isEmpty()) {
                    sendMessage("ERROR|Invalid name provided");
                    return false;
                }
                
                System.out.println("✓ Student registered: " + studentName + " (ID: " + clientId + ")");
                return true;
            } else {
                sendMessage("ERROR|Invalid registration format");
                return false;
            }
            
        } catch (IOException e) {
            System.err.println("Error during client registration: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Listen for incoming messages from the client
     */
    private void listenForMessages() throws IOException {
        String message;
        
        while (connected && (message = input.readLine()) != null) {
            System.out.println("[" + studentName + "] Received: " + message);
            
            // Process the message
            processMessage(message);
        }
    }
    
    /**
     * Process messages received from client
     */
    private void processMessage(String message) {
        if (message.startsWith("PING")) {
            // Respond to keep-alive ping
            sendMessage("PONG");
            
        } else if (message.startsWith("STATUS")) {
            // Send current status
            String status = server.isQuizStarted() ? "STARTED" : "WAITING";
            sendMessage("STATUS|" + status);
            
        } else if (message.startsWith("DISCONNECT")) {
            // Client requested disconnection
            connected = false;
            
        } else {
            // Forward other messages to appropriate handler
            // (Will be handled by other team members' components)
            System.out.println("Message from " + studentName + ": " + message);
        }
    }
    
    /**
     * Send a message to the client
     */
    public void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }
    
    /**
     * Disconnect the client and cleanup resources
     */
    public void disconnect() {
        connected = false;
        
        // Remove from clients manager
        clientsManager.removeClient(this);
        
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            
            System.out.println("✗ Client disconnected: " + 
                (studentName != null ? studentName : "Unknown") + 
                " (ID: " + clientId + ")");
                
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }
    
    // Getters
    public String getStudentName() {
        return studentName;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public String getClientAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }
    
    public int getClientPort() {
        return clientSocket.getPort();
    }
    
    /**
     * Get client information as a formatted string
     */
    public String getClientInfo() {
        return String.format("Student: %s | ID: %s | Address: %s:%d | Status: %s",
            studentName != null ? studentName : "Unknown",
            clientId,
            getClientAddress(),
            getClientPort(),
            connected ? "Connected" : "Disconnected"
        );
    }
}
