import React, { useState, useEffect } from 'react';
import './ResultBoard.css';

/**
 * ResultBoard.jsx - Member 4 Frontend
 * Displays individual student score after quiz completion
 * Shows marks obtained, total marks, percentage, and rank
 * 
 * Networking Concepts:
 * - Receiving score data from server
 * - Real-time score updates
 * - Synchronized threading for score calculation
 */
const ResultBoard = ({ socket, studentId }) => {
  const [score, setScore] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (socket) {
      socket.on('SCORE', (data) => {
        if (data.studentId === studentId || !data.studentId) {
          setScore(data);
          setLoading(false);
        }
      });

      return () => {
        socket.off('SCORE');
      };
    }
  }, [socket, studentId]);

  if (loading) {
    return (
      <div className="result-board">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Calculating your score...</p>
        </div>
      </div>
    );
  }

  if (!score) {
    return (
      <div className="result-board">
        <div className="no-result">
          <p>Score data not available</p>
        </div>
      </div>
    );
  }

  const getGrade = (percentage) => {
    if (percentage >= 90) return 'A';
    if (percentage >= 80) return 'B';
    if (percentage >= 70) return 'C';
    if (percentage >= 60) return 'D';
    return 'F';
  };

  const getGradeColor = (percentage) => {
    if (percentage >= 90) return '#28a745';
    if (percentage >= 80) return '#17a2b8';
    if (percentage >= 70) return '#ffc107';
    if (percentage >= 60) return '#fd7e14';
    return '#dc3545';
  };

  const grade = getGrade(score.percentage);
  const gradeColor = getGradeColor(score.percentage);

  return (
    <div className="result-board">
      <div className="result-card">
        <div className="result-header">
          <h2>Your Results</h2>
          <p className="student-name">{score.studentName}</p>
        </div>

        {/* Grade Circle */}
        <div className="grade-container">
          <div className="grade-circle" style={{ borderColor: gradeColor }}>
            <span className="grade-letter" style={{ color: gradeColor }}>
              {grade}
            </span>
          </div>
        </div>

        {/* Score Details */}
        <div className="score-details">
          <div className="detail-row">
            <span className="detail-label">Marks Obtained</span>
            <span className="detail-value">{score.obtainedMarks}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Total Marks</span>
            <span className="detail-value">{score.totalMarks}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Percentage</span>
            <span className="detail-value">{score.percentage.toFixed(2)}%</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Rank</span>
            <span className="detail-value">
              #{score.rank}
            </span>
          </div>
        </div>

        {/* Progress Bar */}
        <div className="progress-section">
          <div className="progress-label">
            <span>Score Progress</span>
            <span className="progress-text">{score.obtainedMarks}/{score.totalMarks}</span>
          </div>
          <div className="progress-bar">
            <div
              className="progress-fill"
              style={{
                width: `${(score.obtainedMarks / score.totalMarks) * 100}%`,
                backgroundColor: gradeColor,
              }}
            ></div>
          </div>
        </div>

        {/* Performance Message */}
        <div className="performance-message">
          {score.percentage >= 90 && (
            <p className="excellent">🎉 Excellent performance!</p>
          )}
          {score.percentage >= 80 && score.percentage < 90 && (
            <p className="good">👍 Great job!</p>
          )}
          {score.percentage >= 70 && score.percentage < 80 && (
            <p className="satisfactory">✓ Good effort</p>
          )}
          {score.percentage >= 60 && score.percentage < 70 && (
            <p className="average">Try harder next time</p>
          )}
          {score.percentage < 60 && (
            <p className="poor">Keep practicing</p>
          )}
        </div>
      </div>
    </div>
  );
};

export default ResultBoard;
