package com.quizapp.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket Server Adapter for Student Quiz Interface
 * Bridges WebSocket connections from React frontend to existing IntegratedQuizServer
 */
public class WebSocketQuizServer extends WebSocketServer {
    
    private Map<WebSocket, WebSocketClient> clients;
    private int clientIdCounter = 1;
    private Object quizBridge; // Reference to quiz bridge
    
    public WebSocketQuizServer(int port) {
        super(new InetSocketAddress(port));
        this.clients = new ConcurrentHashMap<>();
        
        System.out.println("WebSocket Server initialized on port " + port);
    }
    
    /**
     * Set the quiz bridge reference for admin control
     */
    public void setQuizBridge(Object bridge) {
        this.quizBridge = bridge;
    }
    
    /**
     * Get the quiz bridge
     */
    public Object getQuizBridge() {
        return this.quizBridge;
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String clientId = "WS-" + clientIdCounter++;
        WebSocketClient client = new WebSocketClient(clientId, conn);
        clients.put(conn, client);
        
        System.out.println("✅ WebSocket client connected: " + clientId);
        System.out.println("   Remote address: " + conn.getRemoteSocketAddress());
        
        // Send welcome message
        JSONObject welcome = new JSONObject();
        welcome.put("type", "WELCOME");
        welcome.put("clientId", clientId);
        welcome.put("message", "Connected to Quiz Server");
        conn.send(welcome.toString());
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        WebSocketClient client = clients.remove(conn);
        if (client != null) {
            System.out.println("❌ WebSocket client disconnected: " + client.getClientId());
            System.out.println("   Name: " + client.getStudentName());
            System.out.println("   Reason: " + reason);
        }
    }
    
    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JSONObject json = new JSONObject(message);
            String type = json.getString("type");
            WebSocketClient client = clients.get(conn);
            
            if (client == null) {
                sendError(conn, "Client not found");
                return;
            }
            
            System.out.println("📩 Message from " + client.getClientId() + ": " + type);
            
            switch (type) {
                case "REGISTER":
                    handleRegister(conn, client, json);
                    break;
                    
                case "ADMIN_CONNECT":
                    handleAdminConnect(conn, client);
                    break;
                    
                case "GET_STATUS":
                    handleGetStatus(conn, client);
                    break;
                    
                case "START_QUIZ":
                    handleAdminStartQuiz(conn, client);
                    break;
                    
                case "ANSWER":
                    handleAnswer(conn, client, json);
                    break;
                    
                case "PING":
                    handlePing(conn);
                    break;
                    
                default:
                    System.out.println("⚠ Unknown message type: " + type);
                    sendError(conn, "Unknown message type: " + type);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error processing message: " + e.getMessage());
            e.printStackTrace();
            sendError(conn, "Error processing message: " + e.getMessage());
        }
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("❌ WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
        
        if (conn != null) {
            WebSocketClient client = clients.get(conn);
            if (client != null) {
                System.err.println("   Client: " + client.getClientId());
            }
        }
    }
    
