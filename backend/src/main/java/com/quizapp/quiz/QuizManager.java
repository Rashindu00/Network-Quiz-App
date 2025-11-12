package com.quizapp.quiz;

import java.io.*;
import java.util.*;

/**
 * Quiz Manager - Member 2
 * Manages quiz questions: loading, selecting, and distributing
 * 
 * Network Concepts:
 * - File I/O for question loading
 * - Data serialization
 * - Broadcasting protocol
 * 
 * @author Member 2
 */
public class QuizManager {
    private List<Quiz> allQuestions;
    private List<Quiz> currentQuizQuestions;
    private int currentQuestionIndex;
    private boolean quizLoaded;
    private String questionsFilePath;
    
    public QuizManager() {
        this.allQuestions = new ArrayList<>();
        this.currentQuizQuestions = new ArrayList<>();
        this.currentQuestionIndex = 0;
        this.quizLoaded = false;
        this.questionsFilePath = "questions.txt";
    }
    
    public QuizManager(String questionsFilePath) {
        this();
        this.questionsFilePath = questionsFilePath;
    }
    
    /**
     * Load questions from file
     * Format: ID|Question|A|B|C|D|Answer|Points|Category
     */
    public boolean loadQuestions() {
        allQuestions.clear();
        
        // Always load default questions first
        loadDefaultQuestions();
        System.out.println("✓ Loaded " + allQuestions.size() + " default questions");
        
        try {
            // Try to load from file as well
            File file = new File(questionsFilePath);
            if (!file.exists()) {
                System.out.println("⚠ Questions file not found: " + questionsFilePath);
                quizLoaded = true;
                return true;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            int count = 0;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                Quiz question = parseQuestionLine(line);
                if (question != null) {
                    allQuestions.add(question);
                    count++;
                }
            }
            
            reader.close();
            quizLoaded = true;
            
            if (count > 0) {
                System.out.println("✓ Loaded " + count + " additional questions from file");
            }
            return true;
            
        } catch (IOException e) {
            System.err.println("✗ Error loading questions from file: " + e.getMessage());
            // Already have default questions, so return true
            quizLoaded = true;
            return true;
        }
    }
    
    /**
     * Parse a question line from file
     */
    private Quiz parseQuestionLine(String line) {
        try {
            String[] parts = line.split("\\|");
            if (parts.length >= 7) {
                String id = parts[0].trim();
                String question = parts[1].trim();
                String optionA = parts[2].trim();
                String optionB = parts[3].trim();
                String optionC = parts[4].trim();
                String optionD = parts[5].trim();
                String answer = parts[6].trim();
                
                int points = 10; // default
                String category = "General"; // default
                
                if (parts.length >= 8) {
                    points = Integer.parseInt(parts[7].trim());
                }
                if (parts.length >= 9) {
                    category = parts[8].trim();
                }
                
                return new Quiz(id, question, optionA, optionB, optionC, optionD, answer, points, category);
            }
        } catch (Exception e) {
            System.err.println("Error parsing line: " + line);
        }
        return null;
    }
    
    /**
     * Load default questions if file not found
     */
    private void loadDefaultQuestions() {
        allQuestions.add(new Quiz("Q1", 
            "What does TCP stand for?",
            "Transmission Control Protocol",
            "Transfer Control Protocol",
            "Transport Control Protocol",
            "Transition Control Protocol",
            "A", 10, "Network Basics"));
            
        allQuestions.add(new Quiz("Q2",
            "What is the default port for HTTP?",
            "80",
            "8080",
            "443",
            "8000",
            "A", 10, "Network Basics"));
            
        allQuestions.add(new Quiz("Q3",
            "Which layer of OSI model does TCP belong to?",
            "Physical Layer",
            "Data Link Layer",
            "Transport Layer",
            "Application Layer",
            "C", 15, "OSI Model"));
            
        allQuestions.add(new Quiz("Q4",
            "What does IP stand for?",
            "Internet Protocol",
            "Internal Protocol",
            "International Protocol",
            "Interconnect Protocol",
            "A", 10, "Network Basics"));
            
        allQuestions.add(new Quiz("Q5",
            "Which protocol is connectionless?",
            "TCP",
            "UDP",
            "FTP",
            "HTTP",
            "B", 15, "Protocols"));
            
        allQuestions.add(new Quiz("Q6",
            "What is the maximum size of TCP packet?",
            "32 KB",
            "64 KB",
            "128 KB",
            "256 KB",
            "B", 20, "Advanced"));
            
        allQuestions.add(new Quiz("Q7",
            "What does DNS stand for?",
            "Domain Name System",
            "Domain Network System",
            "Digital Name Service",
            "Data Network Service",
            "A", 10, "Network Services"));
            
        allQuestions.add(new Quiz("Q8",
            "Which is NOT a private IP address range?",
            "10.0.0.0 - 10.255.255.255",
            "172.16.0.0 - 172.31.255.255",
            "192.168.0.0 - 192.168.255.255",
            "200.0.0.0 - 200.255.255.255",
            "D", 15, "IP Addressing"));
            
        allQuestions.add(new Quiz("Q9",
            "What is the purpose of ARP?",
            "Resolve IP to MAC address",
            "Resolve domain to IP",
            "Route packets",
            "Encrypt data",
            "A", 15, "Network Protocols"));
            
        allQuestions.add(new Quiz("Q10",
            "What is localhost IP address?",
            "192.168.1.1",
            "127.0.0.1",
            "0.0.0.0",
            "255.255.255.255",
            "B", 10, "Network Basics"));
            
        quizLoaded = true;
        System.out.println("✓ Loaded " + allQuestions.size() + " default questions");
    }
    
