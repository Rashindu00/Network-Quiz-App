package com.quizapp.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Simple Test Client - For testing Member 1's server
 * This simulates a student connecting to the quiz server
 * 
 * Network Concepts: Socket, InputStream, OutputStream
 */
public class TestClient {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Scanner scanner;
    private boolean connected = false;
    
    public TestClient() {
        scanner = new Scanner(System.in);
    }
    
    /**
     * Connect to the quiz server
     */
    public void connect() {
        try {
            System.out.println("Connecting to Quiz Server at " + SERVER_HOST + ":" + SERVER_PORT);
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            
            // Setup streams
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            
            connected = true;
            System.out.println("✓ Connected to server successfully!\n");
            
            // Start listening for server messages in a separate thread
            Thread listenerThread = new Thread(this::listenToServer);
            listenerThread.start();
            
            // Handle user input
            handleUserInput();
            
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
    
    /**
     * Listen for messages from the server
     */
    private void listenToServer() {
        try {
            String message;
            while (connected && (message = input.readLine()) != null) {
                processServerMessage(message);
            }
        } catch (IOException e) {
            if (connected) {
                System.err.println("Connection lost: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }
    
    /**
     * Process messages received from server
     */
    private void processServerMessage(String message) {
        System.out.println("\n[SERVER] " + message);
        
        if (message.startsWith("REGISTER|")) {
            // Server is asking for registration
            String prompt = message.substring(9);
            System.out.println(prompt);
            System.out.print("Enter your name: ");
            
        } else if (message.startsWith("WELCOME|")) {
            // Successfully registered
            String welcomeMsg = message.substring(8);
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║ " + welcomeMsg);
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("\nWaiting for quiz to start...");
            System.out.println("Type 'help' for available commands.\n");
            
        } else if (message.equals("QUIZ_START")) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║        QUIZ HAS STARTED!               ║");
            System.out.println("╚════════════════════════════════════════╝\n");
            
        } else if (message.startsWith("ERROR|")) {
            String error = message.substring(6);
            System.err.println("\n❌ ERROR: " + error);
            
        } else if (message.startsWith("STATUS|")) {
            String status = message.substring(7);
            System.out.println("\nQuiz Status: " + status);
            
        } else if (message.equals("PONG")) {
            System.out.println("Server is alive (PONG received)");
            
        } else if (message.startsWith("SERVER_SHUTDOWN|")) {
            String shutdownMsg = message.substring(16);
            System.out.println("\n" + shutdownMsg);
            connected = false;
        }
        
        System.out.print("> ");
    }
    
    /**
     * Handle user input from console
     */
    private void handleUserInput() {
        System.out.println("Commands: help, ping, status, disconnect");
        System.out.print("> ");
        
        while (connected) {
            try {
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) {
                    continue;
                }
                
                if (input.equalsIgnoreCase("help")) {
                    showHelp();
                    
                } else if (input.equalsIgnoreCase("ping")) {
                    sendMessage("PING");
                    
                } else if (input.equalsIgnoreCase("status")) {
                    sendMessage("STATUS");
                    
                } else if (input.equalsIgnoreCase("disconnect") || input.equalsIgnoreCase("quit")) {
                    sendMessage("DISCONNECT");
                    connected = false;
                    break;
                    
                } else if (input.startsWith("NAME|")) {
                    // User is responding to registration
                    sendMessage(input);
                    
                } else {
                    // Check if we need to register
                    // If input doesn't contain |, assume it's a name for registration
                    sendMessage("NAME|" + input);
                }
                
            } catch (Exception e) {
                System.err.println("Error reading input: " + e.getMessage());
            }
        }
    }
    
    /**
     * Send a message to the server
     */
    private void sendMessage(String message) {
        if (output != null) {
            output.println(message);
        }
    }
    
    /**
     * Show available commands
     */
    private void showHelp() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         Available Commands             ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ help       - Show this help message    ║");
        System.out.println("║ ping       - Test connection           ║");
        System.out.println("║ status     - Get quiz status           ║");
        System.out.println("║ disconnect - Disconnect from server    ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        System.out.print("> ");
    }
    
    /**
     * Disconnect from the server
     */
    public void disconnect() {
        connected = false;
        
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null && !socket.isClosed()) socket.close();
            
            System.out.println("\n✓ Disconnected from server.");
            
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║      Quiz Client - Test Program        ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        TestClient client = new TestClient();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nShutting down client...");
            client.disconnect();
        }));
        
        client.connect();
    }
}
