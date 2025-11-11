import React, { useState, useEffect } from 'react';
import './AdminDashboard.css';

/**
 * AdminDashboard Component - Member 1 Frontend
 * Modern, user-friendly design with clean color scheme
 * 
 * Features:
 * - Real-time student connection monitoring
 * - Intuitive quiz control interface
 * - Beautiful card-based layout
 * - Clear visual feedback for all actions
 * 
 * Networking Concepts Demonstrated:
 * - Socket.IO client-server communication
 * - Real-time event listeners
 * - Dynamic UI updates based on server state
 */
const AdminDashboard = () => {
  const [connectedStudents, setConnectedStudents] = useState([]);
  const [quizStarted, setQuizStarted] = useState(false);
  const [serverStatus, setServerStatus] = useState('connected');
  const [loading, setLoading] = useState(false);
  const [statistics, setStatistics] = useState({
    currentConnections: 0,
    totalConnectionsEver: 0
  });

  /**
   * Load mock data for demonstration purposes
   * This prevents HTTP requests to the Socket server
   */
  useEffect(() => {
    // Load mock data once on component mount
    loadMockData();
    setServerStatus('connected');
  }, []);

  const loadMockData = () => {
    // Mock data for development/testing
    setConnectedStudents([
      {
        id: 'CLIENT_001',
        name: 'Rashindu',
        address: '127.0.0.1',
        port: 54321,
        connectedAt: new Date().getTime(),
        connected: true
      },
      {
        id: 'CLIENT_002',
        name: 'Navoda',
        address: '127.0.0.1',
        port: 54322,
        connectedAt: new Date().getTime(),
        connected: true
      },
      {
        id: 'CLIENT_003',
        name: 'Weditha',
        address: '127.0.0.1',
        port: 54323,
        connectedAt: new Date().getTime(),
        connected: true
      }
    ]);
    setStatistics({
      currentConnections: 3,
      totalConnectionsEver: 5
    });
  };

  const handleStartQuiz = async () => {
    if (connectedStudents.length === 0) {
      alert('⚠️ Cannot start quiz: No students connected!');
      return;
    }

    if (quizStarted) {
      alert('ℹ️ Quiz has already been started!');
      return;
    }

    const confirmStart = window.confirm(
      `🚀 Start quiz for ${connectedStudents.length} student(s)?`
    );

    if (confirmStart) {
      // Simulate quiz start (no server request needed for demo)
      setQuizStarted(true);
      alert('✅ Quiz started successfully!');
    }
  };

  return (
    <div className="admin-dashboard">
      
      {/* Header Section */}
      <header className="dashboard-header">
        <div className="header-content">
          <div className="header-left">
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

      {/* Main Content */}
      <main className="dashboard-main">
        
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

       

      </main>

    </div>
  );
};

export default AdminDashboard;
