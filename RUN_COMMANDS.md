# 🚀 Complete Commands - Network Quiz App

## COPY-PASTE READY COMMANDS

---

## ✅ TERMINAL 1: Start Java Backend

```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App\backend
$files = Get-ChildItem -Path src\main\java\com\quizapp -Include *.java -Recurse | Where-Object { $_.Name -ne 'TestClient.java' } | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d .\target\classes $files
java -cp .\target\classes com.quizapp.server.QuizServer
```

**Keep this terminal open** ✋

---

## ✅ TERMINAL 2: Start Socket.IO Bridge

```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App
node .\server.js
```

**Keep this terminal open** ✋

---

## ✅ TERMINAL 3: Start React Frontend

```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App\frontend
npm start
```

**Browser will open automatically to http://localhost:3000** ✋

---

## 🎮 USAGE - What to do in Browser

### Step 1: Admin Starts Quiz
1. Go to http://localhost:3000
2. Click **[Admin]** button
3. Click **[START QUIZ]** button
4. Wait for confirmation

### Step 2: Open Student Windows (2-3 times)
In new browser tabs/windows, repeat:
1. Go to http://localhost:3000
2. Click **[Student]** button
3. Type name (e.g., "Alice", "Bob", "Charlie")
4. Press Enter or click **Login**
5. See questions appear with 30-second timer
6. Click one option (A, B, C, or D)
7. Click **[SUBMIT]** button
8. Wait for next question

### Step 3: View Results
After all questions:
1. See your score on **Result Board**
2. Click **[VIEW LEADERBOARD]**
3. See final rankings and statistics

---

## 📊 Expected Output

### Terminal 1 (Backend) - Should show:
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

### Terminal 2 (Bridge) - Should show:
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

### Terminal 3 (Frontend) - Should show:
```
Compiled successfully!

You can now view network-quiz-app-frontend in the browser.

  Local:            http://localhost:3000
```

---

## 🔧 If Something Goes Wrong

### Error: "Address already in use: bind"
```powershell
# Find what's using port 5000
netstat -ano | Select-String ":5000"

# Kill it (replace PID with number from above)
taskkill /PID <PID> /F
```

### Frontend stuck on "Connecting..."
- Check all 3 terminals are running
- If not, kill all and restart from Terminal 1

### "Cannot find module" errors
```powershell
cd C:\Users\Weditha\Desktop\Network\Network-Quiz-App
npm install
```

---

## 📱 Simple Test Flow

1. **Terminal 1:** Copy-paste backend commands → Hit Enter → Wait for "Waiting for students..."
2. **Terminal 2:** Copy-paste bridge commands → Hit Enter → Wait for "Bridge server ready..."
3. **Terminal 3:** Copy-paste frontend commands → Hit Enter → Browser opens
4. **Browser Tab 1:** Click Admin → Click "Start Quiz"
5. **Browser Tab 2:** Click Student → Type "Alice" → Submit → Answer question → Submit
6. **Browser Tab 3:** Click Student → Type "Bob" → Submit → Answer question → Submit
7. **Either tab:** After all questions → See score → Click "View Leaderboard"

✅ **Done!**

---

## 🎯 What You'll See

```
Admin View:
- Connected Students: 2
- Quiz Status: Running
- Questions Sent: 1 / 10

Student View:
Q1: What is TCP/IP?
⭕ A) Transport Control Protocol
⭕ B) Tech Control Process
⭕ C) Transfer Connection Protocol
⭕ D) Transmission Communication Process

Time: ⏱️ 25 seconds remaining

[SELECT A-D] [SUBMIT]

Results:
Your Score: 7/10 (70%)
Grade: C

Leaderboard:
🥇 Alice: 9/10 (90%)
🥈 Bob: 7/10 (70%)
```

---

**That's it! Just copy-paste the commands above and you're ready to go! 🎉**
