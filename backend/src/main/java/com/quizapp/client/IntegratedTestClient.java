package com.quizapp.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Simple Test Client to connect to Integrated Quiz Server
 * Handles NAME protocol for registration and displays quiz questions
 */
public class IntegratedTestClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private boolean connected = false;
    private boolean nameEntered = false;

    public IntegratedTestClient() {
        scanner = new Scanner(System.in);
    }

    public void connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            System.out.println("[CONNECTED] Connected to quiz server at " + host + ":" + port);

            // Start listening to server in a separate thread
            new Thread(this::listenToServer).start();

            // Handle user input
            handleUserInput();

        } catch (IOException e) {
            System.err.println("[ERROR] Could not connect to server: " + e.getMessage());
        }
    }

    private void listenToServer() {
        try {
            String message;
            while (connected && (message = in.readLine()) != null) {
                handleServerMessage(message);
            }
        } catch (IOException e) {
            if (connected) {
                System.err.println("[ERROR] Connection to server lost: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    private void handleUserInput() {
        try {
            while (connected) {
                String input = scanner.nextLine();
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }

                // First input is the name, send with NAME| prefix
                if (!nameEntered) {
                    out.println("NAME|" + input.trim());
                    nameEntered = true;
                } else {
                    // Subsequent inputs are answers
                    out.println(input.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Input error: " + e.getMessage());
        }
    }

    private void handleServerMessage(String message) {
        if (message.startsWith("CONNECTED|")) {
            // Format: CONNECTED|clientId|prompt
            String[] parts = message.split("\\|");
            System.out.println("\n" + parts[2]);
            System.out.print("Your Name: ");
        }
        else if (message.startsWith("WELCOME|")) {
            // Format: WELCOME|name|message
            String[] parts = message.split("\\|");
            System.out.println("\n[OK] " + parts[2]);
            System.out.println("\nWaiting for quiz to start...");
        }
        else if (message.startsWith("QUIZ_START|")) {
            // Format: QUIZ_START|totalQuestions
            String[] parts = message.split("\\|");
            System.out.println("\n========================================");
            System.out.println("        QUIZ STARTING NOW!              ");
            System.out.println("========================================");
            System.out.println("Total Questions: " + parts[1]);
        }
        else if (message.startsWith("QUESTION|")) {
            // Format: QUESTION|questionId|number|text|A|B|C|D|points|timeLimit
            String[] parts = message.split("\\|");
            System.out.println("\n===================================================");
            System.out.println("Question " + parts[2] + ":");
            System.out.println(parts[3]);
            System.out.println("A) " + parts[4]);
            System.out.println("B) " + parts[5]);
            System.out.println("C) " + parts[6]);
            System.out.println("D) " + parts[7]);
            System.out.println("Points: " + parts[8] + " | Time: " + parts[9] + " seconds");
            System.out.println("===================================================");
            System.out.print("Your Answer (A/B/C/D): ");
        }
        else if (message.startsWith("RESULT|")) {
            // Format: RESULT|CORRECT/INCORRECT/TIMEOUT|points|feedback
            String[] parts = message.split("\\|", 4);
            String status = parts[1];
            String feedback = parts.length > 3 ? parts[3] : "";

            if (status.equals("CORRECT")) {
                System.out.println("\n[CORRECT] " + feedback);
            } else if (status.equals("INCORRECT")) {
                System.out.println("\n[WRONG] " + feedback);
            } else if (status.equals("TIMEOUT")) {
                System.out.println("\n[TIME UP] " + feedback);
            }
        }
        else if (message.startsWith("LEADERBOARD|")) {
            // Format: LEADERBOARD|title\nentries...
            String leaderboard = message.substring(12).replace("\\n", "\n");
            System.out.println("\n" + leaderboard);
        }
        else if (message.startsWith("FINAL_RESULTS|")) {
            // Format: FINAL_RESULTS|results...
            String results = message.substring(14).replace("\\n", "\n");
            System.out.println("\n" + results);
        }
        else if (message.startsWith("INFO|")) {
            // Format: INFO|message
            String info = message.substring(5);
            // Print INFO on a new line, then restore "Your Name: " prompt if in waiting state
            System.out.print("\r"); // Clear current line
            System.out.println("[INFO] " + info);
            // Don't reprint prompt - user will see it naturally
        }
        else if (message.startsWith("ACK|")) {
            // Acknowledgment - usually silent
            // System.out.println("[OK] " + message.substring(4));
        }
        else {
            System.out.println("\n[SERVER] " + message);
        }
    }

    private void disconnect() {
        connected = false;
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("[ERROR] Error closing connections: " + e.getMessage());
        }
        System.out.println("\n[DISCONNECTED] Connection closed");
    }

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("     INTEGRATED QUIZ CLIENT v1.0         ");
        System.out.println("==========================================");
        System.out.println();

        IntegratedTestClient client = new IntegratedTestClient();
        client.connect("localhost", 8080);
    }
}