    /**
     * Prepare quiz with specified number of random questions
     */
    public void prepareQuiz(int numberOfQuestions) {
        if (!quizLoaded) {
            loadQuestions();
        }
        
        currentQuizQuestions.clear();
        currentQuestionIndex = 0;
        
        if (numberOfQuestions >= allQuestions.size()) {
            currentQuizQuestions.addAll(allQuestions);
        } else {
            // Random selection
            List<Quiz> shuffled = new ArrayList<>(allQuestions);
            Collections.shuffle(shuffled);
            currentQuizQuestions.addAll(shuffled.subList(0, numberOfQuestions));
        }
        
        System.out.println("✓ Quiz prepared with " + currentQuizQuestions.size() + " questions");
    }
    
    /**
     * Prepare quiz with all questions
     */
    public void prepareQuiz() {
        prepareQuiz(allQuestions.size());
    }
    
    /**
     * Get current question
     */
    public Quiz getCurrentQuestion() {
        if (hasMoreQuestions()) {
            return currentQuizQuestions.get(currentQuestionIndex);
        }
        return null;
    }
    
    /**
     * Move to next question
     */
    public Quiz getNextQuestion() {
        if (hasMoreQuestions()) {
            return currentQuizQuestions.get(currentQuestionIndex++);
        }
        return null;
    }
    
    /**
     * Check if there are more questions
     */
    public boolean hasMoreQuestions() {
        return currentQuestionIndex < currentQuizQuestions.size();
    }
    
    /**
     * Get total number of questions in current quiz
     */
    public int getTotalQuestions() {
        return allQuestions.size();  // Return all loaded questions
    }
    
    /**
     * Get current question number (1-based)
     */
    public int getCurrentQuestionNumber() {
        return currentQuestionIndex + 1;
    }
    
    /**
     * Get question by ID
     */
    public Quiz getQuestionById(String questionId) {
        for (Quiz q : currentQuizQuestions) {
            if (q.getQuestionId().equals(questionId)) {
                return q;
            }
        }
        return null;
    }
    
    /**
     * Get all questions in current quiz
     */
    public List<Quiz> getCurrentQuizQuestions() {
        return new ArrayList<>(currentQuizQuestions);
    }
    
    /**
     * Reset quiz to start
     */
    public void resetQuiz() {
        currentQuestionIndex = 0;
    }
    
    /**
     * Get quiz statistics
     */
    public String getQuizStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("\n╔════════════════════════════════════════╗\n");
        stats.append("║         Quiz Statistics                ║\n");
        stats.append("╠════════════════════════════════════════╣\n");
        stats.append(String.format("║ Total Questions Available: %-11d║%n", allQuestions.size()));
        stats.append(String.format("║ Questions in Current Quiz: %-11d║%n", currentQuizQuestions.size()));
        stats.append(String.format("║ Current Question Number:   %-11d║%n", getCurrentQuestionNumber()));
        stats.append("╚════════════════════════════════════════╝\n");
        return stats.toString();
    }
    
    /**
     * Check if quiz is loaded and ready
     */
    public boolean isQuizReady() {
        return quizLoaded && !currentQuizQuestions.isEmpty();
    }
}
