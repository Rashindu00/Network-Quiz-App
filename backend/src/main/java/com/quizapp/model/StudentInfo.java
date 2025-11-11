package com.quizapp.model;

import java.io.Serializable;

/**
 * StudentInfo.java
 * Represents a connected student with identification details.
 */
public class StudentInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String name;
    private long connectionTime;
    private boolean connected;
    
    public StudentInfo() {
    }
    
    public StudentInfo(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
        this.connectionTime = System.currentTimeMillis();
        this.connected = true;
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public long getConnectionTime() {
        return connectionTime;
    }
    
    public void setConnectionTime(long connectionTime) {
        this.connectionTime = connectionTime;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    public void setConnected(boolean connected) {
        this.connected = connected;
    }
    
    @Override
    public String toString() {
        return "StudentInfo{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", connectionTime=" + connectionTime +
                ", connected=" + connected +
                '}';
    }
}
