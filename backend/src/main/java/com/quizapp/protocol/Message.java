package com.quizapp.protocol;

import java.io.Serializable;

/**
 * Message.java
 * Represents a message protocol for client-server communication.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum MessageType {
        REGISTER,           // Student registration
        QUESTION,           // Send question
        ANSWER,             // Receive answer
        START_QUIZ,         // Admin starts quiz
        QUIZ_COMPLETE,      // Quiz completion notification
        SCORE,              // Send individual score
        LEADERBOARD,        // Send leaderboard
        DISCONNECT,         // Client disconnect
        CONNECTED_STUDENTS, // List of connected students
        ERROR,              // Error message
        ACK                 // Acknowledgment
    }
    
    private MessageType type;
    private Object data;
    private String senderId;
    private long timestamp;
    
    public Message() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Message(MessageType type, Object data, String senderId) {
        this.type = type;
        this.data = data;
        this.senderId = senderId;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public MessageType getType() {
        return type;
    }
    
    public void setType(MessageType type) {
        this.type = type;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public String getSenderId() {
        return senderId;
    }
    
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", senderId='" + senderId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
