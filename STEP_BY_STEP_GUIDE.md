# 📖 Step-by-Step Guide: Network Quiz App

## Overview
This guide will walk you through running the entire project from scratch. You'll need **3 terminal windows** to run:
1. Java Backend (ports 5000 + 8080)
2. Node.js Bridge Server (port 3001)
3. React Frontend (port 3000)

---

## ⚙️ Step 1: Start Java Backend

### What it does:
- Starts the Quiz Server that handles all students
- REST API server for admin dashboard
- Broadcasts questions to students
- Handles answer submissions and scoring

### Commands:
```powershell
# Open PowerShell Terminal #1
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App\backend

# Compile Java files (if not already done)
$files = Get-ChildItem -Path src\main\java\com\quizapp -Include *.java -Recurse | Where-Object { $_.Name -ne 'TestClient.java' } | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d .\target\classes $files

# Start the backend server
java -cp .\target\classes com.quizapp.server.QuizServer
```

### Expected Output:
```
✓ REST API Server started on port: 8080
  GET  http://localhost:8080/api/clients
  POST http://localhost:8080/api/quiz/start

╔════════════════════════════════════════╗
║   Quiz Server Started Successfully!   ║
╚════════════════════════════════════════╝
Server is listening on port: 5000
Waiting for students to connect...
```

✅ **Leave this terminal running. Do NOT close it.**

---

## 🌉 Step 2: Start Socket.IO Bridge Server

### What it does:
- Acts as a bridge between React frontend (Socket.IO) and Java backend
- Handles real-time communication
- Broadcasts questions, answers, and results

### Commands:
```powershell
# Open PowerShell Terminal #2
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App

# Start the bridge server
node .\server.js
```

### Expected Output:
```
╔════════════════════════════════════════╗
║     Socket.IO Bridge Server Started    ║
╚════════════════════════════════════════╝
✓ Socket.IO listening on port: 3001
✓ Java REST API: http://localhost:8080
✓ Java Socket: localhost:5000

✓ Bridge server ready on http://localhost:3001
✓ Frontend will connect to http://localhost:3001
```

✅ **Leave this terminal running. Do NOT close it.**

---

## 💻 Step 3: Start React Frontend

### What it does:
- Serves the React web application
- Compiles TypeScript/JSX to JavaScript
- Hot-reloads when you make changes

### Commands:
```powershell
# Open PowerShell Terminal #3
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App\frontend

# Start the React dev server
npm start
```

### Expected Output:
```
Compiled successfully!

You can now view network-quiz-app-frontend in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://<your-ip>:3000

Note that the development build is not optimized.
To create a production build, use npm run build.

webpack compiled...
```

✅ **Your browser should automatically open http://localhost:3000**

---

## 🎮 Step 4: Open Browser & Select Role

### What you'll see:
A role selection screen with two options: **Admin** and **Student**

### First Time Setup:
Open **http://localhost:3000** in your browser (if it didn't auto-open).

You should see:
```
╔════════════════════════════════╗
║     NETWORK QUIZ APPLICATION   ║
║                                ║
║  Select Your Role              ║
╠════════════════════════════════╣
║                                ║
║  [Admin Dashboard]  [Student]  ║
║                                ║
╚════════════════════════════════╝
```

### What to do:
**Choose Admin first** to start the quiz.

---

## 👨‍💼 Step 5: Admin - Start Quiz

### After clicking Admin, you'll see:

```
╔════════════════════════════════════════╗
║          ADMIN DASHBOARD               ║
╠════════════════════════════════════════╣
║ Status: Connected ✓                    ║
║ Connected Students: 0                  ║
║ Quiz Status: Not Started               ║
║                                        ║
║ [START QUIZ]  [STOP QUIZ]              ║
╚════════════════════════════════════════╝
```

### Actions:
1. Click **[START QUIZ]** button
2. Wait for confirmation message
3. You should see the status change to "Quiz Started"
4. Questions will begin broadcasting to any connected students

