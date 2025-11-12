package com.quizapp.server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Simple HTTP REST API Server for Admin Dashboard
 * Runs on port 8081 (separate from Socket server on 8080)
 * 
 * Provides endpoints:
 * - GET /api/clients - Get list of connected clients
 * - POST /api/quiz/start - Start the quiz
 */
public class RestApiServer {
    private HttpServer server;
    private ConnectedClientsManager clientsManager;
    private QuizServer quizServer;
    
    public RestApiServer(ConnectedClientsManager clientsManager, QuizServer quizServer) {
        this.clientsManager = clientsManager;
        this.quizServer = quizServer;
    }
    
    public void start() throws IOException {
        // Create HTTP server on port 8081 (separate from Socket server)
        server = HttpServer.create(new InetSocketAddress(8081), 0);
        
        // Add CORS headers to allow React frontend to connect
        server.createContext("/api/clients", new ClientsHandler());
        server.createContext("/api/quiz/start", new StartQuizHandler());
        
        server.setExecutor(null); // creates a default executor
        server.start();
        
        System.out.println("✓ REST API Server started on port: 8081");
        System.out.println("  GET  http://localhost:8081/api/clients");
        System.out.println("  POST http://localhost:8081/api/quiz/start\n");
    }
    
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
    
    /**
     * Handler for GET /api/clients
     * Returns JSON list of connected clients
     */
    class ClientsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            
            // Handle OPTIONS request (CORS preflight)
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("GET".equals(exchange.getRequestMethod())) {
                // Get client list from manager
                List<ClientHandler> clients = clientsManager.getAllClients();
                
                // Build JSON response
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"clients\": [");
                
                for (int i = 0; i < clients.size(); i++) {
                    ClientHandler client = clients.get(i);
                    json.append("{");
                    json.append("\"id\": \"").append(client.getClientId()).append("\",");
                    json.append("\"name\": \"").append(client.getStudentName()).append("\",");
                    json.append("\"address\": \"").append(client.getClientAddress()).append("\",");
                    json.append("\"port\": ").append(client.getClientPort()).append(",");
                    json.append("\"connectedAt\": ").append(System.currentTimeMillis()).append(",");
                    json.append("\"connected\": true");
                    json.append("}");
                    
                    if (i < clients.size() - 1) {
                        json.append(",");
                    }
                }
                
                json.append("],");
                json.append("\"totalEver\": ").append(clients.size());
                json.append("}");
                
                String response = json.toString();
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
    
    /**
     * Handler for POST /api/quiz/start
     * Starts the quiz
     */
    class StartQuizHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            
            // Handle OPTIONS request (CORS preflight)
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(200, -1);
                return;
            }
            
            if ("POST".equals(exchange.getRequestMethod())) {
                // Start the quiz
                boolean success = quizServer.startQuiz();
                
                String response;
                if (success) {
                    response = "{\"success\": true, \"message\": \"Quiz started successfully\"}";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                } else {
                    response = "{\"success\": false, \"message\": \"Quiz already started or no clients connected\"}";
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                }
                
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}
