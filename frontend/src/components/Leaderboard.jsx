import React, { useState, useEffect } from 'react';
import './Leaderboard.css';

/**
 * Leaderboard.jsx - Member 5 Frontend
 * Displays final leaderboard with all students ranked by score
 * Shows position, student name, marks, percentage, and medal for top performers
 * 
 * Networking Concepts:
 * - Broadcasting leaderboard to all clients
 * - NIO-based non-blocking communication
 * - Real-time updates of rankings
 */
const Leaderboard = ({ socket }) => {
  const [rankings, setRankings] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (socket) {
      socket.on('LEADERBOARD', (data) => {
        setRankings(data.rankings || []);
        setStatistics(data.statistics || null);
        setLoading(false);
      });

      return () => {
        socket.off('LEADERBOARD');
      };
    }
  }, [socket]);

  const getMedalEmoji = (rank) => {
    if (rank === 1) return '🥇';
    if (rank === 2) return '🥈';
    if (rank === 3) return '🥉';
    return null;
  };

  const getPerformanceColor = (percentage) => {
    if (percentage >= 90) return '#28a745';
    if (percentage >= 80) return '#17a2b8';
    if (percentage >= 70) return '#ffc107';
    if (percentage >= 60) return '#fd7e14';
    return '#dc3545';
  };

  if (loading) {
    return (
      <div className="leaderboard">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Loading leaderboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="leaderboard">
      <div className="leaderboard-container">
        <div className="leaderboard-header">
          <h1>🏆 Quiz Leaderboard</h1>
          <p className="subtitle">Final Rankings</p>
        </div>

        {/* Statistics Section */}
        {statistics && (
          <div className="statistics-grid">
            <div className="stat-card">
              <span className="stat-icon">👥</span>
              <span className="stat-label">Total Participants</span>
              <span className="stat-value">{statistics.totalStudents}</span>
            </div>
            <div className="stat-card">
              <span className="stat-icon">📊</span>
              <span className="stat-label">Average Score</span>
              <span className="stat-value">{statistics.averageScore.toFixed(1)}</span>
            </div>
            <div className="stat-card">
              <span className="stat-icon">⭐</span>
              <span className="stat-label">Highest Score</span>
              <span className="stat-value">{statistics.highestScore}</span>
            </div>
            <div className="stat-card">
              <span className="stat-icon">📈</span>
              <span className="stat-label">Lowest Score</span>
              <span className="stat-value">{statistics.lowestScore}</span>
            </div>
          </div>
        )}

        {/* Rankings Table */}
        <div className="rankings-table">
          <div className="table-header">
            <div className="col-rank">Rank</div>
            <div className="col-name">Student Name</div>
            <div className="col-score">Score</div>
            <div className="col-percentage">Percentage</div>
          </div>

          {rankings && rankings.length > 0 ? (
            <div className="table-body">
              {rankings.map((entry, index) => (
                <div
                  key={index}
                  className={`table-row ${index < 3 ? 'top-performer' : ''}`}
                  style={{
                    borderLeftColor: getPerformanceColor(entry.percentage),
                  }}
                >
                  <div className="col-rank">
                    <span className="rank-badge">
                      {getMedalEmoji(entry.rank)}
                      {entry.rank}
                    </span>
                  </div>
                  <div className="col-name">
                    <span className="student-name">{entry.studentName}</span>
                  </div>
                  <div className="col-score">
                    <span className="score-value">
                      {entry.obtainedMarks}/{entry.totalMarks}
                    </span>
                  </div>
                  <div className="col-percentage">
                    <div className="percentage-wrapper">
                      <div className="percentage-bar">
                        <div
                          className="percentage-fill"
                          style={{
                            width: `${entry.percentage}%`,
                            backgroundColor: getPerformanceColor(entry.percentage),
                          }}
                        ></div>
                      </div>
                      <span className="percentage-text">
                        {entry.percentage.toFixed(1)}%
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="no-rankings">
              <p>No rankings available yet</p>
            </div>
          )}
        </div>

        {/* Top Performers Highlight */}
        {rankings && rankings.length > 0 && (
          <div className="top-performers">
            <h3>🌟 Top Performers</h3>
            <div className="performers-grid">
              {rankings.slice(0, 3).map((entry) => (
                <div key={entry.rank} className={`performer-card rank-${entry.rank}`}>
                  <div className="performer-medal">
                    {getMedalEmoji(entry.rank)}
                  </div>
                  <div className="performer-info">
                    <p className="performer-rank">Rank #{entry.rank}</p>
                    <p className="performer-name">{entry.studentName}</p>
                    <p className="performer-score">
                      {entry.obtainedMarks}/{entry.totalMarks} ({entry.percentage.toFixed(1)}%)
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Leaderboard;