### What happens in Terminal #1 (Backend):
You'll see logs like:
```
Question 1 sent: "What is TCP/IP?"
Broadcasting to X students...
```

---

## 👨‍🎓 Step 6: Students - Login & Answer Questions

### Open new browser windows/tabs as students:

**For each student:**
1. Open a new browser tab (or separate browser window)
2. Go to **http://localhost:3000**
3. Click **[Student]** button
4. Enter your name (e.g., "Alice", "Bob", "Charlie")
5. Click **Login** or press Enter

### Student Quiz Interface:
```
╔════════════════════════════════════════╗
║        QUIZ - Question 1 of 10         ║
╠════════════════════════════════════════╣
║ What is TCP/IP?                        ║
║                                        ║
║ ⭕ Option A: Transport Control...     ║
║ ⭕ Option B: Tech Control...          ║
║ ⭕ Option C: Transfer Connection...   ║
║ ⭕ Option D: Transmission...          ║
║                                        ║
║ Time Remaining: ⏱️ 29 seconds         ║
║                                        ║
║ [SELECT ANSWER]  [SUBMIT]             ║
╚════════════════════════════════════════╝
```

### How to Answer:
1. Click one of the options (A, B, C, or D)
2. Click **[SUBMIT]** button
3. Wait for next question (or see "Waiting for next question..." message)

### What Admin sees:
In Terminal #1, you'll see:
```
Answer Submission: Alice answered Q1 -> Option A
Answer Submission: Bob answered Q1 -> Option C
```

### Open multiple student windows:
Repeat steps 1-5 in different browser tabs/windows to simulate multiple students:
- Open Tab 2: Student "Alice" 
- Open Tab 3: Student "Bob"
- Open Tab 4: Student "Charlie"

---

## 🏆 Step 7: View Results & Leaderboard

### After all questions are answered:

Each student will see the **Result Board**:
```
╔════════════════════════════════════════╗
║            YOUR SCORE                  ║
╠════════════════════════════════════════╣
║                                        ║
║          Grade: A                      ║
║          Score: 85 / 100               ║
║          Percentage: 85%               ║
║                                        ║
║  [VIEW LEADERBOARD]                    ║
║                                        ║
╚════════════════════════════════════════╝
```

### Click [VIEW LEADERBOARD]:
```
╔════════════════════════════════════════╗
║         LEADERBOARD - Rankings         ║
╠════════════════════════════════════════╣
║                                        ║
║  🥇 1st - Alice     : 90 / 100 (90%)  ║
║  🥈 2nd - Charlie   : 85 / 100 (85%)  ║
║  🥉 3rd - Bob       : 70 / 100 (70%)  ║
║                                        ║
║ Statistics:                            ║
║ • Participants: 3                      ║
║ • Average Score: 81.67                 ║
║ • Highest: 90                          ║
║ • Lowest: 70                           ║
║                                        ║
╚════════════════════════════════════════╝
```

---

## 🔄 Complete User Flow Summary

```
1. Start Backend (Terminal 1)
   ↓
2. Start Bridge (Terminal 2)
   ↓
3. Start Frontend (Terminal 3)
   ↓
4. Open Browser → http://localhost:3000
   ↓
5. Select ADMIN
   ↓
6. Click [START QUIZ]
   ↓
7. (Repeat in new browser tabs) Select STUDENT
   ↓
8. Login with Name
   ↓
9. See Questions & Answer
   ↓
10. Submit Answers
    ↓
11. View Individual Score
    ↓
12. View Leaderboard
```

---

## 📱 Testing with Multiple Students

### Recommended Setup:
- **Window 1:** Admin (to start quiz and monitor)
- **Window 2-4:** Three students (Alice, Bob, Charlie)

### Example Timeline:
```
T=0s    Admin clicks START QUIZ
T=1s    Question 1 appears in all student windows
T=5s    Alice answers and submits
T=8s    Bob answers and submits
T=15s   Charlie answers and submits (auto-submit after 30s)
T=35s   Question 2 appears
...
T=5m    Last question answered
T=5:10s Results shown to all students
T=5:15s Leaderboard displayed
```

