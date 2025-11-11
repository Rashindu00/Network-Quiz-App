import React, { useState, useEffect } from 'react';
import io from 'socket.io-client';
import AdminDashboard from './components/AdminDashboard';
import StudentQuiz from './components/StudentQuiz';
import ResultBoard from './components/ResultBoard';
import Leaderboard from './components/Leaderboard';
import './App.css';

/**
 * App.js - Main Application Component
 * Orchestrates all frontend components and WebSocket communication
 * 
 * Features:
 * - Role selection (Admin/Student)
 * - Real-time Socket.IO communication
 * - Multiple views (Dashboard, Quiz, Results, Leaderboard)
 * - Member-wise task distribution
 */
function App() {
  const [role, setRole] = useState(null); // 'admin' or 'student'
  const [socket, setSocket] = useState(null);
  const [connected, setConnected] = useState(false);
  const [currentView, setCurrentView] = useState('role-selection');
  const [studentId, setStudentId] = useState('');

  useEffect(() => {
    // Initialize Socket.IO connection to backend
    const newSocket = io('http://localhost:3001', {
      reconnection: true,
      reconnectionDelay: 1000,
      reconnectionDelayMax: 5000,
      reconnectionAttempts: 5,
    });

    newSocket.on('connect', () => {
      console.log('Connected to server');
      setConnected(true);
    });

    newSocket.on('disconnect', () => {
      console.log('Disconnected from server');
      setConnected(false);
    });

    newSocket.on('error', (error) => {
      console.error('Socket error:', error);
    });

    // Listen for quiz completion
    newSocket.on('QUIZ_COMPLETE', () => {
      setCurrentView('result-board');
    });

    setSocket(newSocket);

    return () => {
      newSocket.close();
    };
  }, []);

  const handleSelectRole = (selectedRole) => {
    setRole(selectedRole);
    if (selectedRole === 'admin') {
      setCurrentView('admin-dashboard');
    } else {
      setCurrentView('student-quiz');
    }
  };

  const handleStudentRegistered = (id) => {
    setStudentId(id);
  };

  const handleLeaveQuiz = () => {
    setRole(null);
    setCurrentView('role-selection');
    setStudentId('');
  };

  const handleViewLeaderboard = () => {
    setCurrentView('leaderboard');
  };

  if (!connected) {
    return (
      <div className="app loading">
        <div className="connection-loading">
          <div className="spinner"></div>
          <h2>Connecting to Quiz Server...</h2>
          <p>Please wait while we establish connection</p>
        </div>
      </div>
    );
  }

  return (
    <div className="app">
      {currentView === 'role-selection' && (
        <RoleSelection onSelectRole={handleSelectRole} />
      )}

      {currentView === 'admin-dashboard' && socket && (
        <AdminDashboard socket={socket} onViewLeaderboard={handleViewLeaderboard} />
      )}

      {currentView === 'student-quiz' && socket && (
        <StudentQuiz
          socket={socket}
          onStudentRegistered={handleStudentRegistered}
          onLeave={handleLeaveQuiz}
        />
      )}

      {currentView === 'result-board' && socket && (
        <ResultBoard socket={socket} studentId={studentId} onViewLeaderboard={handleViewLeaderboard} />
      )}

      {currentView === 'leaderboard' && socket && (
        <Leaderboard socket={socket} />
      )}
    </div>
  );
}

/**
 * RoleSelection Component - Initial screen to select role
 * Displays options for Admin and Student roles
 */
const RoleSelection = ({ onSelectRole }) => {
  return (
    <div className="role-selection">
      <div className="role-container">
        <div className="role-header">
          <h1>🎓 Network Quiz Platform</h1>
          <p>Select your role to begin</p>
        </div>

        <div className="role-grid">
          <div
            className="role-card admin"
            onClick={() => onSelectRole('admin')}
          >
            <div className="role-icon">👨‍💼</div>
            <h3>Admin</h3>
            <p>Manage students and start quiz</p>
            <button className="role-button">Enter as Admin</button>
          </div>

          <div
            className="role-card student"
            onClick={() => onSelectRole('student')}
          >
            <div className="role-icon">👨‍🎓</div>
            <h3>Student</h3>
            <p>Join quiz and answer questions</p>
            <button className="role-button">Enter as Student</button>
          </div>
        </div>

        <div className="role-info">
          <p>
            🌐 <strong>Member-Wise Task Distribution:</strong>
          </p>
          <ul>
            <li><strong>Member 1:</strong> ServerSocket, Multithreading, Client Management</li>
            <li><strong>Member 2:</strong> Question Broadcasting with ObjectOutputStream</li>
            <li><strong>Member 3:</strong> Client Socket Connection & Answer Submission</li>
            <li><strong>Member 4:</strong> Synchronized Answer Evaluation & Scoring</li>
            <li><strong>Member 5:</strong> NIO-based Result Distribution & Leaderboard</li>
          </ul>
        </div>
      </div>
    </div>
  );
};

export default App;
