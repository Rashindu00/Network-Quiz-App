package com.quizapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizQuestion.java
 * Represents a single quiz question with options and correct answer.
 */
public class QuizQuestion implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int questionId;
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;
    private int marks;
    
    public QuizQuestion() {
        this.options = new ArrayList<>();
    }
    
    public QuizQuestion(int questionId, String questionText, List<String> options, 
                       int correctAnswerIndex, int marks) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.marks = marks;
    }
    
    // Getters and Setters
    public int getQuestionId() {
        return questionId;
    }
    
    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
    
    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }
    
    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }
    
    public int getMarks() {
        return marks;
    }
    
    public void setMarks(int marks) {
        this.marks = marks;
    }
    
    @Override
    public String toString() {
        return "QuizQuestion{" +
                "questionId=" + questionId +
                ", questionText='" + questionText + '\'' +
                ", options=" + options +
                ", correctAnswerIndex=" + correctAnswerIndex +
                ", marks=" + marks +
                '}';
    }
}
