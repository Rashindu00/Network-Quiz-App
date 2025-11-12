import React, { useState, useEffect } from 'react';
import './ScoreboardUI.css';

/**
 * Scoreboard UI Component - Member 4 Frontend
 * Displays live scores and leaderboard
 * 
 * Features:
 * - Real-time score updates
 * - Animated leaderboard
 * - Visual rank indicators
 * - Score statistics
 * 
 * @author Member 4
 */
const ScoreboardUI = () => {
  const [scores, setScores] = useState([]);
  const [statistics, setStatistics] = useState({
    totalParticipants: 0,
    averageScore: 0,
    averageAccuracy: 0,
    highestScore: 0
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Load mock data for demonstration
    loadMockScores();
  }, []);

  const loadMockScores = () => {
    const mockScores = [
      {
        rank: 1,
        clientId: 'CLIENT_001',
        name: 'Rashindu',
        score: 95,
        correct: 10,
        total: 10,
        accuracy: 100.0
      },
      {
        rank: 2,
        clientId: 'CLIENT_002',
        name: 'Navoda',
        score: 85,
        correct: 9,
        total: 10,
        accuracy: 90.0
      },
      {
        rank: 3,
        clientId: 'CLIENT_003',
        name: 'Weditha',
        score: 80,
        correct: 8,
        total: 10,
        accuracy: 80.0
      },
      {
        rank: 4,
        clientId: 'CLIENT_004',
        name: 'Kavindu',
        score: 75,
        correct: 8,
        total: 10,
        accuracy: 80.0
      },
      {
        rank: 5,
        clientId: 'CLIENT_005',
        name: 'Dilshan',
        score: 70,
        correct: 7,
        total: 10,
        accuracy: 70.0
      }
    ];

    setScores(mockScores);
    setStatistics({
      totalParticipants: 5,
      averageScore: 81.0,
      averageAccuracy: 84.0,
      highestScore: 95
    });
    setLoading(false);
  };

  const getRankBadge = (rank) => {
    switch(rank) {
      case 1: return '🥇';
      case 2: return '🥈';
      case 3: return '🥉';
      default: return `#${rank}`;
    }
  };

  const getScoreColor = (accuracy) => {
    if (accuracy >= 90) return '#10b981'; // Green
    if (accuracy >= 70) return '#f59e0b'; // Orange
    return '#ef4444'; // Red
  };

  if (loading) {
    return (
      <div className="scoreboard-ui">
        <div className="loading-state">
          <div className="loader"></div>
          <p>Loading scores...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="scoreboard-ui">
      
      {/* Header */}
      <header className="scoreboard-header">
        <div className="header-content">
          <h1 className="scoreboard-title">
            <span className="trophy-icon">🏆</span>
            Live Leaderboard
          </h1>
          <p className="scoreboard-subtitle">Real-time Quiz Rankings</p>
        </div>
      </header>

      {/* Statistics Cards */}
      <section className="statistics-section">
        <div className="stat-card">
          <div className="stat-icon participants-icon">👥</div>
          <div className="stat-info">
            <div className="stat-value">{statistics.totalParticipants}</div>
            <div className="stat-label">Participants</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon score-icon">📊</div>
          <div className="stat-info">
            <div className="stat-value">{statistics.averageScore}</div>
            <div className="stat-label">Average Score</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon accuracy-icon">🎯</div>
          <div className="stat-info">
            <div className="stat-value">{statistics.averageAccuracy}%</div>
            <div className="stat-label">Avg Accuracy</div>
          </div>
        </div>

        <div className="stat-card">
          <div className="stat-icon trophy-icon">🏅</div>
          <div className="stat-info">
            <div className="stat-value">{statistics.highestScore}</div>
            <div className="stat-label">Highest Score</div>
          </div>
        </div>
      </section>

      {/* Leaderboard Table */}
      <section className="leaderboard-section">
        <div className="section-header">
          <h2 className="section-title">Rankings</h2>
          <div className="live-indicator">
            <span className="pulse-dot"></span>
            <span className="live-text">Live</span>
          </div>
        </div>

        <div className="leaderboard-table">
          <div className="table-header">
            <div className="col-rank">Rank</div>
            <div className="col-player">Player</div>
            <div className="col-score">Score</div>
            <div className="col-answers">Answers</div>
            <div className="col-accuracy">Accuracy</div>
          </div>

          <div className="table-body">
            {scores.map((player, index) => (
              <div 
                key={player.clientId} 
                className={`table-row ${index < 3 ? 'top-three' : ''}`}
                style={{ animationDelay: `${index * 0.1}s` }}
              >
                <div className="col-rank">
                  <span className="rank-badge" style={{
                    background: index < 3 ? 'linear-gradient(135deg, #fbbf24, #f59e0b)' : '#6b7280'
                  }}>
                    {getRankBadge(player.rank)}
                  </span>
                </div>

                <div className="col-player">
                  <div className="player-info">
                    <span className="player-name">{player.name}</span>
                    <span className="player-id">{player.clientId}</span>
                  </div>
                </div>

                <div className="col-score">
                  <div className="score-display">
                    <span className="score-value" style={{ color: getScoreColor(player.accuracy) }}>
                      {player.score}
                    </span>
                    <span className="score-label">pts</span>
                  </div>
                </div>

                <div className="col-answers">
                  <div className="answers-display">
                    <span className="correct-count">{player.correct}</span>
                    <span className="separator">/</span>
                    <span className="total-count">{player.total}</span>
                  </div>
                </div>

                <div className="col-accuracy">
                  <div className="accuracy-bar-container">
                    <div 
                      className="accuracy-bar"
                      style={{ 
                        width: `${player.accuracy}%`,
                        background: getScoreColor(player.accuracy)
                      }}
                    ></div>
                    <span className="accuracy-text">{player.accuracy.toFixed(1)}%</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Top 3 Podium */}
      <section className="podium-section">
        <h2 className="section-title">🎖️ Top 3 Champions 🎖️</h2>
        
        <div className="podium">
          {/* Second Place */}
          {scores[1] && (
            <div className="podium-place second-place">
              <div className="podium-card">
                <div className="place-badge silver">🥈</div>
                <div className="place-rank">#2</div>
                <div className="place-name">{scores[1].name}</div>
                <div className="place-score">{scores[1].score} pts</div>
              </div>
            </div>
          )}

          {/* First Place */}
          {scores[0] && (
            <div className="podium-place first-place">
              <div className="podium-card winner">
                <div className="place-badge gold">🥇</div>
                <div className="place-rank">WINNER</div>
                <div className="place-name">{scores[0].name}</div>
                <div className="place-score">{scores[0].score} pts</div>
                <div className="winner-crown">👑</div>
              </div>
            </div>
          )}

          {/* Third Place */}
          {scores[2] && (
            <div className="podium-place third-place">
              <div className="podium-card">
                <div className="place-badge bronze">🥉</div>
                <div className="place-rank">#3</div>
                <div className="place-name">{scores[2].name}</div>
                <div className="place-score">{scores[2].score} pts</div>
              </div>
            </div>
          )}
        </div>
      </section>

    </div>
  );
};

export default ScoreboardUI;
