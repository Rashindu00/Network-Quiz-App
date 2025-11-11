package com.quizapp.model;

import java.io.Serializable;

/**
 * Answer.java
 * Represents a student's answer to a quiz question.
 */
public class Answer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String studentId;
    private int questionId;
    private int selectedAnswerIndex;
    private boolean isCorrect;
    private long submissionTime;
    
    public Answer() {
    }
    
    public Answer(String studentId, int questionId, int selectedAnswerIndex) {
        this.studentId = studentId;
        this.questionId = questionId;
        this.selectedAnswerIndex = selectedAnswerIndex;
        this.submissionTime = System.currentTimeMillis();
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public int getSelectedAnswerIndex() {
        return selectedAnswerIndex;
    }
    
    public void setSelectedAnswerIndex(int selectedAnswerIndex) {
        this.selectedAnswerIndex = selectedAnswerIndex;
    }
    
    public boolean isCorrect() {
        return isCorrect;
    }
    
    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
    
    public long getSubmissionTime() {
        return submissionTime;
    }
    
    public void setSubmissionTime(long submissionTime) {
        this.submissionTime = submissionTime;
    }
    
    @Override
    public String toString() {
        return "Answer{" +
                "studentId='" + studentId + '\'' +
                ", questionId=" + questionId +
                ", selectedAnswerIndex=" + selectedAnswerIndex +
                ", isCorrect=" + isCorrect +
                ", submissionTime=" + submissionTime +
                '}';
    }
}
