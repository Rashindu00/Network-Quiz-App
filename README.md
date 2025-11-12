# 🎯 Network Quiz Application - All 5 Members Integrated

## Overview

Complete network programming quiz application demonstrating **all 5 team members' contributions** working together in a fully integrated system.

---

## 🚀 Quick Start - 3 Ways to Run!

### **Method 1: Complete System (Recommended)** ⭐

```powershell
# Start Backend + Frontend + Test Clients
.\start-quiz-system.ps1
# Select: Option 1 (Full System)
```

**Access:**
- Admin Dashboard: http://localhost:3000
- Backend API: http://localhost:8080

---

### **Method 2: Backend Only (Quiz Testing)**

```powershell
# Navigate to backend
cd backend

# Run integrated quiz system
.\start-integrated-quiz.ps1
```

**What happens:**
- Server starts on port 8080
- 3 test clients auto-launch
- Ready to test quiz flow

---

### **Method 3: Frontend Only (Dashboard Development)**

```powershell
# Start React admin dashboard
.\start-frontend.ps1
```

**What happens:**
- React dev server starts
- Browser opens at http://localhost:3000
- Hot reload enabled

---

**📖 For detailed instructions, see:**
- [COMPLETE_USAGE_GUIDE.md](COMPLETE_USAGE_GUIDE.md) - Full documentation
- [SCRIPTS_GUIDE.md](SCRIPTS_GUIDE.md) - All available scripts

---

## ✅ What's Included

### All 5 Members Working Together:

| Member | Contribution | Files | Status |
|--------|--------------|-------|--------|
| **Member 1** | Server Setup<br>Client Management | `IntegratedQuizServer.java`<br>`IntegratedClientHandler.java`<br>`IntegratedClientsManager.java` | ✅ Integrated |
| **Member 2** | Quiz Questions<br>Question Management | `QuizManager.java`<br>`Quiz.java`<br>`questions.txt` (20+ questions) | ✅ Integrated |
| **Member 3** | Answer Collection<br>Answer Validation | `AnswerCollector.java`<br>`AnswerValidator.java` | ✅ Integrated |
| **Member 4** | Score Tracking<br>Leaderboard System | `ScoreManager.java`<br>`Leaderboard.java` | ✅ Integrated |
| **Member 5** | Results Generation<br>Statistics Analysis | `ResultsGenerator.java`<br>`QuizStatistics.java` | ✅ Integrated |

---

## 📁 Project Structure

```
Network-Quiz-App/
├── backend/
│   ├── start-integrated-quiz.ps1          ⭐ START HERE!
│   ├── INTEGRATED_USAGE_GUIDE.md           (Detailed English guide)
│   ├── RUN_කරන්න_කොහොමද.md                (සිංහල උපදෙස්)
│   ├── compile-all.ps1
│   └── src/main/java/com/quizapp/
│       ├── server/                         (Member 1)
│       │   ├── IntegratedQuizServer.java   ← Main server (ALL MEMBERS)
│       │   ├── IntegratedClientHandler.java
│       │   └── IntegratedClientsManager.java
│       ├── quiz/                           (Member 2)
│       │   ├── QuizManager.java
│       │   ├── Quiz.java
│       │   └── questions.txt
│       ├── answer/                         (Member 3)
│       │   ├── AnswerCollector.java
│       │   └── AnswerValidator.java
│       ├── score/                          (Member 4)
│       │   ├── ScoreManager.java
│       │   └── Leaderboard.java
│       ├── results/                        (Member 5)
│       │   ├── ResultsGenerator.java
│       │   └── QuizStatistics.java
│       └── client/
│           └── IntegratedTestClient.java   ← Test client
└── frontend/                               (React UI - Optional)
    └── src/components/
        ├── AdminDashboard.jsx
        ├── ScoreboardUI.jsx
        └── ResultsDisplay.jsx
```

---

## 📖 Documentation

| File | Description |
|------|-------------|
| `INTEGRATED_USAGE_GUIDE.md` | Complete English documentation |
| `RUN_කරන්න_කොහොමද.md` | සිංහල උපදෙස් (Sinhala instructions) |

---

## 🐛 Troubleshooting

### Port 8080 Already in Use?
```powershell
Get-Process | Where-Object {$_.Name -like "*java*"} | Stop-Process -Force
```

### Compilation Errors?
```powershell
cd backend
.\compile-all.ps1
```

---

## 🏆 Status

**Status:** ✅ **COMPLETE & INTEGRATED**  
**All 5 Members:** Working Together  

**Happy Quizzing! 🎓** - Member 1 Implementation


## 👤 Member 1 - Implementation Details

### Backend Components (Java)

#### 1. **QuizServer.java** 
- **Network Concepts**: `ServerSocket`, `Socket`, `Multithreading`, `Thread Pool`
- **Functionality**:
  - Initializes ServerSocket on port 8080
  - Accepts multiple client connections
  - Creates separate threads for each client
  - Manages quiz state and lifecycle
  - Broadcasts messages to all clients

#### 2. **ClientHandler.java**
- **Network Concepts**: `Thread`, `Socket Communication`, `InputStream/OutputStream`
- **Functionality**:
  - Runs in a separate thread per client
  - Handles client registration
  - Manages bidirectional communication
  - Processes client messages
  - Handles disconnections gracefully

#### 3. **ConnectedClientsManager.java**
- **Network Concepts**: `Thread-safe Collections`, `Synchronization`
- **Functionality**:
  - Maintains list of all connected clients
  - Thread-safe add/remove operations
  - Broadcasts messages to all/specific clients
  - Provides client statistics
  - Manages concurrent access

### Frontend Component (React)

#### **AdminDashboard.jsx**
- **Features**:
  - Real-time display of connected students
  - Start Quiz button with validation
  - Connection statistics dashboard
  - Server status monitoring
  - Responsive design

## 🚀 Setup Instructions

### Backend (Java)

#### Prerequisites
- Java JDK 11 or higher
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
