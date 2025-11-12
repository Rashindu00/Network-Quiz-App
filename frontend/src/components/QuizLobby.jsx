import React, { useState } from 'react';
import './QuizLobby.css';

/**
 * Quiz Lobby - Students enter their name and join the quiz
 */
const QuizLobby = ({ onJoinQuiz }) => {
  const [studentName, setStudentName] = useState('');
  const [isConnecting, setIsConnecting] = useState(false);
  const [error, setError] = useState('');

  const handleJoin = (e) => {
    e.preventDefault();
    
    // Validate name
    if (!studentName.trim()) {
      setError('Please enter your name');
      return;
    }

    if (studentName.trim().length < 2) {
      setError('Name must be at least 2 characters');
      return;
    }

    setIsConnecting(true);
    setError('');

    // Call parent component to connect to quiz server
    onJoinQuiz(studentName.trim());
  };

  return (
    <div className="quiz-lobby">
      <div className="lobby-container">
        <div className="lobby-header">
          <h1>🎯 Network Quiz Application</h1>
          <p className="subtitle">Test your knowledge in networking!</p>
        </div>

        <div className="lobby-card">
          <div className="card-icon">👤</div>
          <h2>Join Quiz</h2>
          <p className="card-description">
            Enter your name to participate in the quiz
          </p>

          <form onSubmit={handleJoin} className="join-form">
            <div className="form-group">
              <label htmlFor="studentName">Your Name</label>
              <input
                type="text"
                id="studentName"
                placeholder="Enter your name..."
                value={studentName}
                onChange={(e) => setStudentName(e.target.value)}
                disabled={isConnecting}
                autoFocus
                maxLength={50}
              />
            </div>

            {error && (
              <div className="error-message">
                <span className="error-icon">⚠️</span>
                {error}
              </div>
            )}

            <button
              type="submit"
              className="join-button"
              disabled={isConnecting}
            >
              {isConnecting ? (
                <>
                  <span className="spinner"></span>
                  Connecting...
                </>
              ) : (
                <>
                  Join Quiz
                  <span className="arrow">→</span>
                </>
              )}
            </button>
          </form>

          <div className="lobby-info">
            <div className="info-item">
              <span className="info-icon">📝</span>
              <span>Multiple choice questions</span>
            </div>
            <div className="info-item">
              <span className="info-icon">⏱️</span>
              <span>30 seconds per question</span>
            </div>
            <div className="info-item">
              <span className="info-icon">🏆</span>
              <span>Compete with other students</span>
            </div>
          </div>
        </div>

        <div className="lobby-footer">
          <p>Quiz starts when 3+ students join</p>
        </div>
      </div>
    </div>
  );
};

export default QuizLobby;
