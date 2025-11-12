import React, { useState, useEffect, useRef } from 'react';
import QuizLobby from './QuizLobby';
import QuizRoom from './QuizRoom';
import './StudentQuizApp.css';

/**
 * Student Quiz Application
 * Connects to backend quiz server via WebSocket
 */
const StudentQuizApp = () => {
  const [gameState, setGameState] = useState('lobby'); // 'lobby', 'waiting', 'playing', 'ended'
  const [studentName, setStudentName] = useState('');
  const [currentQuestion, setCurrentQuestion] = useState(null);
  const [questionNumber, setQuestionNumber] = useState(0);
  const [totalQuestions, setTotalQuestions] = useState(0);
  const [timeRemaining, setTimeRemaining] = useState(30);
  const [currentScore, setCurrentScore] = useState(0);
  const [connectionError, setConnectionError] = useState(null);
  
  const socketRef = useRef(null);
  const timerRef = useRef(null);

  // Connect to quiz server
  const connectToQuiz = (name) => {
    setStudentName(name);
    setGameState('connecting');

    try {
      // Connect to WebSocket server
      const socket = new WebSocket('ws://localhost:8081');
      
      socket.onopen = () => {
        console.log('Connected to quiz server');
        // Send name to register
        socket.send(JSON.stringify({
          type: 'REGISTER',
          name: name
        }));
        setGameState('waiting');
      };

      socket.onmessage = (event) => {
        handleServerMessage(event.data);
      };

      socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        setConnectionError('Failed to connect to quiz server');
        setGameState('lobby');
      };

      socket.onclose = () => {
        console.log('Disconnected from quiz server');
        if (gameState !== 'ended') {
          setConnectionError('Connection lost');
          setGameState('lobby');
        }
      };

      socketRef.current = socket;
    } catch (error) {
      console.error('Connection error:', error);
      setConnectionError('Unable to connect to server');
      setGameState('lobby');
    }
  };

  // Handle messages from server
  const handleServerMessage = (data) => {
    try {
      const message = JSON.parse(data);

      switch (message.type) {
        case 'WELCOME':
          console.log('Registration successful');
          break;

        case 'QUIZ_START':
          setTotalQuestions(message.totalQuestions);
          setGameState('playing');
          break;

        case 'QUESTION':
          setCurrentQuestion({
            id: message.questionId,
            text: message.questionText,
            optionA: message.options.A,
            optionB: message.options.B,
            optionC: message.options.C,
            optionD: message.options.D,
            points: 10
          });
          setQuestionNumber(message.questionNumber);
          setTimeRemaining(message.timeLimit || 30);
          startTimer(message.timeLimit || 30);
          break;

        case 'RESULT':
          if (message.correct) {
            setCurrentScore(message.score);
          }
          break;

        case 'QUIZ_END':
          setGameState('ended');
          stopTimer();
          break;

        case 'INFO':
          console.log('Server info:', message.message);
          break;

        default:
          console.log('Unknown message type:', message.type);
      }
    } catch (error) {
      console.error('Error parsing server message:', error);
    }
  };

  // Start countdown timer
  const startTimer = (duration) => {
    stopTimer(); // Clear any existing timer
    
    let remaining = duration;
    setTimeRemaining(remaining);

    timerRef.current = setInterval(() => {
      remaining--;
      setTimeRemaining(remaining);

      if (remaining <= 0) {
        stopTimer();
        // Auto-submit or handle timeout
      }
    }, 1000);
  };

  // Stop timer
  const stopTimer = () => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
  };

  // Submit answer
  const submitAnswer = (answer) => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify({
        type: 'ANSWER',
        questionId: currentQuestion.id,
        answer: answer
      }));
      stopTimer();
    }
  };

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      stopTimer();
      if (socketRef.current) {
        socketRef.current.close();
      }
    };
  }, []);

  // Render appropriate screen
  if (gameState === 'lobby' || gameState === 'connecting') {
    return (
      <div className="student-quiz-app">
        {connectionError && (
          <div className="connection-error">
            <span className="error-icon">⚠️</span>
            {connectionError}
          </div>
        )}
        <QuizLobby onJoinQuiz={connectToQuiz} />
      </div>
    );
  }

  return (
    <div className="student-quiz-app">
      <QuizRoom
        studentName={studentName}
        currentQuestion={currentQuestion}
        questionNumber={questionNumber}
        totalQuestions={totalQuestions}
        timeRemaining={timeRemaining}
        onAnswerSubmit={submitAnswer}
        waitingForQuiz={gameState === 'waiting'}
        quizEnded={gameState === 'ended'}
        currentScore={currentScore}
      />
    </div>
  );
};

export default StudentQuizApp;
