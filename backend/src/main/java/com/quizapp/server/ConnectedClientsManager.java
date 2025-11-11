package com.quizapp.server;

import com.quizapp.model.StudentInfo;
import java.util.*;
import java.util.concurrent.*;

/**
 * ConnectedClientsManager - Member 1 Backend
 * Demonstrates: Thread-safe collections, Synchronization
 * 
 * Manages the list of all connected clients.
 * Provides thread-safe operations for:
 * 1. Adding/removing clients
 * 2. Broadcasting messages
 * 3. Getting client information
 * 4. Tracking connection statistics
 */
public class ConnectedClientsManager {
    // Thread-safe list to store all connected clients
    private final CopyOnWriteArrayList<ClientHandler> connectedClients;
    
    // Map for quick client lookup by ID
    private final ConcurrentHashMap<String, ClientHandler> clientMap;
    
    // Map for student information
    private final ConcurrentHashMap<String, StudentInfo> studentInfoMap;
    
    // Statistics
    private int totalConnectionsEver = 0;
    
    public ConnectedClientsManager() {
        this.connectedClients = new CopyOnWriteArrayList<>();
        this.clientMap = new ConcurrentHashMap<>();
        this.studentInfoMap = new ConcurrentHashMap<>();
    }
    
    /**
     * Add a new client to the manager
     * Thread-safe method
     */
    public synchronized void addClient(ClientHandler client) {
        connectedClients.add(client);
        clientMap.put(client.getClientId(), client);
        totalConnectionsEver++;
        
        System.out.println("┌─────────────────────────────────────┐");
        System.out.println("│ Client Added to Manager             │");
        System.out.println("├─────────────────────────────────────┤");
        System.out.println("│ Student: " + client.getStudentName());
        System.out.println("│ Total Connected: " + connectedClients.size());
        System.out.println("└─────────────────────────────────────┘\n");
        
        // Notify all listeners about the update
        notifyClientListUpdated();
    }
    
    /**
     * Remove a client from the manager
     * Thread-safe method
     */
    public synchronized void removeClient(ClientHandler client) {
        boolean removed = connectedClients.remove(client);
        if (removed) {
            clientMap.remove(client.getClientId());
            
            System.out.println("┌─────────────────────────────────────┐");
            System.out.println("│ Client Removed from Manager         │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│ Student: " + client.getStudentName());
            System.out.println("│ Total Connected: " + connectedClients.size());
            System.out.println("└─────────────────────────────────────┘\n");
            
            // Notify all listeners about the update
            notifyClientListUpdated();
        }
    }
    
    /**
     * Get a client by their ID
     */
    public ClientHandler getClientById(String clientId) {
        return clientMap.get(clientId);
    }
    
    /**
     * Get a client by their student name
     */
    public ClientHandler getClientByName(String studentName) {
        for (ClientHandler client : connectedClients) {
            if (client.getStudentName().equalsIgnoreCase(studentName)) {
                return client;
            }
        }
        return null;
    }
    
    /**
     * Get the number of currently connected clients
     */
    public int getConnectedClientsCount() {
        return connectedClients.size();
    }
    
    /**
     * Get a copy of all connected clients
     */
    public List<ClientHandler> getAllClients() {
        return new ArrayList<>(connectedClients);
    }
    
    /**
     * Register a student
     */
    public void registerStudent(String studentId, String name) {
        StudentInfo studentInfo = new StudentInfo(studentId, name);
        studentInfoMap.put(studentId, studentInfo);
    }
    
    /**
     * Get student info by ID
     */
    public StudentInfo getStudentInfo(String studentId) {
        return studentInfoMap.get(studentId);
    }
    
    /**
     * Get all student information
     */
    public Map<String, StudentInfo> getAllStudentInfo() {
        return new ConcurrentHashMap<>(studentInfoMap);
    }
    
    /**
     * Get list of all student names
     */
    public List<String> getStudentNames() {
        List<String> names = new ArrayList<>();
        for (ClientHandler client : connectedClients) {
            names.add(client.getStudentName());
        }
        return names;
    }
    
    /**
     * Get detailed information about all clients
     */
    public List<Map<String, String>> getClientsInfo() {
        List<Map<String, String>> clientsInfo = new ArrayList<>();
        
        for (ClientHandler client : connectedClients) {
            Map<String, String> info = new HashMap<>();
            info.put("id", client.getClientId());
            info.put("name", client.getStudentName());
            info.put("address", client.getClientAddress());
            info.put("port", String.valueOf(client.getClientPort()));
            info.put("status", client.isConnected() ? "Connected" : "Disconnected");
            
            clientsInfo.add(info);
        }
        
        return clientsInfo;
    }
    
