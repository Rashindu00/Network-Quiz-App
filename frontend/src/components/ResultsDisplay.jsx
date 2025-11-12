import React, { useState, useEffect } from 'react';
import './ResultsDisplay.css';

/**
 * Results Display Component - Member 5 Frontend
 * Shows final quiz results, winner announcement, and statistics
 * 
 * Features:
 * - Winner celebration
 * - Final rankings
 * - Quiz statistics
 * - Performance charts
 * 
 * @author Member 5
 */
const ResultsDisplay = () => {
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showConfetti, setShowConfetti] = useState(false);

  useEffect(() => {
    // Load mock results for demonstration
    loadMockResults();
    setTimeout(() => setShowConfetti(true), 500);
  }, []);

  const loadMockResults = () => {
    const mockResults = {
      quizId: 'QUIZ_2025_001',
      startTime: '2025-11-12 14:00:00',
      endTime: '2025-11-12 14:25:30',
      duration: '25:30',
      winner: {
        name: 'Rashindu',
        score: 95,
        accuracy: 100.0
      },
      statistics: {
        totalParticipants: 5,
        totalQuestions: 10,
        averageScore: 81.0,
        highestScore: 95,
        lowestScore: 70,
        averageAccuracy: 84.0
      },
      topScorers: [
        { rank: 1, name: 'Rashindu', score: 95, correct: 10, total: 10, accuracy: 100.0 },
        { rank: 2, name: 'Navoda', score: 85, correct: 9, total: 10, accuracy: 90.0 },
        { rank: 3, name: 'Weditha', score: 80, correct: 8, total: 10, accuracy: 80.0 }
      ],
      performanceDistribution: {
        excellent: 2,      // 90-100%
        good: 2,          // 70-89%
        average: 1,       // 50-69%
        needsImprovement: 0  // <50%
      }
    };

    setResults(mockResults);
    setLoading(false);
  };

  if (loading) {
    return (
      <div className="results-display">
        <div className="loading-state">
          <div className="loader"></div>
          <p>Generating results...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="results-display">
      
      {showConfetti && <div className="confetti-container">
        {[...Array(50)].map((_, i) => (
          <div key={i} className="confetti" style={{
            left: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 3}s`,
            backgroundColor: ['#ff6b6b', '#4ecdc4', '#ffe66d', '#a8e6cf', '#ff8b94'][Math.floor(Math.random() * 5)]
          }}></div>
        ))}
      </div>}

      {/* Winner Announcement */}
      <section className="winner-section">
        <div className="winner-card">
          <div className="winner-crown">👑</div>
          <h1 className="winner-title">🎉 Quiz Complete! 🎉</h1>
          <div className="winner-announcement">
            <div className="trophy-large">🏆</div>
            <h2 className="winner-name">{results.winner.name}</h2>
            <p className="winner-label">IS THE WINNER!</p>
            <div className="winner-stats">
              <div className="winner-stat">
                <span className="stat-value">{results.winner.score}</span>
                <span className="stat-label">Points</span>
              </div>
              <div className="winner-stat">
                <span className="stat-value">{results.winner.accuracy}%</span>
                <span className="stat-label">Accuracy</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Quiz Information */}
      <section className="quiz-info-section">
        <div className="info-card">
          <h3 className="info-title">📋 Quiz Information</h3>
          <div className="info-grid">
            <div className="info-item">
              <span className="info-label">Quiz ID:</span>
              <span className="info-value">{results.quizId}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Start Time:</span>
              <span className="info-value">{results.startTime}</span>
            </div>
            <div className="info-item">
              <span className="info-label">End Time:</span>
              <span className="info-value">{results.endTime}</span>
            </div>
            <div className="info-item">
              <span className="info-label">Duration:</span>
              <span className="info-value">{results.duration}</span>
            </div>
          </div>
        </div>
      </section>

      {/* Statistics Cards */}
      <section className="stats-section">
        <h3 className="section-title">📊 Quiz Statistics</h3>
        <div className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon">👥</div>
            <div className="stat-content">
              <div className="stat-number">{results.statistics.totalParticipants}</div>
              <div className="stat-text">Participants</div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">📝</div>
            <div className="stat-content">
              <div className="stat-number">{results.statistics.totalQuestions}</div>
              <div className="stat-text">Questions</div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">📈</div>
            <div className="stat-content">
              <div className="stat-number">{results.statistics.averageScore}</div>
              <div className="stat-text">Avg Score</div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">🎯</div>
            <div className="stat-content">
              <div className="stat-number">{results.statistics.averageAccuracy}%</div>
              <div className="stat-text">Avg Accuracy</div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">⬆️</div>
            <div className="stat-content">
              <div className="stat-number">{results.statistics.highestScore}</div>
              <div className="stat-text">Highest</div>
            </div>
          </div>
          <div className="stat-card">
            <div className="stat-icon">⬇️</div>
            <div className="stat-content">
              <div className="stat-number">{results.statistics.lowestScore}</div>
              <div className="stat-text">Lowest</div>
            </div>
          </div>
        </div>
      </section>

      {/* Top 3 Performers */}
      <section className="top-performers-section">
        <h3 className="section-title">🏅 Top 3 Performers</h3>
        <div className="podium-display">
          {results.topScorers.map((scorer, index) => (
            <div key={index} className={`podium-item rank-${scorer.rank}`}>
              <div className="podium-medal">
                {scorer.rank === 1 ? '🥇' : scorer.rank === 2 ? '🥈' : '🥉'}
              </div>
              <div className="podium-rank">#{scorer.rank}</div>
              <div className="podium-name">{scorer.name}</div>
              <div className="podium-score">{scorer.score} pts</div>
              <div className="podium-accuracy">{scorer.accuracy}% accuracy</div>
              <div className="podium-answers">{scorer.correct}/{scorer.total} correct</div>
            </div>
          ))}
        </div>
      </section>

      {/* Performance Distribution */}
      <section className="performance-section">
        <h3 className="section-title">📉 Performance Distribution</h3>
        <div className="performance-grid">
          <div className="performance-bar">
            <div className="performance-label">
              <span className="performance-emoji">🟢</span>
              <span className="performance-text">Excellent (90-100%)</span>
            </div>
            <div className="performance-meter">
              <div 
                className="performance-fill excellent"
                style={{ width: `${(results.performanceDistribution.excellent / results.statistics.totalParticipants) * 100}%` }}
              ></div>
              <span className="performance-count">{results.performanceDistribution.excellent}</span>
            </div>
          </div>

          <div className="performance-bar">
            <div className="performance-label">
              <span className="performance-emoji">🟡</span>
              <span className="performance-text">Good (70-89%)</span>
            </div>
            <div className="performance-meter">
              <div 
                className="performance-fill good"
                style={{ width: `${(results.performanceDistribution.good / results.statistics.totalParticipants) * 100}%` }}
              ></div>
              <span className="performance-count">{results.performanceDistribution.good}</span>
            </div>
          </div>

          <div className="performance-bar">
            <div className="performance-label">
              <span className="performance-emoji">🟠</span>
              <span className="performance-text">Average (50-69%)</span>
            </div>
            <div className="performance-meter">
              <div 
                className="performance-fill average"
                style={{ width: `${(results.performanceDistribution.average / results.statistics.totalParticipants) * 100}%` }}
              ></div>
              <span className="performance-count">{results.performanceDistribution.average}</span>
            </div>
          </div>

          <div className="performance-bar">
            <div className="performance-label">
              <span className="performance-emoji">🔴</span>
              <span className="performance-text">Needs Improvement (&lt;50%)</span>
            </div>
            <div className="performance-meter">
              <div 
                className="performance-fill needs-improvement"
                style={{ width: `${(results.performanceDistribution.needsImprovement / results.statistics.totalParticipants) * 100}%` }}
              ></div>
              <span className="performance-count">{results.performanceDistribution.needsImprovement}</span>
            </div>
          </div>
        </div>
      </section>

      {/* Action Buttons */}
      <section className="actions-section">
        <button className="action-btn primary" onClick={() => window.print()}>
          <span className="btn-icon">🖨️</span>
          <span className="btn-text">Print Results</span>
        </button>
        <button className="action-btn secondary" onClick={() => alert('Results downloaded!')}>
          <span className="btn-icon">💾</span>
          <span className="btn-text">Download Report</span>
        </button>
        <button className="action-btn success" onClick={() => window.location.href = '/'}>
          <span className="btn-icon">🔄</span>
          <span className="btn-text">New Quiz</span>
        </button>
      </section>

    </div>
  );
};

export default ResultsDisplay;
