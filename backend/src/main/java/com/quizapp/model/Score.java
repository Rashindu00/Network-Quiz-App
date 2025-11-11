package com.quizapp.model;

import java.io.Serializable;

/**
 * Score.java
 * Represents a student's quiz score and performance metrics.
 */
public class Score implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private String studentName;
    private int totalMarks;
    private int obtainedMarks;
    private double percentage;
    private int rank;
    private long submissionTime;
    
    public Score() {
    }
    
    public Score(String studentId, String studentName, int totalMarks, int obtainedMarks) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.totalMarks = totalMarks;
        this.obtainedMarks = obtainedMarks;
        this.percentage = (obtainedMarks * 100.0) / totalMarks;
        this.submissionTime = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public int getTotalMarks() {
        return totalMarks;
    }
    
    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }
    
    public int getObtainedMarks() {
        return obtainedMarks;
    }
    
    public void setObtainedMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
        this.percentage = (obtainedMarks * 100.0) / this.totalMarks;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    public long getSubmissionTime() {
        return submissionTime;
    }
    
    public void setSubmissionTime(long submissionTime) {
        this.submissionTime = submissionTime;
    }
    
    @Override
    public String toString() {
        return "Score{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", totalMarks=" + totalMarks +
                ", obtainedMarks=" + obtainedMarks +
                ", percentage=" + percentage +
                ", rank=" + rank +
                '}';
    }
}
