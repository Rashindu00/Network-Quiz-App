/**
 * Socket.IO Bridge Server - Connects React frontend to Java backend
 * 
 * This lightweight Node.js server:
 * 1. Listens on port 3001 for Socket.IO client connections (React frontend)
 * 2. Proxies events to the Java backend (REST API on 8080, ServerSocket on 5000)
 * 3. Broadcasts quiz events to all connected students
 */

const express = require('express');
const http = require('http');
const socketIo = require('socket.io');
const axios = require('axios');
const net = require('net');

const app = express();
const server = http.createServer(app);
const io = socketIo(server, {
  cors: {
    origin: '*',
    methods: ['GET', 'POST']
  }
});

const PORT = 3001;
const JAVA_REST_API = 'http://localhost:8080';
const JAVA_SOCKET_HOST = 'localhost';
const JAVA_SOCKET_PORT = 5000;

// Track connected students
const connectedStudents = new Map();

console.log('╔════════════════════════════════════════╗');
console.log('║     Socket.IO Bridge Server Started    ║');
console.log('╚════════════════════════════════════════╝');
console.log(`✓ Socket.IO listening on port: ${PORT}`);
console.log(`✓ Java REST API: ${JAVA_REST_API}`);
console.log(`✓ Java Socket: ${JAVA_SOCKET_HOST}:${JAVA_SOCKET_PORT}`);

// Socket.IO connection handler
io.on('connection', (socket) => {
  console.log(`[Socket.IO] Client connected: ${socket.id}`);

  // Admin: Start Quiz
  socket.on('START_QUIZ', async (data, callback) => {
    try {
      console.log('[Admin Event] START_QUIZ triggered');
      
      // Call Java REST API to start quiz
      const response = await axios.post(`${JAVA_REST_API}/api/quiz/start`);
      console.log('[Java Response] Quiz started:', response.data);
      
      // Broadcast to all connected clients
      io.emit('QUIZ_STARTED', { message: 'Quiz has started!' });
      
      if (callback) callback({ success: true, message: 'Quiz started' });
    } catch (error) {
      console.error('[Error] Failed to start quiz:', error.message);
      if (callback) callback({ success: false, error: error.message });
    }
  });

  // Student: Register
  socket.on('REGISTER', (data, callback) => {
    const { name } = data;
    console.log(`[Student Register] ${name} (${socket.id})`);
    
    connectedStudents.set(socket.id, {
      id: socket.id,
      name: name,
      connectedAt: new Date()
    });
    
    // Send success response back to student
    socket.emit('REGISTRATION_SUCCESS', {
      success: true,
      studentId: socket.id,
      studentName: name
    });
    
    // Notify admin of new student
    io.emit('STUDENT_CONNECTED', {
      studentId: socket.id,
      studentName: name,
      totalConnected: connectedStudents.size
    });
    
    if (callback) callback({ success: true, studentId: socket.id });
  });

  // Student: Submit Answer
  socket.on('SUBMIT_ANSWER', (data, callback) => {
    const student = connectedStudents.get(socket.id);
    if (student) {
      console.log(`[Answer Submission] ${student.name}: Q${data.questionNumber} -> Option ${data.selectedOption}`);
      
      // Broadcast answer event to admin/all
      io.emit('ANSWER_RECEIVED', {
        studentId: socket.id,
        studentName: student.name,
        questionNumber: data.questionNumber,
        selectedOption: data.selectedOption,
        timestamp: new Date()
      });
      
      if (callback) callback({ success: true });
    }
  });

  // Get connected clients (for admin dashboard)
  socket.on('GET_CLIENTS', async (callback) => {
    try {
      const response = await axios.get(`${JAVA_REST_API}/api/clients`);
      const clients = Array.from(connectedStudents.values());
      console.log(`[Admin Query] GET_CLIENTS: ${clients.length} connected`);
      
      if (callback) callback({ success: true, clients });
    } catch (error) {
      console.error('[Error] Failed to get clients:', error.message);
      const clients = Array.from(connectedStudents.values());
      if (callback) callback({ success: true, clients });
    }
  });

  // Broadcast Question to all students
  socket.on('BROADCAST_QUESTION', (data) => {
    console.log(`[Question Broadcast] Q${data.questionNumber}: "${data.questionText}"`);
    io.emit('QUESTION', data);
  });

  // Broadcast Results/Leaderboard
  socket.on('BROADCAST_RESULTS', (data) => {
    console.log('[Results Broadcast] Sending leaderboard to all clients');
    io.emit('LEADERBOARD', data);
  });

  // Disconnect handler
  socket.on('disconnect', () => {
    const student = connectedStudents.get(socket.id);
    if (student) {
      console.log(`[Disconnect] ${student.name} (${socket.id})`);
      connectedStudents.delete(socket.id);
      
      io.emit('STUDENT_DISCONNECTED', {
        studentId: socket.id,
        studentName: student.name,
        totalConnected: connectedStudents.size
      });
    } else {
      console.log(`[Disconnect] Client ${socket.id}`);
    }
  });

  // Generic event logger
  socket.on('error', (error) => {
    console.error(`[Socket Error] ${socket.id}:`, error);
  });
});

// Express route for health check
app.get('/health', (req, res) => {
  res.json({
    status: 'OK',
    timestamp: new Date(),
    connectedClients: connectedStudents.size,
    javaRestApi: JAVA_REST_API,
    javaSocket: `${JAVA_SOCKET_HOST}:${JAVA_SOCKET_PORT}`
  });
});

// Start server
server.listen(PORT, () => {
  console.log(`\n✓ Bridge server ready on http://localhost:${PORT}`);
  console.log('✓ Frontend will connect to http://localhost:3001\n');
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('\n\nShutting down bridge server...');
  io.close();
  server.close(() => {
    console.log('Bridge server stopped.');
    process.exit(0);
  });
});
