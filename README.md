# Network Quiz Application - Complete Member-Wise Implementation

A comprehensive networked quiz application demonstrating Java networking concepts, multithreading, and real-time communication with a modern React frontend.

## � Project Overview

This is a complete implementation of a Network Quiz Platform with member-wise task distribution covering all essential networking concepts.

### Key Features
- ✅ Multi-client server with thread management
- ✅ Real-time question broadcasting
- ✅ Socket-based client connections
- ✅ Thread-safe scoring system
- ✅ NIO-based result distribution
- ✅ Modern React admin and student dashboards
- ✅ Live leaderboard with rankings

---

## 👥 Member-Wise Task Distribution

### **Member 1: Server Setup & Client Connection Management**

**Backend (Java)**
- `QuizServer.java` - Main server orchestrator with ServerSocket
- `ClientHandler.java` - Individual thread per client
- `ConnectedClientsManager.java` - Thread-safe client collection management

**Frontend (React)**
- `AdminDashboard.jsx` - Admin interface showing connected students

**Networking Concepts**
- ServerSocket for accepting connections
- Socket communication
- Multithreading (Thread per client)
- Concurrent collections

**Location**: `/backend/src/main/java/com/quizapp/server/`

---

### **Member 2: Question Broadcasting System**

**Backend (Java)**
- `QuestionManager.java` - Stores and manages quiz questions
- `QuestionBroadcaster.java` - Broadcasts questions to all clients
- `Message.java` - Message protocol for communication

**Frontend (React)**
- `QuestionPanel.jsx` - Displays questions with timer and options

**Networking Concepts**
- ObjectOutputStream/InputStream
- Serialization
- Broadcasting to multiple clients
- Thread-based question delivery

**Location**: `/backend/src/main/java/com/quizapp/question/`

---

### **Member 3: Student Client Handler**

**Backend (Java)**
- `QuizClient.java` - Client application for students to connect

**Frontend (React)**
- `StudentQuiz.jsx` - Student login and quiz interface

**Networking Concepts**
- Socket connections
- InputStream/OutputStream
- Client-server handshake
- Event listeners

**Location**: `/backend/src/main/java/com/quizapp/client/`

---

### **Member 4: Answer Evaluation & Scoring**

**Backend (Java)**
- `AnswerEvaluator.java` - Evaluates answers with thread safety
- `ScoreManager.java` - Manages scores and rankings
- `Answer.java` - Answer model
- `Score.java` - Score model

**Frontend (React)**
- `ResultBoard.jsx` - Displays individual student scores

**Networking Concepts**
- Synchronized blocks
- Thread-safe collections (ConcurrentHashMap)
- Thread communication
- Data consistency

**Location**: `/backend/src/main/java/com/quizapp/scoring/`

---

### **Member 5: Result Distribution & Leaderboard**

**Backend (Java)**
- `ResultBroadcaster.java` - Non-blocking result distribution with NIO
- Thread pool for concurrent result sending

**Frontend (React)**
- `Leaderboard.jsx` - Final leaderboard with rankings

**Networking Concepts**
- Java NIO channels
- Non-blocking operations
- ExecutorService for thread pools
- Broadcast communication

**Location**: `/backend/src/main/java/com/quizapp/result/`

---

## 🏗️ Project Structure

```
Network-Quiz-App/
├── backend/
│   └── src/main/java/com/quizapp/
│       ├── model/
│       │   ├── QuizQuestion.java
│       │   ├── StudentInfo.java
│       │   ├── Answer.java
│       │   └── Score.java
│       ├── protocol/
│       │   └── Message.java
│       ├── server/
│       │   ├── QuizServer.java
│       │   ├── ClientHandler.java
│       │   ├── ConnectedClientsManager.java
│       │   └── RestApiServer.java
│       ├── client/
│       │   ├── QuizClient.java
│       │   └── TestClient.java
│       ├── question/
│       │   ├── QuestionManager.java
│       │   └── QuestionBroadcaster.java
│       ├── scoring/
│       │   ├── AnswerEvaluator.java
│       │   └── ScoreManager.java
│       └── result/
│           └── ResultBroadcaster.java
├── frontend/
│   ├── src/
│   │   ├── App.js
│   │   ├── App.css
│   │   ├── components/
│   │   │   ├── AdminDashboard.jsx
│   │   │   ├── AdminDashboard.css
│   │   │   ├── StudentQuiz.jsx
│   │   │   ├── StudentQuiz.css
│   │   │   ├── QuestionPanel.jsx
│   │   │   ├── QuestionPanel.css
│   │   │   ├── ResultBoard.jsx
│   │   │   ├── ResultBoard.css
│   │   │   ├── Leaderboard.jsx
│   │   │   └── Leaderboard.css
│   │   └── index.js
│   └── package.json
└── README.md

- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code)

#### Running the Server

1. **Compile the Java files**:
```bash
cd backend/src/main/java

javac -encoding UTF-8 com\quizapp\server\QuizServer.java com\quizapp\server\ClientHandler.java com\quizapp\server\ConnectedClientsManager.java com\quizapp\client\TestClient.java


```

2. **Run the server**:
```bash
java com.quizapp.server.QuizServer
```

The server will start on port `8080` and wait for client connections.

### Frontend (React)

#### Prerequisites
- Node.js 14 or higher
- npm or yarn

#### Running the Frontend

1. **Install dependencies**:
```bash
cd frontend
npm install
```

2. **Start the development server**:
```bash
npm start
```

The application will open at `http://localhost:3000`
