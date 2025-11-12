package com.quizapp.server;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages all connected clients for the integrated quiz server
 * Thread-safe implementation
 */
public class IntegratedClientsManager {
    private final Map<String, IntegratedClientHandler> clients;
    
    public IntegratedClientsManager() {
        this.clients = new ConcurrentHashMap<>();
    }
    
    /**
     * Add a new client
     */
    public synchronized void addClient(IntegratedClientHandler client) {
        clients.put(client.getClientId(), client);
    }
    
    /**
     * Remove a client
     */
    public synchronized void removeClient(IntegratedClientHandler client) {
        clients.remove(client.getClientId());
    }
    
    /**
     * Get all connected clients
     */
    public List<IntegratedClientHandler> getAllClients() {
        return new ArrayList<>(clients.values());
    }
    
    /**
     * Get count of connected clients
     */
    public int getConnectedClientsCount() {
        return clients.size();
    }
    
    /**
     * Get count of clients with registered names (ready to play)
     */
    public int getRegisteredClientsCount() {
        int count = 0;
        for (IntegratedClientHandler client : clients.values()) {
            if (client.getStudentName() != null && !client.getStudentName().isEmpty()) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Send message to a specific client
     */
    public void sendToClient(String clientId, String message) {
        IntegratedClientHandler client = clients.get(clientId);
        if (client != null) {
            client.sendMessage(message);
        }
    }
    
    /**
     * Broadcast message to all clients
     */
    public void broadcastToAll(String message) {
        for (IntegratedClientHandler client : clients.values()) {
            client.sendMessage(message);
        }
    }
    
    /**
     * Broadcast message to all clients except sender
     */
    public void broadcastToOthers(IntegratedClientHandler sender, String message) {
        for (IntegratedClientHandler client : clients.values()) {
            if (!client.getClientId().equals(sender.getClientId())) {
                client.sendMessage(message);
            }
        }
    }
    
    /**
     * Disconnect all clients
     */
    public synchronized void disconnectAll() {
        for (IntegratedClientHandler client : clients.values()) {
            client.disconnect();
        }
        clients.clear();
    }
}
