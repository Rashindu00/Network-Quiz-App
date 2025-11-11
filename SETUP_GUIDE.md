# 🚀 Complete Setup & Running Guide - Network Quiz Application

## Prerequisites

### Backend Requirements
- **Java JDK 11+** (download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.java.net/))
- **Maven 3.6+** (optional, for dependency management)
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code with Java extensions)

### Frontend Requirements
- **Node.js 14+** and **npm 6+**
- Modern web browser (Chrome, Firefox, Safari, Edge)

---

## 🔧 Backend Setup (Java)

### Step 1: Verify Java Installation

```bash
java -version
```

Expected output: `java 11+` or higher

### Step 2: Navigate to Backend Directory

```bash
cd backend/src/main/java
```

### Step 3: Compile Java Source Files

**Option A: Compile All Files** (Windows - PowerShell)
```powershell
javac -encoding UTF-8 `
  com/quizapp/model/*.java `
  com/quizapp/protocol/*.java `
  com/quizapp/server/*.java `
  com/quizapp/client/*.java `
  com/quizapp/question/*.java `
  com/quizapp/scoring/*.java `
  com/quizapp/result/*.java
```

**Option B: Compile All Files** (Linux/Mac - Bash)
```bash
find . -name "*.java" -type f | xargs javac -encoding UTF-8
```

### Step 4: Run the Quiz Server

```bash
java com.quizapp.server.QuizServer
```

**Expected Console Output:**
```
╔════════════════════════════════════════╗
║      QUIZ SERVER STARTED               ║
║      Port: 5000                        ║
║      Status: Listening for connections ║
╚════════════════════════════════════════╝

Waiting for client connection...
```

✅ **Server is now running and waiting for client connections!**

---

## 🌐 Frontend Setup (React)

### Step 1: Navigate to Frontend Directory

```bash
cd frontend
```

### Step 2: Install Dependencies

```bash
npm install
```

This installs all required packages including:
- `react` - UI library
- `react-dom` - React DOM rendering
- `socket.io-client` - WebSocket communication
- `tailwindcss` - CSS framework (if using)

### Step 3: Start Development Server

```bash
npm start
```

**Expected Console Output:**
```
webpack compiled successfully

On Your Network: http://192.168.x.x:3000/
```

The application will automatically open at `http://localhost:3000` in your default browser.

✅ **Frontend is now running!**

---

## 📱 Using the Application

### Admin Dashboard

1. **Select Admin Role** on the landing page
2. **View Connected Students** - Real-time list of students who joined
3. **Click "Start Quiz"** - Broadcasts questions to all connected students
4. **Monitor Progress** - Watch as students submit answers
5. **View Leaderboard** - See final rankings after quiz completes

### Student Interface

1. **Select Student Role** on the landing page
2. **Enter Your Name** in the login form
3. **Wait for Quiz to Start** - Admin will start the quiz
4. **Answer Questions** - Select answers and submit before timer expires
5. **View Your Score** - See results and ranking after quiz completion

---

## 🧪 Testing the Application

### Test Scenario: 3 Students with Admin

**Terminal 1: Start Server**
```bash
cd backend/src/main/java
java com.quizapp.server.QuizServer
```

**Terminal 2-4: Start Student Clients** (or use Browser)
```bash
# Student 1
java com.quizapp.client.QuizClient STU001 "Student One"

# Student 2
java com.quizapp.client.QuizClient STU002 "Student Two"

# Student 3
java com.quizapp.client.QuizClient STU003 "Student Three"
```

**Browser:**
1. Open `http://localhost:3000`
2. Open 4 browser windows/tabs
3. Window 1: Select Admin role
4. Windows 2-4: Select Student role and enter different names

---

## 🔍 Troubleshooting

### Issue: Port Already in Use (5000)

**Solution:**
Find and kill the process using port 5000:

**Windows (PowerShell):**
```powershell
Get-Process -Id (Get-NetTCPConnection -LocalPort 5000).OwningProcess | Stop-Process -Force
```

**Linux/Mac:**
```bash
lsof -ti:5000 | xargs kill -9
```

### Issue: Java Compilation Error

**Solution:** Ensure you're compiling from the correct directory:
```bash
# Correct
cd backend/src/main/java
javac com/quizapp/server/QuizServer.java

# Wrong - will fail
javac quizapp/server/QuizServer.java
```

### Issue: Frontend Won't Connect to Backend

**Solution:** Verify both services are running:
- Backend on `http://localhost:5000`
- Frontend on `http://localhost:3000`

Check browser console (F12) for connection errors.

### Issue: No Students Showing in Admin Dashboard

**Solution:**
1. Ensure students are actually connected (check server console)
2. Try refreshing the admin page
3. Check browser console for errors

---

## 📊 Architecture Overview

```
┌─────────────────────────────────────────────────────┐
│                 React Frontend                      │
│         (Admin Dashboard, Student Quiz, etc)        │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ Socket.IO
                   │ WebSocket
                   │
┌──────────────────▼──────────────────────────────────┐
│              Node.js/Express Server                 │
│         (REST API & Socket.IO Gateway)              │
└──────────────────┬──────────────────────────────────┘
                   │
                   │ TCP Sockets
                   │ ObjectStreams
                   │
┌──────────────────▼──────────────────────────────────┐
│           Java Quiz Server (Port 5000)              │
│                                                     │
│  ┌─────────────────────────────────────────────┐   │
│  │        Server & Client Management           │   │
│  │  - QuizServer.java                          │   │
│  │  - ClientHandler.java (Multithreading)      │   │
│  │  - ConnectedClientsManager.java             │   │
│  └─────────────────────────────────────────────┘   │
│                                                     │
│  ┌─────────────────────────────────────────────┐   │
│  │      Question & Answer Processing           │   │
│  │  - QuestionManager.java                     │   │
│  │  - QuestionBroadcaster.java                 │   │
│  │  - AnswerEvaluator.java                     │   │
│  └─────────────────────────────────────────────┘   │
│                                                     │
│  ┌─────────────────────────────────────────────┐   │
│  │     Scoring & Result Distribution           │   │
│  │  - ScoreManager.java                        │   │
│  │  - ResultBroadcaster.java (NIO)             │   │
│  └─────────────────────────────────────────────┘   │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## 🎯 Key Networking Concepts Demonstrated

### Member 1: ServerSocket & Multithreading
- ✅ Accepting multiple simultaneous connections
- ✅ Creating threads for each client
- ✅ Thread-safe client management

### Member 2: Question Broadcasting
- ✅ Object serialization and transmission
- ✅ Broadcasting to multiple clients
- ✅ Sequential message delivery

### Member 3: Client Socket Connection
- ✅ Socket connection establishment
- ✅ Input/Output stream handling
- ✅ Event-driven client programming

### Member 4: Thread-Safe Scoring
- ✅ Synchronized score updates
- ✅ Concurrent collections (ConcurrentHashMap)
- ✅ Thread communication

### Member 5: NIO Result Distribution
- ✅ Non-blocking I/O operations
- ✅ ExecutorService thread pools
- ✅ Broadcast communication

---

## 📚 File Structure Reference

```
Network-Quiz-App/
├── backend/
│   ├── src/main/java/com/quizapp/
│   │   ├── model/               (Data models)
│   │   ├── protocol/            (Communication protocol)
│   │   ├── server/              (Server components)
│   │   ├── client/              (Client components)
│   │   ├── question/            (Question management)
│   │   ├── scoring/             (Scoring system)
│   │   └── result/              (Result distribution)
│   └── questions.dat            (Serialized question file)
│
├── frontend/
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── App.js              (Main app component)
│   │   ├── App.css             (Main styles)
│   │   ├── components/         (React components)
│   │   │   ├── AdminDashboard/
│   │   │   ├── StudentQuiz/
│   │   │   ├── QuestionPanel/
│   │   │   ├── ResultBoard/
│   │   │   └── Leaderboard/
│   │   └── index.js
│   └── package.json
│
├── README.md                    (Project overview)
├── SETUP_GUIDE.md              (This file)
└── .gitignore
```

---

## 🚦 Quick Start Checklist

- [ ] Java JDK 11+ installed and verified
- [ ] Backend code in `/backend/src/main/java`
- [ ] Frontend code in `/frontend`
- [ ] Java files compiled successfully
- [ ] Quiz Server running on port 5000
- [ ] Node.js and npm installed
- [ ] Frontend dependencies installed (`npm install`)
- [ ] Frontend running on `http://localhost:3000`
- [ ] Browser can reach both frontend and backend
- [ ] Multiple browser tabs/windows open for testing
- [ ] Admin selects role first, then students
- [ ] Students can connect before admin starts quiz

---

## 📞 Getting Help

If you encounter issues:

1. **Check Console Output** - Both Java and browser consoles show errors
2. **Verify Ports** - Ensure 5000 (backend) and 3000 (frontend) are free
3. **Check Firewall** - May be blocking local connections
4. **Verify Java Version** - Must be 11 or higher
5. **Clear Browser Cache** - Sometimes helps with frontend issues

---

## 🎓 Learning Resources

- [Java Networking Guide](https://docs.oracle.com/javase/tutorial/networking/index.html)
- [Multithreading in Java](https://docs.oracle.com/javase/tutorial/essential/concurrency/)
- [Java NIO](https://docs.oracle.com/javase/tutorial/nio/index.html)
- [React Documentation](https://react.dev)
- [Socket.IO Documentation](https://socket.io/docs/)

---

**Happy Testing! 🎉**