    /**
     * Broadcast a message to all connected clients
     * Thread-safe method
     */
    public void broadcastToAll(String message) {
        System.out.println("Broadcasting to " + connectedClients.size() + " clients: " + message);
        
        for (ClientHandler client : connectedClients) {
            if (client.isConnected()) {
                try {
                    client.sendMessage(message);
                } catch (Exception e) {
                    System.err.println("Error broadcasting to " + 
                        client.getStudentName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Broadcast an object to all connected clients
     * Thread-safe method
     */
    public void broadcastToAll(Object message) {
        System.out.println("Broadcasting object to " + connectedClients.size() + " clients");
        
        for (ClientHandler client : connectedClients) {
            if (client.isConnected()) {
                try {
                    client.sendMessage(message.toString());
                } catch (Exception e) {
                    System.err.println("Error broadcasting to " + 
                        client.getStudentName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Broadcast a message to specific clients
     */
    public void broadcastToClients(String message, List<String> clientIds) {
        for (String clientId : clientIds) {
            ClientHandler client = clientMap.get(clientId);
            if (client != null && client.isConnected()) {
                client.sendMessage(message);
            }
        }
    }
    
    /**
     * Send a message to a specific client
     */
    public void sendToClient(String clientId, String message) {
        ClientHandler client = clientMap.get(clientId);
        if (client != null && client.isConnected()) {
            client.sendMessage(message);
        } else {
            System.err.println("Client not found or disconnected: " + clientId);
        }
    }
    
    /**
     * Send an object to a specific client
     */
    public void sendToClient(String clientId, Object message) {
        ClientHandler client = clientMap.get(clientId);
        if (client != null && client.isConnected()) {
            try {
                client.sendMessage(message.toString());
            } catch (Exception e) {
                System.err.println("Error sending object to " + clientId + ": " + e.getMessage());
            }
        } else {
            System.err.println("Client not found or disconnected: " + clientId);
        }
    }
    
    /**
     * Disconnect all clients
     */
    public synchronized void disconnectAll() {
        System.out.println("Disconnecting all clients...");
        
        // Create a copy to avoid concurrent modification
        List<ClientHandler> clientsCopy = new ArrayList<>(connectedClients);
        
        for (ClientHandler client : clientsCopy) {
            try {
                client.sendMessage("SERVER_SHUTDOWN|Server is shutting down");
                client.disconnect();
            } catch (Exception e) {
                System.err.println("Error disconnecting client: " + e.getMessage());
            }
        }
        
        connectedClients.clear();
        clientMap.clear();
    }
    
    /**
     * Check if a student name is already taken
     */
    public boolean isNameTaken(String studentName) {
        for (ClientHandler client : connectedClients) {
            if (client.getStudentName().equalsIgnoreCase(studentName)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Display all connected clients
     */
    public void displayConnectedClients() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       Connected Clients List           ║");
        System.out.println("╠════════════════════════════════════════╣");
        
        if (connectedClients.isEmpty()) {
            System.out.println("║ No clients connected                   ║");
        } else {
            int i = 1;
            for (ClientHandler client : connectedClients) {
                System.out.println("║ " + i + ". " + client.getClientInfo());
                i++;
            }
        }
        
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ Total: " + connectedClients.size() + " clients");
        System.out.println("╚════════════════════════════════════════╝\n");
    }
    
    /**
     * Get statistics about connections
     */
    public Map<String, Integer> getStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("currentConnections", connectedClients.size());
        stats.put("totalConnectionsEver", totalConnectionsEver);
        return stats;
    }
    
    /**
     * Notify about client list updates
     * This can be extended to notify WebSocket connections or REST API endpoints
     */
    private void notifyClientListUpdated() {
        // This method can be used to trigger updates to the frontend
        // For now, it's a placeholder for future WebSocket implementation
        // In a full implementation, this would push updates to AdminDashboard.jsx
    }
    
    /**
     * Get a formatted list of clients for API response
     */
    public String getClientsAsJson() {
        StringBuilder json = new StringBuilder();
        json.append("[");
        
        List<ClientHandler> clients = getAllClients();
        for (int i = 0; i < clients.size(); i++) {
            ClientHandler client = clients.get(i);
            json.append("{");
            json.append("\"id\":\"").append(client.getClientId()).append("\",");
            json.append("\"name\":\"").append(client.getStudentName()).append("\",");
            json.append("\"address\":\"").append(client.getClientAddress()).append("\",");
            json.append("\"port\":").append(client.getClientPort()).append(",");
            json.append("\"connected\":").append(client.isConnected());
            json.append("}");
            
            if (i < clients.size() - 1) {
                json.append(",");
            }
        }
        
        json.append("]");
        return json.toString();
    }
}
