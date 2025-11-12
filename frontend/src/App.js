import React, { useState } from 'react';
import AdminDashboard from './components/AdminDashboard';
import StudentQuizApp from './components/StudentQuizApp';
import './App.css';

function App() {
  const [mode, setMode] = useState('select'); // 'select', 'admin', 'student'

  // Mode selection screen
  if (mode === 'select') {
    return (
      <div className="App mode-select">
        <div className="mode-container">
          <h1 className="app-title">🎯 Network Quiz Application</h1>
          <p className="app-subtitle">Choose your role to continue</p>
          
          <div className="mode-cards">
            <button 
              className="mode-card student-card"
              onClick={() => setMode('student')}
            >
              <div className="card-icon">👨‍🎓</div>
              <h2>Student</h2>
              <p>Join and take the quiz</p>
              <span className="card-arrow">→</span>
            </button>

            <button 
              className="mode-card admin-card"
              onClick={() => setMode('admin')}
            >
              <div className="card-icon">👨‍💼</div>
              <h2>Admin</h2>
              <p>Manage quiz and view results</p>
              <span className="card-arrow">→</span>
            </button>
          </div>

          <div className="mode-footer">
            <p>Network Programming Quiz System</p>
            <p className="version">Version 1.0.0</p>
          </div>
        </div>
      </div>
    );
  }

  // Admin Dashboard
  if (mode === 'admin') {
    return (
      <div className="App">
        <AdminDashboard onBack={() => setMode('select')} />
      </div>
    );
  }

  // Student Quiz
  if (mode === 'student') {
    return (
      <div className="App">
        <button 
          className="back-button"
          onClick={() => setMode('select')}
        >
          ← Back to Selection
        </button>
        <StudentQuizApp />
      </div>
    );
  }

  return <div className="App">Loading...</div>;
}

export default App;
