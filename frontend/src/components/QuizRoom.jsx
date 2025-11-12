import React, { useState, useEffect } from 'react';
import './QuizRoom.css';

/**
 * Quiz Room - Students answer questions in real-time
 */
const QuizRoom = ({ 
  studentName, 
  currentQuestion, 
  questionNumber,
  totalQuestions,
  timeRemaining,
  onAnswerSubmit,
  waitingForQuiz,
  quizEnded,
  currentScore
}) => {
  const [selectedAnswer, setSelectedAnswer] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [lastResult, setLastResult] = useState(null);

  // Reset selection when new question arrives
  useEffect(() => {
    setSelectedAnswer('');
    setIsSubmitting(false);
    setLastResult(null);
  }, [currentQuestion]);

  const handleAnswerSelect = (option) => {
    if (!isSubmitting && timeRemaining > 0) {
      setSelectedAnswer(option);
    }
  };

  const handleSubmit = () => {
    if (selectedAnswer && !isSubmitting) {
      setIsSubmitting(true);
      onAnswerSubmit(selectedAnswer);
    }
  };

  const getTimeColor = () => {
    if (timeRemaining > 20) return '#4caf50';
    if (timeRemaining > 10) return '#ff9800';
    return '#f44336';
  };

  const getProgressPercentage = () => {
    return ((questionNumber - 1) / totalQuestions) * 100;
  };

  // Waiting for quiz to start
  if (waitingForQuiz) {
    return (
      <div className="quiz-room waiting">
        <div className="waiting-container">
          <div className="waiting-icon">⏳</div>
          <h2>Waiting for Quiz to Start...</h2>
          <p>Welcome, <strong>{studentName}</strong>!</p>
          <p className="waiting-message">
            Quiz will begin when 3 or more students join.
          </p>
          <div className="waiting-spinner">
            <div className="spinner-dot"></div>
            <div className="spinner-dot"></div>
            <div className="spinner-dot"></div>
          </div>
        </div>
      </div>
    );
  }

  // Quiz ended
  if (quizEnded) {
    return (
      <div className="quiz-room ended">
        <div className="ended-container">
          <div className="ended-icon">🎉</div>
          <h2>Quiz Completed!</h2>
          <p>Great job, <strong>{studentName}</strong>!</p>
          <div className="final-score">
            <div className="score-label">Your Final Score</div>
            <div className="score-value">{currentScore}</div>
            <div className="score-subtext">points</div>
          </div>
          <p className="results-message">
            Waiting for final results and leaderboard...
          </p>
        </div>
      </div>
    );
  }

  // Active quiz
  return (
    <div className="quiz-room active">
      {/* Header */}
      <div className="quiz-header">
        <div className="student-info">
          <span className="student-icon">👤</span>
          <span className="student-name">{studentName}</span>
        </div>
        <div className="score-display">
          <span className="score-label">Score:</span>
          <span className="score-value">{currentScore}</span>
        </div>
      </div>

      {/* Progress Bar */}
      <div className="quiz-progress">
        <div className="progress-info">
          <span>Question {questionNumber} of {totalQuestions}</span>
          <span className="progress-percentage">
            {Math.round(getProgressPercentage())}%
          </span>
        </div>
        <div className="progress-bar">
          <div 
            className="progress-fill"
            style={{ width: `${getProgressPercentage()}%` }}
          ></div>
        </div>
      </div>

      {/* Timer */}
      <div className="quiz-timer" style={{ '--timer-color': getTimeColor() }}>
        <div className="timer-icon">⏱️</div>
        <div className="timer-value">{timeRemaining}</div>
        <div className="timer-label">seconds</div>
        <div className="timer-circle">
          <svg width="100" height="100">
            <circle
              cx="50"
              cy="50"
              r="45"
              fill="none"
              stroke="var(--timer-color)"
              strokeWidth="8"
              strokeDasharray={`${(timeRemaining / 30) * 283} 283`}
              transform="rotate(-90 50 50)"
            />
          </svg>
        </div>
      </div>

      {/* Question Card */}
      {currentQuestion && (
        <div className="question-card">
          <div className="question-header">
            <span className="question-badge">Question {questionNumber}</span>
            <span className="points-badge">
              {currentQuestion.points || 10} points
            </span>
          </div>
          
          <div className="question-text">
            {currentQuestion.text}
          </div>

          {/* Answer Options */}
          <div className="answer-options">
            {['A', 'B', 'C', 'D'].map((option) => (
              <button
                key={option}
                className={`answer-option ${selectedAnswer === option ? 'selected' : ''}`}
                onClick={() => handleAnswerSelect(option)}
                disabled={isSubmitting || timeRemaining === 0}
              >
                <span className="option-letter">{option}</span>
                <span className="option-text">
                  {currentQuestion[`option${option}`]}
                </span>
                {selectedAnswer === option && (
                  <span className="check-icon">✓</span>
                )}
              </button>
            ))}
          </div>

          {/* Submit Button */}
          <button
            className="submit-button"
            onClick={handleSubmit}
            disabled={!selectedAnswer || isSubmitting || timeRemaining === 0}
          >
            {isSubmitting ? (
              <>
                <span className="spinner"></span>
                Submitting...
              </>
            ) : timeRemaining === 0 ? (
              'Time\'s Up!'
            ) : !selectedAnswer ? (
              'Select an answer'
            ) : (
              <>
                Submit Answer
                <span className="submit-arrow">→</span>
              </>
            )}
          </button>
        </div>
      )}

      {/* Last Result Feedback */}
      {lastResult && (
        <div className={`result-feedback ${lastResult.correct ? 'correct' : 'incorrect'}`}>
          <span className="result-icon">
            {lastResult.correct ? '✅' : '❌'}
          </span>
          <span className="result-text">{lastResult.message}</span>
        </div>
      )}
    </div>
  );
};

export default QuizRoom;
