import React, { useState, useEffect, useRef } from 'react';
import './AdminDashboard.css';
import './leaderboard-styles.css';

/**
 * AdminDashboard Component - Member 1 Frontend
 * Modern, user-friendly design with clean color scheme
 * 
 * Features:
 * - Real-time student connection monitoring via WebSocket
 * - Intuitive quiz control interface
 * - Beautiful card-based layout
 * - Clear visual feedback for all actions
 */
const AdminDashboard = ({ onBack }) => {
  const [connectedStudents, setConnectedStudents] = useState([]);
  const [quizStarted, setQuizStarted] = useState(false);
  const [serverStatus, setServerStatus] = useState('connecting');
  const [leaderboard, setLeaderboard] = useState([]);
  const [liveAnswers, setLiveAnswers] = useState([]);
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [activeTab, setActiveTab] = useState('overview'); // 'overview', 'leaderboard', 'answers'
  const [statistics, setStatistics] = useState({
    currentConnections: 0,
    totalConnectionsEver: 0
  });
  
  const socketRef = useRef(null);
  const reconnectTimeoutRef = useRef(null);

  /**
   * Connect to WebSocket server for real-time updates
   */
  useEffect(() => {
    connectToWebSocket();
    
    return () => {
      if (socketRef.current) {
        socketRef.current.close();
      }
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
    };
  }, []);

  const connectToWebSocket = () => {
    try {
      const socket = new WebSocket('ws://localhost:8081');
      
      socket.onopen = () => {
        console.log('Admin connected to WebSocket server');
        setServerStatus('connected');
        
        // Register as admin
        socket.send(JSON.stringify({
          type: 'ADMIN_CONNECT'
        }));
        
        // Request current status
        requestStatus(socket);
      };

      socket.onmessage = (event) => {
        handleServerMessage(event.data);
      };

      socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        setServerStatus('disconnected');
      };

      socket.onclose = () => {
        console.log('WebSocket connection closed');
        setServerStatus('disconnected');
        
        // Attempt to reconnect after 3 seconds
        reconnectTimeoutRef.current = setTimeout(() => {
          console.log('Attempting to reconnect...');
          connectToWebSocket();
        }, 3000);
      };

      socketRef.current = socket;
    } catch (error) {
      console.error('Connection error:', error);
      setServerStatus('disconnected');
    }
  };

  const requestStatus = (socket) => {
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify({
        type: 'GET_STATUS'
      }));
    }
  };

  const handleServerMessage = (data) => {
    try {
      const message = JSON.parse(data);

      switch (message.type) {
        case 'WELCOME':
        case 'REGISTERED':
          console.log('Connected to server:', message.message);
          break;

        case 'STATUS':
          // Update student list and statistics
          if (message.students) {
            setConnectedStudents(message.students.map((student, index) => ({
              id: student.clientId || `STUDENT_${index}`,
              name: student.name || 'Unknown',
              address: '127.0.0.1',
              port: 8081,
              connectedAt: student.registrationTime || Date.now(),
              connected: true
            })));
            
            setStatistics({
              currentConnections: message.registeredCount || 0,
              totalConnectionsEver: message.totalConnections || message.registeredCount || 0
            });
          }
          break;

        case 'STUDENT_JOINED':
          // A new student joined
          console.log('Student joined:', message.studentName);
          requestStatus(socketRef.current);
          break;

        case 'STUDENT_LEFT':
          // A student disconnected
          console.log('Student left:', message.studentName);
          requestStatus(socketRef.current);
          break;

        case 'QUIZ_STARTED':
          setQuizStarted(true);
          break;

        case 'LEADERBOARD_UPDATE':
          // Update live leaderboard
          if (message.leaderboard) {
            setLeaderboard(message.leaderboard);
          }
          break;

        case 'STUDENT_ANSWERED':
          // Track live answers
          if (message.data) {
            const answer = {
              studentName: message.data.studentName,
              questionId: message.data.questionId,
              answer: message.data.answer,
              timestamp: message.data.timestamp
            };
            setLiveAnswers(prev => [answer, ...prev].slice(0, 20)); // Keep last 20 answers
          }
          break;

        case 'QUESTION':
          // Track current question
          setCurrentQuestion({
            number: message.questionNumber,
            text: message.questionText
          });
          break;

        case 'INFO':
        case 'ERROR':
          console.log('Server message:', message.message);
          break;

        default:
          console.log('Unknown message type:', message.type);
      }
    } catch (error) {
      console.error('Error parsing server message:', error);
    }
  };

  const handleStartQuiz = async () => {
    if (statistics.currentConnections === 0) {
      alert('⚠️ Cannot start quiz: No students connected!');
      return;
    }

    if (quizStarted) {
      alert('ℹ️ Quiz has already been started!');
      return;
    }

    const confirmStart = window.confirm(
      `🚀 Start quiz for ${statistics.currentConnections} student(s)?`
    );

    if (confirmStart) {
      // Send start command to WebSocket server
      if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
        socketRef.current.send(JSON.stringify({
          type: 'START_QUIZ'
        }));
        
        setQuizStarted(true);
        alert('✅ Quiz started successfully!');
      } else {
        alert('❌ Not connected to server. Please refresh and try again.');
      }
    }
  };

  return (
    <div className="admin-dashboard">
      
      {/* Header Section */}
      <header className="dashboard-header">
        <div className="header-content">
          <div className="header-left">
            <button className="header-back-button" onClick={onBack}>
              <span className="back-arrow">←</span>
              <span className="back-text">Back</span>
            </button>
            <div className="logo-section">
              <span className="logo-icon"></span>
              <div className="title-group">
                <h1 className="dashboard-title">Quiz Admin Dashboard</h1>
                <p className="dashboard-subtitle">Real-time Student Connection Management</p>
              </div>
            </div>
          </div>
          <div className="header-right">
            <div className={`server-status ${serverStatus}`}>
              <span className="status-indicator"></span>
              <div className="status-text">
                <span className="status-label">Server Status</span>
                <span className="status-value">
                  {serverStatus === 'connected' ? 'Online' : 'Offline'}
                </span>
              </div>
            </div>
          </div>
        </div>
      </header>

      {/* Navigation Tabs */}
      <nav className="dashboard-nav">
        <div className="nav-container">
          <button 
            className={`nav-tab ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            <span className="nav-icon">📊</span>
            <span className="nav-label">Overview</span>
            {activeTab === 'overview' && <span className="nav-indicator"></span>}
          </button>
          
          <button 
            className={`nav-tab ${activeTab === 'leaderboard' ? 'active' : ''} ${!quizStarted ? 'disabled' : ''}`}
            onClick={() => quizStarted && setActiveTab('leaderboard')}
            disabled={!quizStarted}
          >
            <span className="nav-icon">🏆</span>
            <span className="nav-label">Live Leaderboard</span>
            {leaderboard.length > 0 && <span className="nav-badge">{leaderboard.length}</span>}
            {activeTab === 'leaderboard' && <span className="nav-indicator"></span>}
          </button>
          
          <button 
            className={`nav-tab ${activeTab === 'answers' ? 'active' : ''} ${!quizStarted ? 'disabled' : ''}`}
            onClick={() => quizStarted && setActiveTab('answers')}
            disabled={!quizStarted}
          >
            <span className="nav-icon">📝</span>
            <span className="nav-label">Live Answers</span>
            {liveAnswers.length > 0 && <span className="nav-badge">{liveAnswers.length}</span>}
            {activeTab === 'answers' && <span className="nav-indicator"></span>}
          </button>
        </div>
      </nav>

      {/* Main Content */}
      <main className="dashboard-main">
        
        {/* Overview Tab */}
        {activeTab === 'overview' && (
          <>
            {/* Statistics Cards */}
            <section className="stats-section">
          <div className="stat-card stat-primary">
            <div className="stat-icon-wrapper primary">
              <span className="stat-icon">👥</span>
            </div>
            <div className="stat-details">
              <h3 className="stat-value">{statistics.currentConnections}</h3>
              <p className="stat-label">Connected Students</p>
            </div>
            <div className="stat-trend">
              <span className="trend-indicator up">↑ Active</span>
            </div>
          </div>

          <div className="stat-card stat-success">
            <div className="stat-icon-wrapper success">
              <span className="stat-icon"></span>
            </div>
            <div className="stat-details">
              <h3 className="stat-value">{statistics.totalConnectionsEver}</h3>
              <p className="stat-label">Total Connections</p>
            </div>
            <div className="stat-trend">
              <span className="trend-indicator">All Time</span>
            </div>
          </div>

          <div className="stat-card stat-info">
            <div className="stat-icon-wrapper info">
              <span className="stat-icon">🎯</span>
            </div>
            <div className="stat-details">
              <h3 className="stat-value">{quizStarted ? 'Active' : 'Ready'}</h3>
              <p className="stat-label">Quiz Status</p>
            </div>
            <div className="stat-trend">
              <span className={`trend-indicator ${quizStarted ? 'active' : ''}`}>
                {quizStarted ? '● Live' : '○ Standby'}
              </span>
            </div>
          </div>
        </section>

        {/* Control Panel */}
        <section className="control-section">
          <div className="control-card">
            <div className="control-header">
              <div className="control-title-group">
                <h2 className="control-title">Quiz Controls</h2>
                <p className="control-description">
                  Start the quiz when all students are connected and ready
                </p>
              </div>
            </div>
            <div className="control-body">
              <button 
                className={`start-quiz-btn ${quizStarted ? 'quiz-active' : ''} ${statistics.currentConnections === 0 ? 'btn-disabled' : ''}`}
                onClick={handleStartQuiz}
                disabled={quizStarted || statistics.currentConnections === 0}
              >
                <span className="btn-icon">
                  {quizStarted ? '✅' : '🚀'}
                </span>
                <span className="btn-text">
                  {quizStarted ? 'Quiz Started Successfully' : 'Start Quiz Now'}
                </span>
              </button>
              
              {statistics.currentConnections === 0 && !quizStarted && (
                <div className="control-warning">
                  <span className="warning-icon">⚠️</span>
                  <span className="warning-text">
                    Waiting for at least one student to connect before starting
                  </span>
                </div>
              )}
              
              {quizStarted && (
                <div className="control-info">
                  <span className="info-icon">ℹ️</span>
                  <span className="info-text">
                    Quiz is currently active for {statistics.currentConnections} student(s)
                  </span>
                </div>
              )}
            </div>
          </div>
        </section>

        {/* Students Section */}
        <section className="students-section">
          <div className="section-header">
            <div className="section-title-group">
              <h2 className="section-title">
                Connected Students
                <span className="student-count-badge">{connectedStudents.length}</span>
              </h2>
              <p className="section-subtitle">Live student connections</p>
            </div>
          </div>

          <div className="students-content">
            {connectedStudents.length === 0 ? (
              <div className="empty-state">
                <div className="empty-icon">🔍</div>
                <h3 className="empty-title">No Students Connected</h3>
                <p className="empty-description">
                  Waiting for students to join the quiz session...
                </p>
                <div className="loading-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            ) : (
              <div className="students-grid">
                {connectedStudents.map((student, index) => (
                  <div key={student.id} className="student-card">
                    <div className="student-card-header">
                      <div className="student-avatar">
                        <span className="avatar-text">
                          {student.name.charAt(0).toUpperCase()}
                        </span>
                      </div>
                      <div className={`student-status ${student.connected ? 'online' : 'offline'}`}>
                        <span className="status-dot"></span>
                        <span className="status-label">
                          {student.connected ? 'Online' : 'Offline'}
                        </span>
                      </div>
                    </div>
                    
                    <div className="student-card-body">
                      <h3 className="student-name">{student.name}</h3>
                      <div className="student-number">Student #{index + 1}</div>
                      
                      <div className="student-details">
                        <div className="detail-row">
                          <span className="detail-icon">🔑</span>
                          <div className="detail-content">
                            <span className="detail-label">Client ID</span>
                            <span className="detail-value">{student.id}</span>
                          </div>
                        </div>
                        
                        <div className="detail-row">
                          <span className="detail-icon">🌐</span>
                          <div className="detail-content">
                            <span className="detail-label">IP Address</span>
                            <span className="detail-value">{student.address}</span>
                          </div>
                        </div>
                        
                        <div className="detail-row">
                          <span className="detail-icon">🔌</span>
                          <div className="detail-content">
                            <span className="detail-label">Port</span>
                            <span className="detail-value">{student.port}</span>
                          </div>
                        </div>
                        
                        <div className="detail-row">
                          <span className="detail-icon">⏰</span>
                          <div className="detail-content">
                            <span className="detail-label">Connected At</span>
                            <span className="detail-value">
                              {new Date(student.connectedAt).toLocaleTimeString()}
                            </span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </section>
          </>
        )}

        {/* Leaderboard Tab */}
        {activeTab === 'leaderboard' && (
          <>
            {/* Live Leaderboard Section */}
            {quizStarted && leaderboard.length > 0 ? (
          <section className="leaderboard-section">
            <div className="section-header">
              <div className="section-title-group">
                <h2 className="section-title">
                  🏆 Live Leaderboard
                  {currentQuestion && <span className="current-question"> - Question {currentQuestion.number}</span>}
                </h2>
                <p className="section-subtitle">Real-time scores and rankings</p>
              </div>
            </div>

            <div className="leaderboard-content">
              <div className="leaderboard-table">
                <table>
                  <thead>
                    <tr>
                      <th>Rank</th>
                      <th>Student</th>
                      <th>Score</th>
                      <th>Answers</th>
                    </tr>
                  </thead>
                  <tbody>
                    {leaderboard.map((entry, index) => (
                      <tr key={index} className={index === 0 ? 'first-place' : index === 1 ? 'second-place' : index === 2 ? 'third-place' : ''}>
                        <td className="rank-cell">
                          {entry.rank === 1 ? '🥇' : entry.rank === 2 ? '🥈' : entry.rank === 3 ? '🥉' : entry.rank}
                        </td>
                        <td className="name-cell">
                          <div className="student-avatar-small">
                            {entry.name.charAt(0).toUpperCase()}
                          </div>
                          {entry.name}
                        </td>
                        <td className="score-cell">
                          <span className="score-badge">{entry.score}</span>
                        </td>
                        <td className="answers-cell">{entry.answersCount}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </section>
            ) : (
              <div className="empty-state-large">
                <div className="empty-icon-large">🏆</div>
                <h3 className="empty-title-large">No Leaderboard Data Yet</h3>
                <p className="empty-description-large">
                  Leaderboard will appear once the quiz starts and students begin answering questions.
                </p>
              </div>
            )}
          </>
        )}

        {/* Answers Tab */}
        {activeTab === 'answers' && (
          <>
            {/* Live Answers Feed */}
            {quizStarted && liveAnswers.length > 0 ? (
          <section className="live-answers-section">
            <div className="section-header">
              <div className="section-title-group">
                <h2 className="section-title">
                  📝 Live Answer Feed
                </h2>
                <p className="section-subtitle">Students' answers in real-time</p>
              </div>
            </div>

            <div className="live-answers-content">
              <div className="answers-feed">
                {liveAnswers.map((answer, index) => (
                  <div key={index} className="answer-item">
                    <div className="answer-student">
                      <div className="student-avatar-tiny">
                        {answer.studentName.charAt(0).toUpperCase()}
                      </div>
                      <span className="student-name-small">{answer.studentName}</span>
                    </div>
                    <div className="answer-details">
                      <span className="question-badge">Q{answer.questionId}</span>
                      <span className="answer-badge">{answer.answer}</span>
                    </div>
                    <div className="answer-time">
                      {new Date(answer.timestamp).toLocaleTimeString()}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </section>
            ) : (
              <div className="empty-state-large">
                <div className="empty-icon-large">📝</div>
                <h3 className="empty-title-large">No Live Answers Yet</h3>
                <p className="empty-description-large">
                  Live answer feed will show students' responses as they submit their answers during the quiz.
                </p>
              </div>
            )}
          </>
        )}

      </main>

    </div>
  );
};

export default AdminDashboard;
