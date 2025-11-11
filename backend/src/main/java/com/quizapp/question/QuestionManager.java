package com.quizapp.question;

import com.quizapp.model.QuizQuestion;
import java.io.*;
import java.util.*;

/**
 * QuestionManager.java - Member 2 Backend
 * Demonstrates: Object serialization, File I/O, Collections
 * 
 * Manages quiz questions:
 * 1. Load questions from file
 * 2. Store questions in memory
 * 3. Retrieve questions sequentially
 */
public class QuestionManager {
    private List<QuizQuestion> questions;
    private static final String QUESTIONS_FILE = "questions.dat";
    
    public QuestionManager() {
        this.questions = new ArrayList<>();
        loadQuestions();
    }
    
    /**
     * Load questions from file or create default set
     */
    private void loadQuestions() {
        File file = new File(QUESTIONS_FILE);
        
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                questions = (List<QuizQuestion>) ois.readObject();
                System.out.println("Loaded " + questions.size() + " questions from file");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading questions: " + e.getMessage());
                createDefaultQuestions();
            }
        } else {
            createDefaultQuestions();
        }
    }
    
    /**
     * Create default questions for testing
     */
    private void createDefaultQuestions() {
        // Question 1
        List<String> options1 = Arrays.asList(
            "Transmission Control Protocol",
            "Transfer Control Process",
            "Transmission Communication Protocol",
            "Transfer Communication Process"
        );
        QuizQuestion q1 = new QuizQuestion(1, "What does TCP stand for?", options1, 0, 5);
        
        // Question 2
        List<String> options2 = Arrays.asList(
            "Simple Mail Transfer Protocol",
            "Secure Mail Transmission Protocol",
            "Simple Message Transfer Process",
            "Secure Message Transmission Process"
        );
        QuizQuestion q2 = new QuizQuestion(2, "What does SMTP stand for?", options2, 0, 5);
        
        // Question 3
        List<String> options3 = Arrays.asList(
            "File Transfer Protocol",
            "File Transmission Process",
            "Fast Transfer Protocol",
            "File Transmission Protocol"
        );
        QuizQuestion q3 = new QuizQuestion(3, "What does FTP stand for?", options3, 0, 5);
        
        // Question 4
        List<String> options4 = Arrays.asList(
            "7",
            "5",
            "9",
            "10"
        );
        QuizQuestion q4 = new QuizQuestion(4, "How many layers does the OSI model have?", options4, 0, 5);
        
        // Question 5
        List<String> options5 = Arrays.asList(
            "Physical Layer",
            "Data Link Layer",
            "Transport Layer",
            "Session Layer"
        );
        QuizQuestion q5 = new QuizQuestion(5, "Which layer is responsible for routing?", options5, 2, 5);
        
        questions.addAll(Arrays.asList(q1, q2, q3, q4, q5));
        saveQuestions();
    }
    
    /**
     * Save questions to file
     */
    public void saveQuestions() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(QUESTIONS_FILE))) {
            oos.writeObject(questions);
            System.out.println("Questions saved to file");
        } catch (IOException e) {
            System.err.println("Error saving questions: " + e.getMessage());
        }
    }
    
    /**
     * Get all questions
     */
    public List<QuizQuestion> getAllQuestions() {
        return new ArrayList<>(questions);
    }
    
    /**
     * Get question by ID
     */
    public QuizQuestion getQuestionById(int questionId) {
        for (QuizQuestion q : questions) {
            if (q.getQuestionId() == questionId) {
                return q;
            }
        }
        return null;
    }
    
    /**
     * Get total questions count
     */
    public int getTotalQuestions() {
        return questions.size();
    }
    
    /**
     * Add a new question
     */
    public void addQuestion(QuizQuestion question) {
        questions.add(question);
        saveQuestions();
    }
    
    /**
     * Get total marks
     */
    public int getTotalMarks() {
        int total = 0;
        for (QuizQuestion q : questions) {
            total += q.getMarks();
        }
        return total;
    }
}
