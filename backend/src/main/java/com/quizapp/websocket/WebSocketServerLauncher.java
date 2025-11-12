package com.quizapp.websocket;

import java.util.List;
import java.util.Scanner;

/**
 * WebSocket Quiz Server Launcher
 * Standalone server for student quiz interface via WebSocket
 */
public class WebSocketServerLauncher {
    
    private static final int WEBSOCKET_PORT = 8081;
    private static final String QUESTIONS_FILE = "questions.txt";
    
    public static void main(String[] args) {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║   NETWORK QUIZ SYSTEM - WEBSOCKET SERVER     ║");
        System.out.println("║        (Student Interface Backend)           ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");
        
        try {
            // Create WebSocket server
            WebSocketQuizServer wsServer = new WebSocketQuizServer(WEBSOCKET_PORT);
            
            // Create quiz bridge
            WebSocketQuizBridge quizBridge = new WebSocketQuizBridge(wsServer, QUESTIONS_FILE);
            
            // Start WebSocket server
            wsServer.start();
            System.out.println("\n✅ Server is ready!");
            System.out.println("📱 Frontend URL: http://localhost:3000");
            System.out.println("🌐 WebSocket URL: ws://localhost:" + WEBSOCKET_PORT);
            System.out.println("\n📋 Instructions:");
            System.out.println("   1. Students open frontend and enter their names");
            System.out.println("   2. Wait for all students to join");
            System.out.println("   3. Type 'start' to begin the quiz");
            System.out.println("   4. Type 'status' to see connected students");
            System.out.println("   5. Type 'exit' to shutdown server");
            
            // Command line interface
            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            
            while (running) {
                System.out.print("\n> ");
                String command = scanner.nextLine().trim().toLowerCase();
                
                switch (command) {
                    case "start":
                        handleStartCommand(quizBridge);
                        break;
                        
                    case "status":
                        handleStatusCommand(wsServer, quizBridge);
                        break;
                        
                    case "help":
                        showHelp();
                        break;
                        
                    case "exit":
                    case "quit":
                        running = false;
                        break;
                        
                    case "":
                        // Ignore empty input
                        break;
                        
                    default:
                        System.out.println("❌ Unknown command: " + command);
                        System.out.println("   Type 'help' for available commands");
                }
            }
            
            // Cleanup
            System.out.println("\n🛑 Shutting down server...");
            quizBridge.shutdown();
            try {
                wsServer.stop();
            } catch (Exception e) {
                // Ignore stop errors
            }
            scanner.close();
            
            System.out.println("✅ Server stopped successfully");
            System.out.println("👋 Thank you for using Network Quiz System!");
            
        } catch (Exception e) {
            System.err.println("\n❌ Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Handle start command
     */
    private static void handleStartCommand(WebSocketQuizBridge quizBridge) {
        if (quizBridge.isQuizStarted()) {
            System.out.println("⚠ Quiz is already running!");
            return;
        }
        
        if (quizBridge.isQuizEnded()) {
            System.out.println("⚠ Quiz has already ended!");
            return;
        }
        
        boolean started = quizBridge.startQuiz();
        if (!started) {
            System.out.println("⚠ Failed to start quiz. Check if students are connected.");
        }
    }
    
    /**
     * Handle status command
     */
    private static void handleStatusCommand(WebSocketQuizServer wsServer, WebSocketQuizBridge quizBridge) {
        System.out.println("\n📊 SERVER STATUS");
        System.out.println("════════════════════════════════════════");
        System.out.println("Connected Students: " + wsServer.getConnectedCount());
        System.out.println("Registered Students: " + wsServer.getRegisteredCount());
        System.out.println("Quiz Started: " + (quizBridge.isQuizStarted() ? "Yes" : "No"));
        System.out.println("Quiz Ended: " + (quizBridge.isQuizEnded() ? "Yes" : "No"));
        
        if (quizBridge.isQuizStarted()) {
            System.out.println("Current Question: " + quizBridge.getCurrentQuestionNumber() + 
                             "/" + quizBridge.getTotalQuestions());
        }
        
        System.out.println("════════════════════════════════════════");
        
        // Show registered students
        List<WebSocketClient> students = wsServer.getRegisteredStudents();
        if (!students.isEmpty()) {
            System.out.println("\n👥 Registered Students:");
            int index = 1;
            for (WebSocketClient student : students) {
                System.out.printf("   %d. %s (Score: %d)\n", 
                    index++, student.getStudentName(), student.getScore());
            }
        } else {
            System.out.println("\n👥 No students registered yet");
        }
    }
    
    /**
     * Show help menu
     */
    private static void showHelp() {
        System.out.println("\n📋 AVAILABLE COMMANDS:");
        System.out.println("════════════════════════════════════════");
        System.out.println("  start   - Start the quiz for all connected students");
        System.out.println("  status  - Show server status and connected students");
        System.out.println("  help    - Show this help menu");
        System.out.println("  exit    - Shutdown the server");
        System.out.println("════════════════════════════════════════");
    }
}