    @Override
    public void onStart() {
        System.out.println("🚀 WebSocket Server started successfully!");
        System.out.println("   Students can now connect via: ws://localhost:" + getPort() + "/quiz");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
    
    /**
     * Handle student registration
     */
    private void handleRegister(WebSocket conn, WebSocketClient client, JSONObject json) {
        String name = json.getString("name");
        client.setStudentName(name);
        client.setRegistered(true);
        
        System.out.println("📝 Student registered: " + name + " (" + client.getClientId() + ")");
        
        // Send confirmation
        JSONObject response = new JSONObject();
        response.put("type", "REGISTERED");
        response.put("name", name);
        response.put("message", "You are registered! Waiting for quiz to start...");
        conn.send(response.toString());
        
        // Notify about connected students
        int totalStudents = (int) clients.values().stream()
            .filter(WebSocketClient::isRegistered)
            .count();
        System.out.println("   Total registered students: " + totalStudents);
    }
    
    /**
     * Handle admin connection
     */
    private void handleAdminConnect(WebSocket conn, WebSocketClient client) {
        client.setAdmin(true);  // Mark as admin
        client.setStudentName("Admin");
        // Don't set registered=true for admins
        
        System.out.println("👨‍💼 Admin connected: " + client.getClientId());
        
        // Send welcome message
        JSONObject response = new JSONObject();
        response.put("type", "WELCOME");
        response.put("message", "Admin connected successfully");
        conn.send(response.toString());
        
        // Send initial status
        handleGetStatus(conn, client);
    }
    
    /**
     * Handle status request from admin
     */
    private void handleGetStatus(WebSocket conn, WebSocketClient client) {
        JSONObject response = new JSONObject();
        response.put("type", "STATUS");
        
        // Count only students (not admins)
        long studentCount = clients.values().stream()
            .filter(c -> c.isRegistered() && !c.isAdmin())
            .count();
        
        response.put("registeredCount", (int) studentCount);
        response.put("totalConnections", getConnectedCount());
        
        // Add student list (exclude admins)
        List<WebSocketClient> students = getRegisteredStudents();
        org.json.JSONArray studentsArray = new org.json.JSONArray();
        for (WebSocketClient student : students) {
            if (!student.isAdmin()) {
                org.json.JSONObject studentObj = new org.json.JSONObject();
                studentObj.put("clientId", student.getClientId());
                studentObj.put("name", student.getStudentName());
                studentObj.put("score", student.getScore());
                studentObj.put("registrationTime", student.getRegistrationTime());
                studentsArray.put(studentObj);
            }
        }
        response.put("students", studentsArray);
        
        conn.send(response.toString());
    }
    
    /**
     * Handle quiz start command from admin
     */
    private void handleAdminStartQuiz(WebSocket conn, WebSocketClient client) {
        System.out.println("👨‍💼 Admin requested quiz start");
        
        // Try to start the quiz via bridge
        if (quizBridge != null) {
            try {
                // Use reflection to call startQuiz() on the bridge
                java.lang.reflect.Method startMethod = quizBridge.getClass().getMethod("startQuiz");
                boolean started = (Boolean) startMethod.invoke(quizBridge);
                
                JSONObject response = new JSONObject();
                if (started) {
                    response.put("type", "QUIZ_STARTED");
                    response.put("message", "Quiz started successfully!");
                    System.out.println("✅ Quiz started by admin");
                } else {
                    response.put("type", "ERROR");
                    response.put("message", "Failed to start quiz. Check if students are connected.");
                }
                conn.send(response.toString());
            } catch (Exception e) {
                System.err.println("❌ Error starting quiz: " + e.getMessage());
                JSONObject response = new JSONObject();
                response.put("type", "ERROR");
                response.put("message", "Error starting quiz: " + e.getMessage());
                conn.send(response.toString());
            }
        } else {
            JSONObject response = new JSONObject();
            response.put("type", "ERROR");
            response.put("message", "Quiz bridge not initialized");
            conn.send(response.toString());
        }
    }
    
    /**
     * Handle student answer
     */
    private void handleAnswer(WebSocket conn, WebSocketClient client, JSONObject json) {
        if (!client.isRegistered()) {
            sendError(conn, "Not registered");
            return;
        }
        
        int questionId = json.getInt("questionId");
        String answer = json.getString("answer");
        
        System.out.println("📝 Answer received from " + client.getStudentName() + 
                         ": Q" + questionId + " = " + answer);
        
        // Store answer
        client.submitAnswer(questionId, answer);
        
        // Send acknowledgment
        JSONObject response = new JSONObject();
        response.put("type", "ANSWER_RECEIVED");
        response.put("questionId", questionId);
        response.put("message", "Answer recorded!");
        conn.send(response.toString());
        
        // Broadcast answer update to all admins
        broadcastToAdmins("STUDENT_ANSWERED", new JSONObject()
            .put("studentName", client.getStudentName())
            .put("questionId", questionId)
            .put("answer", answer)
            .put("timestamp", System.currentTimeMillis()));
    }
    
    /**
     * Handle ping for connection keep-alive
     */
    private void handlePing(WebSocket conn) {
        JSONObject response = new JSONObject();
        response.put("type", "PONG");
        response.put("timestamp", System.currentTimeMillis());
        conn.send(response.toString());
    }
    
    /**
     * Send error message to client
     */
    private void sendError(WebSocket conn, String message) {
        JSONObject error = new JSONObject();
        error.put("type", "ERROR");
        error.put("message", message);
        conn.send(error.toString());
    }
    
    /**
     * Broadcast quiz start to all connected students
     */
    public void broadcastQuizStart(int totalQuestions) {
        JSONObject message = new JSONObject();
        message.put("type", "QUIZ_START");
        message.put("totalQuestions", totalQuestions);
        message.put("message", "Quiz is starting! Get ready!");
        
        broadcast(message.toString());
        System.out.println("📢 Broadcasted QUIZ_START to " + clients.size() + " students");
    }
    
    /**
     * Broadcast question to all students
     */
    public void broadcastQuestion(int questionNumber, String questionText, 
                                  String[] options, int timeLimit) {
        JSONObject message = new JSONObject();
        message.put("type", "QUESTION");
        message.put("questionId", questionNumber);
        message.put("questionNumber", questionNumber);
        message.put("questionText", questionText);
        message.put("options", new JSONObject()
            .put("A", options[0])
            .put("B", options[1])
            .put("C", options[2])
            .put("D", options[3]));
        message.put("timeLimit", timeLimit);
        
        broadcast(message.toString());
        System.out.println("📢 Broadcasted Question " + questionNumber + " to " + clients.size() + " students");
    }
    
    /**
     * Send result for a specific question to a student
     */
    public void sendResult(WebSocket conn, int questionId, boolean correct, int score) {
        JSONObject message = new JSONObject();
        message.put("type", "RESULT");
        message.put("questionId", questionId);
        message.put("correct", correct);
        message.put("score", score);
        message.put("message", correct ? "Correct! ✅" : "Wrong ❌");
        
        conn.send(message.toString());
    }
    
    /**
     * Broadcast quiz end with final results
     */
    public void broadcastQuizEnd(Map<String, Object> results) {
        JSONObject message = new JSONObject();
        message.put("type", "QUIZ_END");
        message.put("results", new JSONObject(results));
        message.put("message", "Quiz completed! Thank you for participating!");
        
        broadcast(message.toString());
        System.out.println("📢 Broadcasted QUIZ_END to " + clients.size() + " students");
    }
    
    /**
     * Get all registered students
     */
    public List<WebSocketClient> getRegisteredStudents() {
        List<WebSocketClient> registered = new ArrayList<>();
        for (WebSocketClient client : clients.values()) {
            if (client.isRegistered()) {
                registered.add(client);
            }
        }
        return registered;
    }
    
    /**
     * Get count of connected students
     */
    public int getConnectedCount() {
        return clients.size();
    }
    
    /**
     * Get count of registered students
     */
    public int getRegisteredCount() {
        return (int) clients.values().stream()
            .filter(WebSocketClient::isRegistered)
            .count();
    }
    
    /**
     * Broadcast message to all admin connections only
     */
    public void broadcastToAdmins(String messageType, JSONObject data) {
        JSONObject message = new JSONObject();
        message.put("type", messageType);
        message.put("data", data);
        
        for (Map.Entry<WebSocket, WebSocketClient> entry : clients.entrySet()) {
            WebSocketClient client = entry.getValue();
            if (client.isAdmin()) {
                entry.getKey().send(message.toString());
            }
        }
    }
    
    /**
     * Send live leaderboard update to all admins
     */
    public void broadcastLeaderboardToAdmins(List<WebSocketClient> students) {
        // Sort students by score (descending)
        List<WebSocketClient> sortedStudents = new ArrayList<>(students);
        sortedStudents.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        
        // Build leaderboard array
        org.json.JSONArray leaderboard = new org.json.JSONArray();
        int rank = 1;
        for (WebSocketClient student : sortedStudents) {
            if (!student.isAdmin()) {
                org.json.JSONObject entry = new org.json.JSONObject();
                entry.put("rank", rank++);
                entry.put("name", student.getStudentName());
                entry.put("score", student.getScore());
                entry.put("answersCount", student.getAllAnswers().size());
                leaderboard.put(entry);
            }
        }
        
        JSONObject message = new JSONObject();
        message.put("type", "LEADERBOARD_UPDATE");
        message.put("leaderboard", leaderboard);
        message.put("timestamp", System.currentTimeMillis());
        
        // Send to all admins
        for (Map.Entry<WebSocket, WebSocketClient> entry : clients.entrySet()) {
            WebSocketClient client = entry.getValue();
            if (client.isAdmin()) {
                entry.getKey().send(message.toString());
            }
        }
    }
}
