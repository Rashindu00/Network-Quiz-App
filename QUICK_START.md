# 🚀 Quick Start Guide - Network Quiz App

## 3-Terminal Setup (Recommended for Development)

Open **3 separate terminal windows/PowerShell** and run these commands in order:

### Terminal 1: Start Java Backend (ports 5000 + 8080)
```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App\backend
java -cp .\target\classes com.quizapp.server.QuizServer
```
**Expected output:**
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

### Terminal 2: Start Socket.IO Bridge (port 3001)
```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App
node .\server.js
```
**Expected output:**
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

### Terminal 3: Start React Frontend (port 3000)
```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App\frontend
npm start
```
**Expected output:**
```
Compiled successfully!

You can now view network-quiz-app-frontend in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://<your-ip>:3000

Note that the development build is not optimized.
To create a production build, use npm run build.
```

---

## 🎮 Testing the App

1. **Open http://localhost:3000 in your browser** — you should see the **Role Selection** page
2. **In one browser tab/window:**
   - Click **"Admin"** → you'll see the Admin Dashboard with a "Start Quiz" button
   - Click **"Start Quiz"** to begin
3. **In 2–3 other browser tabs/windows:**
   - Click **"Student"** → login with any name (e.g., "Alice", "Bob")
   - You should see questions appear and a countdown timer
   - Answer questions and submit
4. **At the end:**
   - View your individual score and the leaderboard
   - Admin dashboard shows connected students and scores

---

## 🔍 Architecture

```
Browser (React Frontend, port 3000)
         ↓ Socket.IO
Node Bridge Server (port 3001)
         ↓ REST API / Java Sockets
Java Backend (ports 8080 + 5000)
         ↓ Multi-threading & NIO
Quiz Events, Answers, Results
```

---

## 🛠️ Troubleshooting

### "Address already in use" error on ports 5000 or 8080
```powershell
# Find process using the port (example: 5000)
netstat -ano | Select-String ":5000"

# Kill the process (replace PID with the process ID found above)
taskkill /PID <PID> /F
```

### Frontend shows "Connecting to Quiz Server..." but doesn't connect
- Verify Terminal 1 (Java backend) is running and shows "Server is listening on port: 5000"
- Verify Terminal 2 (Bridge server) is running and shows "Socket.IO listening on port: 3001"
- Check browser console (F12 → Console tab) for errors

### "Cannot find module 'express'" or similar
Make sure you ran `npm install` in the project root:
```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App
npm install
```

---

## 📚 Project Structure

```
Network-Quiz-App/
├── backend/                     # Java Backend (Multithreading, NIO)
│   ├── src/main/java/com/quizapp/
│   │   ├── server/              # Member 1: Server & Connections
│   │   ├── question/            # Member 2: Question Broadcasting
│   │   ├── client/              # Member 3: Student Client
│   │   ├── scoring/             # Member 4: Answer Evaluation
│   │   ├── result/              # Member 5: Result Distribution
│   │   ├── model/               # Data Models
│   │   └── protocol/            # Message Protocol
│   └── target/classes/          # Compiled .class files
│
├── frontend/                    # React Frontend (port 3000)
│   ├── src/
│   │   ├── App.js               # Main component (Socket.IO client)
│   │   ├── components/
│   │   │   ├── AdminDashboard.jsx   # Member 1: Admin view
│   │   │   ├── QuestionPanel.jsx    # Member 2: Question display + timer
│   │   │   ├── StudentQuiz.jsx      # Member 3: Student login & quiz
│   │   │   ├── ResultBoard.jsx      # Member 4: Score display
│   │   │   └── Leaderboard.jsx      # Member 5: Rankings
│   │   └── [CSS files]
│   └── package.json
│
├── server.js                    # Socket.IO Bridge Server (port 3001)
├── package.json                 # Root dependencies
└── [Documentation files]
```

---

## 🎓 Learning Outcomes

This project demonstrates:
- **Java Networking:** ServerSocket, multi-threading, Object serialization
- **Web Development:** React hooks, real-time communication via Socket.IO
- **System Design:** Client-Server architecture, message broadcasting, thread-safe operations
- **Team Collaboration:** Member-wise task separation (5 backend + 5 frontend components)

---

## 📝 Member Assignments

### Backend (Java)
- **Member 1:** Server setup, client connections, thread management
- **Member 2:** Question management and broadcasting
- **Member 3:** Client socket handler
- **Member 4:** Answer evaluation and scoring (synchronized)
- **Member 5:** Result distribution using NIO

### Frontend (React)
- **Member 1:** Admin Dashboard and server connection
- **Member 2:** Question Panel with timer
- **Member 3:** Student login and quiz interface
- **Member 4:** Result/Score display
- **Member 5:** Leaderboard and rankings

---

**Enjoy! 🎉**
