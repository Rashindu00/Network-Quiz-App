package com.quizapp.scoring;

import com.quizapp.model.Answer;
import com.quizapp.model.QuizQuestion;
import com.quizapp.model.Score;
import com.quizapp.question.QuestionManager;
import java.util.*;
import java.util.concurrent.*;

/**
 * AnswerEvaluator.java - Member 4 Backend
 * Demonstrates: Synchronization, Thread safety, Concurrent collections
 * 
 * Evaluates answers from clients:
 * 1. Receive answers from students
 * 2. Evaluate against correct answers
 * 3. Calculate scores (thread-safe)
 */
public class AnswerEvaluator {
    private QuestionManager questionManager;
    private ConcurrentHashMap<String, List<Answer>> studentAnswers;
    private final Object evaluationLock = new Object();
    
    public AnswerEvaluator(QuestionManager questionManager) {
        this.questionManager = questionManager;
        this.studentAnswers = new ConcurrentHashMap<>();
    }
    
    /**
     * Register a new student
     */
    public void registerStudent(String studentId) {
        studentAnswers.putIfAbsent(studentId, new CopyOnWriteArrayList<>());
    }
    
    /**
     * Submit an answer (thread-safe)
     */
    public void submitAnswer(Answer answer) {
        List<Answer> answers = studentAnswers.get(answer.getStudentId());
        
        if (answers != null) {
            synchronized (evaluationLock) {
                // Evaluate the answer
                QuizQuestion question = questionManager.getQuestionById(answer.getQuestionId());
                if (question != null) {
                    answer.setCorrect(answer.getSelectedAnswerIndex() == question.getCorrectAnswerIndex());
                }
            }
            answers.add(answer);
        }
    }
    
    /**
     * Get all answers for a student
     */
    public List<Answer> getStudentAnswers(String studentId) {
        List<Answer> answers = studentAnswers.get(studentId);
        return answers != null ? new ArrayList<>(answers) : new ArrayList<>();
    }
    
    /**
     * Evaluate all student answers and calculate scores (thread-safe)
     */
    public Map<String, Score> evaluateAllAnswers(Map<String, String> studentNames) {
        Map<String, Score> scores = new ConcurrentHashMap<>();
        
        synchronized (evaluationLock) {
            int totalMarks = questionManager.getTotalMarks();
            
            for (String studentId : studentAnswers.keySet()) {
                List<Answer> answers = getStudentAnswers(studentId);
                int obtainedMarks = 0;
                
                for (Answer answer : answers) {
                    if (answer.isCorrect()) {
                        QuizQuestion question = questionManager.getQuestionById(answer.getQuestionId());
                        if (question != null) {
                            obtainedMarks += question.getMarks();
                        }
                    }
                }
                
                String studentName = studentNames.getOrDefault(studentId, "Unknown");
                Score score = new Score(studentId, studentName, totalMarks, obtainedMarks);
                scores.put(studentId, score);
            }
        }
        
        return scores;
    }
    
    /**
     * Calculate score for a specific student (thread-safe)
     */
    public Score calculateStudentScore(String studentId, String studentName) {
        synchronized (evaluationLock) {
            List<Answer> answers = getStudentAnswers(studentId);
            int totalMarks = questionManager.getTotalMarks();
            int obtainedMarks = 0;
            
            for (Answer answer : answers) {
                if (answer.isCorrect()) {
                    QuizQuestion question = questionManager.getQuestionById(answer.getQuestionId());
                    if (question != null) {
                        obtainedMarks += question.getMarks();
                    }
                }
            }
            
            return new Score(studentId, studentName, totalMarks, obtainedMarks);
        }
    }
    
    /**
     * Get answer statistics
     */
    public Map<String, Object> getAnswerStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        synchronized (evaluationLock) {
            int totalStudents = studentAnswers.size();
            int totalAnswersSubmitted = 0;
            
            for (List<Answer> answers : studentAnswers.values()) {
                totalAnswersSubmitted += answers.size();
            }
            
            stats.put("totalStudents", totalStudents);
            stats.put("totalAnswersSubmitted", totalAnswersSubmitted);
            stats.put("expectedTotalAnswers", totalStudents * questionManager.getTotalQuestions());
            stats.put("submissionPercentage", 
                     totalStudents > 0 ? (totalAnswersSubmitted * 100.0) / (totalStudents * questionManager.getTotalQuestions()) : 0);
        }
        
        return stats;
    }
    
    /**
     * Clear all evaluation data
     */
    public synchronized void clearAllData() {
        studentAnswers.clear();
    }
}
