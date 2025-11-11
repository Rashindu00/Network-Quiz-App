import React, { useState, useEffect } from 'react';
import QuestionPanel from './QuestionPanel';
import './StudentQuiz.css';

/**
 * StudentQuiz.jsx - Member 3 Frontend
 * Main UI for student to participate in the quiz
 * Handles login, question display, and answer submission
 * 
 * Networking Concepts:
 * - Client connection to server
 * - Receiving questions via socket
 * - Sending answers back to server
 */
const StudentQuiz = ({ socket, onLeave }) => {
  const [loggedIn, setLoggedIn] = useState(false);
  const [studentName, setStudentName] = useState('');
  const [studentId, setStudentId] = useState('');
  const [connected, setConnected] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [quizStarted, setQuizStarted] = useState(false);

  useEffect(() => {
    if (socket) {
      // Check if already connected and update state immediately
      if (socket.connected) {
        setConnected(true);
      }

      socket.on('connect', () => {
        console.log('[StudentQuiz] Connected to server');
        setConnected(true);
        setError('');
      });

      socket.on('disconnect', () => {
        console.log('[StudentQuiz] Disconnected from server');
        setConnected(false);
        setLoggedIn(false);
      });

      socket.on('REGISTRATION_SUCCESS', (data) => {
        console.log('[StudentQuiz] Registration success:', data);
        setLoggedIn(true);
        setStudentId(data.studentId);
        setLoading(false);
      });

      socket.on('REGISTRATION_FAILED', (data) => {
        console.log('[StudentQuiz] Registration failed:', data);
        setError(data.message || 'Registration failed');
        setLoading(false);
      });

      socket.on('QUIZ_STARTED', () => {
        console.log('[StudentQuiz] Quiz started');
        setQuizStarted(true);
      });

      socket.on('ERROR', (data) => {
        console.log('[StudentQuiz] Error:', data);
        setError(data.message || 'An error occurred');
        setLoading(false);
      });

      return () => {
        socket.off('connect');
        socket.off('disconnect');
        socket.off('REGISTRATION_SUCCESS');
        socket.off('REGISTRATION_FAILED');
        socket.off('QUIZ_STARTED');
        socket.off('ERROR');
      };
    }
  }, [socket]);

  const handleLogin = (e) => {
    e.preventDefault();
    
    if (!studentName.trim()) {
      setError('Please enter your name');
      return;
    }

    if (!connected) {
      setError('Not connected to server');
      return;
    }

    setLoading(true);
    setError('');

    if (socket) {
      socket.emit('REGISTER', {
        name: studentName.trim(),
      });
    }
  };

  const handleLogout = () => {
    setLoggedIn(false);
    setQuizStarted(false);
    setStudentName('');
    setStudentId('');
    if (socket) {
      socket.emit('DISCONNECT', {});
    }
    onLeave();
  };

  if (!loggedIn) {
    return (
      <div className="student-quiz">
        <div className="login-container">
          <div className="login-card">
            <div className="login-header">
              <h1>🎓 Quiz Platform</h1>
              <p>Student Login</p>
            </div>

            <form onSubmit={handleLogin} className="login-form">
              <div className="form-group">
                <label htmlFor="studentName">Your Name</label>
                <input
                  type="text"
                  id="studentName"
                  placeholder="Enter your full name"
                  value={studentName}
                  onChange={(e) => setStudentName(e.target.value)}
                  disabled={loading || !connected}
                />
              </div>

              {error && <div className="error-message">{error}</div>}

              <div className="connection-status">
                <span className={`status-indicator ${connected ? 'connected' : 'disconnected'}`}></span>
                <span className="status-text">
                  {connected ? 'Connected to server' : 'Connecting...'}
                </span>
              </div>

              <button
                type="submit"
                className="btn-login"
                disabled={loading || !connected}
              >
                {loading ? 'Joining...' : 'Join Quiz'}
              </button>
            </form>

            <div className="login-info">
              <p>ℹ️ Enter your name to join the quiz</p>
              <p>You will receive questions once the admin starts the quiz</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="student-quiz">
      <div className="quiz-header">
        <div className="student-info">
          <h2>Welcome, {studentName}!</h2>
          <p className="student-id">ID: {studentId}</p>
        </div>
        <button className="btn-logout" onClick={handleLogout}>
          Leave Quiz
        </button>
      </div>

      {!quizStarted ? (
        <div className="waiting-screen">
          <div className="waiting-content">
            <div className="spinner"></div>
            <h3>Waiting for Quiz to Start...</h3>
            <p>The admin will start the quiz soon</p>
            <p className="student-count">You are student #{studentId}</p>
          </div>
        </div>
      ) : (
        <QuestionPanel socket={socket} studentId={studentId} />
      )}
    </div>
  );
};

export default StudentQuiz;
