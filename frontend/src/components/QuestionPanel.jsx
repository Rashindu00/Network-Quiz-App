import React, { useState, useEffect } from 'react';
import './QuestionPanel.css';

/**
 * QuestionPanel.jsx - Member 2 Frontend
 * Displays quiz questions with multiple choice options
 * Allows students to submit answers
 * 
 * Networking Concepts:
 * - Receiving serialized question objects
 * - Sending answer submissions
 * - Real-time communication
 */
const QuestionPanel = ({ socket, studentId }) => {
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [selectedOption, setSelectedOption] = useState(null);
  const [questionNumber, setQuestionNumber] = useState(0);
  const [totalQuestions, setTotalQuestions] = useState(0);
  const [timeRemaining, setTimeRemaining] = useState(30);
  const [answeredQuestions, setAnsweredQuestions] = useState(new Set());
  const [submitted, setSubmitted] = useState(false);

  useEffect(() => {
    if (socket) {
      socket.on('QUESTION', (data) => {
        setCurrentQuestion(data);
        setQuestionNumber(data.currentQuestion);
        setTotalQuestions(data.totalQuestions);
        setTimeRemaining(30);
        setSelectedOption(null);
        setSubmitted(false);
      });

      return () => {
        socket.off('QUESTION');
      };
    }
  }, [socket]);

  // Timer effect
  useEffect(() => {
    let timer;
    if (currentQuestion && timeRemaining > 0 && !submitted) {
      timer = setTimeout(() => setTimeRemaining(timeRemaining - 1), 1000);
    } else if (timeRemaining === 0 && !submitted && currentQuestion) {
      submitAnswer();
    }
    return () => clearTimeout(timer);
  }, [timeRemaining, currentQuestion, submitted]);

  const handleOptionSelect = (index) => {
    if (!submitted) {
      setSelectedOption(index);
    }
  };

  const submitAnswer = () => {
    if (currentQuestion && !submitted) {
      setSubmitted(true);
      
      if (socket) {
        socket.emit('SUBMIT_ANSWER', {
          studentId,
          questionId: currentQuestion.questionId,
          selectedOption: selectedOption !== null ? selectedOption : -1,
        });
      }

      setAnsweredQuestions(new Set([...answeredQuestions, currentQuestion.questionId]));
      
      // Auto-submit after 2 seconds
      setTimeout(() => {
        setSubmitted(false);
      }, 2000);
    }
  };

  if (!currentQuestion) {
    return (
      <div className="question-panel">
        <div className="no-question">
          <p>Waiting for questions...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="question-panel">
      <div className="question-container">
        {/* Header */}
        <div className="question-header">
          <div className="question-progress">
            <span className="progress-text">
              Question {questionNumber} of {totalQuestions}
            </span>
            <div className="progress-bar">
              <div
                className="progress-fill"
                style={{ width: `${(questionNumber / totalQuestions) * 100}%` }}
              ></div>
            </div>
          </div>
          <div className={`timer ${timeRemaining <= 5 ? 'critical' : ''}`}>
            <span className="timer-icon">⏱️</span>
            <span className="timer-value">{timeRemaining}s</span>
          </div>
        </div>

        {/* Question */}
        <div className="question-content">
          <h2 className="question-text">{currentQuestion.questionText}</h2>
          <p className="question-marks">Marks: {currentQuestion.marks}</p>
        </div>

        {/* Options */}
        <div className="options-container">
          {currentQuestion.options && currentQuestion.options.map((option, index) => (
            <button
              key={index}
              className={`option-button ${selectedOption === index ? 'selected' : ''} ${
                submitted ? 'disabled' : ''
              }`}
              onClick={() => handleOptionSelect(index)}
              disabled={submitted}
            >
              <span className="option-letter">
                {String.fromCharCode(65 + index)}
              </span>
              <span className="option-text">{option}</span>
              {selectedOption === index && <span className="checkmark">✓</span>}
            </button>
          ))}
        </div>

        {/* Submit Button */}
        <div className="submit-section">
          <button
            className="btn-submit"
            onClick={submitAnswer}
            disabled={submitted || selectedOption === null}
          >
            {submitted ? 'Submitted ✓' : 'Submit Answer'}
          </button>
          {selectedOption === null && !submitted && (
            <p className="hint-text">Please select an option first</p>
          )}
        </div>

        {/* Status */}
        <div className="status-bar">
          <span className="status-text">
            Answered: {answeredQuestions.size} / {totalQuestions}
          </span>
        </div>
      </div>
    </div>
  );
};

export default QuestionPanel;