---

## 🛠️ Troubleshooting

### Problem: "Address already in use: bind"
```powershell
# Check what's using the port
netstat -ano | Select-String ":5000"

# Kill the process (replace 12345 with PID)
taskkill /PID 12345 /F
```

### Problem: Frontend shows "Connecting..." forever
**Check:**
1. ✓ Terminal 1: Java backend is running (shows "Waiting for students...")
2. ✓ Terminal 2: Bridge server is running (shows "Socket.IO listening on port: 3001")
3. ✓ Terminal 3: React is running (shows "Compiled successfully!")

### Problem: "Cannot find module 'express'"
```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App
npm install
```

### Problem: Java compilation errors
```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App\backend

# Try recompiling with explicit file list
javac -encoding UTF-8 -d .\target\classes src/main/java/com/quizapp/**/*.java
```

---

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                      WEB BROWSER                            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │   React App (Port 3000)                              │  │
│  │  - AdminDashboard                                    │  │
│  │  - StudentQuiz                                       │  │
│  │  - ResultBoard                                       │  │
│  │  - Leaderboard                                       │  │
│  └───────────────────────────────────────────────────────┘  │
└──────────────────────────┬──────────────────────────────────┘
                           │ Socket.IO Client
                           ↓
┌──────────────────────────────────────────────────────────────┐
│    Node.js Bridge Server (Port 3001)                         │
│  - Listens for Socket.IO connections                        │
│  - Relays events to Java backend                            │
│  - Broadcasts questions/answers/results                     │
└──────────────────────┬───────────────────────────────────────┘
                       │ HTTP REST API + Raw Java Sockets
                       ↓
┌──────────────────────────────────────────────────────────────┐
│         Java Backend (Ports 5000 + 8080)                     │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ QuizServer (Member 1)                                 │  │
│  │ - ServerSocket on port 5000                           │  │
│  │ - REST API on port 8080                               │  │
│  │ - Multi-threaded client handlers                      │  │
│  └────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ QuestionBroadcaster (Member 2)                        │  │
│  │ - Sends questions sequentially                        │  │
│  │ - 30-second timer per question                        │  │
│  └────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ AnswerEvaluator (Member 4)                            │  │
│  │ - Evaluates answers (thread-safe)                     │  │
│  │ - Calculates scores                                   │  │
│  └────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │ ResultBroadcaster (Member 5)                          │  │
│  │ - Non-blocking distribution (NIO)                     │  │
│  │ - Generates leaderboard                               │  │
│  └────────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────┘
```

---

## 🎓 Learning Points

### Networking Concepts Demonstrated:
- **ServerSocket:** Listening for multiple client connections
- **Multi-threading:** Each client in a separate thread
- **Object Serialization:** Sending complex objects between processes
- **Thread Safety:** Synchronized blocks in scoring
- **NIO:** Non-blocking I/O for result broadcasting
- **REST API:** HTTP endpoints for admin dashboard
- **Real-time Communication:** Socket.IO for live updates

### Code Organization (By Team Member):
- **Member 1:** Server connection management
- **Member 2:** Question broadcasting logic
- **Member 3:** Student client socket handling
- **Member 4:** Scoring and evaluation
- **Member 5:** Result distribution and leaderboard

---

## ✅ Quick Checklist

Before starting:
- [ ] 3 PowerShell terminals ready
- [ ] Internet connection (for npm packages)
- [ ] 2+ Browser windows available

Starting the app:
- [ ] Terminal 1: Backend compiles and runs (shows "Waiting for students...")
- [ ] Terminal 2: Bridge starts (shows "Socket.IO listening on port: 3001")
- [ ] Terminal 3: React runs (shows "Compiled successfully!")
- [ ] Browser opens to http://localhost:3000

Testing:
- [ ] Admin can start quiz
- [ ] Students can login
- [ ] Questions appear in student windows
- [ ] Students can submit answers
- [ ] Results show on result board
- [ ] Leaderboard displays rankings

---

**Ready? Start with Terminal 1! 🚀**
