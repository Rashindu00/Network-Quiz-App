# 🎮 Network Quiz App - Complete Usage Guide

## 🚀 Quick Start (Easiest Way)

### **Method 1: Complete System (Recommended)**

Open PowerShell in project root and run:

```powershell
.\start-quiz-system.ps1
```

Select **Option 1** for Full System

This will:
- ✅ Start Backend Server (Port 8080)
- ✅ Start Frontend Dashboard (Port 3000)
- ✅ Launch 3 Test Clients
- ✅ Open browser automatically

---

## 📖 Detailed Instructions

### **Step-by-Step: Running Complete System**

**1. Open PowerShell**
```powershell
# Navigate to project
cd "C:\Users\Rashindu\Desktop\GitHub Projects\Network-Quiz-App"
```

**2. Run Master Script**
```powershell
.\start-quiz-system.ps1
```

**3. Select Option**
```
SELECT COMPONENTS TO START
1. Backend + Frontend (Full System)    ← Select this
2. Backend Only
3. Frontend Only
4. Exit

Enter your choice (1-4): 1
```

**4. Wait for Initialization**
- Backend window opens → Server starts → 3 clients launch
- Frontend window opens → npm starts → Browser opens

**5. Access Applications**
- **Admin Dashboard:** http://localhost:3000
- **Backend API:** http://localhost:8080
- **Test Clients:** 3 separate windows

---

### **Method 2: Backend Only (Testing)**

**For Quiz Server Testing:**

```powershell
cd backend
.\start-integrated-quiz.ps1
```

**When prompted:**
```
Do you want to auto-launch server and 3 clients? (y/n): y
```

**What Happens:**
1. ✅ Compiles all Java files
2. ✅ Verifies 5 members' components
3. ✅ Starts server on port 8080
4. ✅ Opens 4 windows:
   - 1 Server window
   - 3 Client windows

**Testing the Quiz:**
- In each client window, enter a student name
- After 3rd name entered, quiz auto-starts
- Answer questions using A/B/C/D
- View results and leaderboard

---

### **Method 3: Frontend Only (UI Development)**

**For Dashboard Development:**

```powershell
.\start-frontend.ps1
```

**What Happens:**
1. ✅ Checks Node.js installation
2. ✅ Installs npm packages (if needed)
3. ✅ Starts React dev server
4. ✅ Opens http://localhost:3000
5. ✅ Hot reload enabled

**Development Tips:**
- Edit files in `frontend/src/`
- Browser auto-reloads on save
- Check console for errors
- Use React DevTools

---

## 🎯 Usage Scenarios

### **Scenario 1: Presentation/Demo**

```powershell
# 1. Start complete system
.\start-quiz-system.ps1
# Select: 1 (Full System)

# 2. Wait 10 seconds

# 3. Show Admin Dashboard
# Open: http://localhost:3000

# 4. Show Quiz Flow
# Use the 3 client windows to demonstrate quiz
```

---

### **Scenario 2: Backend Testing**

```powershell
# 1. Start backend only
cd backend
.\start-integrated-quiz.ps1
# Answer: y (to launch clients)

# 2. Test quiz flow in client windows
# Client 1: Enter "Kamal"
# Client 2: Enter "Nimal"
# Client 3: Enter "Sunil"
# Quiz auto-starts after 3rd name

# 3. Answer questions
# Use A/B/C/D in each window

# 4. View results
# Check leaderboard and statistics
```

---

### **Scenario 3: Frontend Development**

```powershell
# 1. Start frontend only
.\start-frontend.ps1

# 2. Make code changes
# Edit files in frontend/src/

# 3. View changes
# Browser auto-reloads

# 4. Test without backend
# Use mock data for development
```

---

### **Scenario 4: Full System Testing**

```powershell
# 1. Start full system
.\start-quiz-system.ps1
# Select: 1

# 2. Test frontend-backend integration
# Admin Dashboard → http://localhost:3000
# API Endpoint → http://localhost:8080

# 3. Test quiz flow
# Use test clients to join quiz
# Monitor in dashboard

# 4. Verify data flow
# Check scores in dashboard
# Verify leaderboard updates
```

---

## 🛠️ Manual Setup (Alternative)

### **Backend Manual Start**

**Terminal 1 - Server:**
```powershell
cd backend/src/main/java
javac com/quizapp/**/*.java
java com.quizapp.server.IntegratedQuizServer
```

**Terminal 2-4 - Clients:**
```powershell
cd backend/src/main/java
java com.quizapp.client.IntegratedTestClient
```

---

### **Frontend Manual Start**

**Terminal:**
```powershell
cd frontend
npm install       # First time only
npm start
```

---

## 📊 Monitoring & Debugging

### **Check Backend Status**

```powershell
# View server logs
# Check server PowerShell window for:
[READY] INTEGRATED QUIZ SYSTEM READY TO USE!
Total Questions Available: 30
```

### **Check Frontend Status**

```powershell
# View React logs
# Check frontend PowerShell window for:
Compiled successfully!
webpack compiled successfully
```

### **Check Ports**

```powershell
# Backend (Port 8080)
Get-NetTCPConnection -LocalPort 8080 -State Listen

# Frontend (Port 3000)
Get-NetTCPConnection -LocalPort 3000 -State Listen
```

---

## 🔧 Troubleshooting

### **Problem: Script won't run**

**Solution:**
```powershell
# Check execution policy
Get-ExecutionPolicy

# If Restricted, change it
Set-ExecutionPolicy -Scope CurrentUser -ExecutionPolicy RemoteSigned

# Try again
.\start-quiz-system.ps1
```

---

### **Problem: Port already in use**

**Backend (8080):**
```powershell
# Kill Java processes
Get-Process | Where-Object {$_.Name -like "*java*"} | Stop-Process -Force
```

**Frontend (3000):**
```powershell
# Kill process on port 3000
$port3000 = Get-NetTCPConnection -LocalPort 3000 -State Listen -ErrorAction SilentlyContinue
if ($port3000) {
    Stop-Process -Id $port3000.OwningProcess -Force
}
```

---

### **Problem: Backend won't compile**

**Solution:**
```powershell
cd backend/src/main/java

# Clean and recompile
Remove-Item -Recurse -Force com/quizapp/**/*.class -ErrorAction SilentlyContinue
javac com/quizapp/quiz/*.java
javac com/quizapp/answer/*.java
javac com/quizapp/score/*.java
javac com/quizapp/results/*.java
javac com/quizapp/server/*.java
javac com/quizapp/client/*.java
```

---

### **Problem: Frontend dependencies missing**

**Solution:**
```powershell
cd frontend

# Clean install
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json -ErrorAction SilentlyContinue
npm install
```

---

## 📋 System Requirements

### **Backend:**
- ✅ Windows 10/11
- ✅ Java JDK 11+
- ✅ PowerShell 5.1+
- ✅ Port 8080 available

### **Frontend:**
- ✅ Node.js 14.x+ 
- ✅ npm 6.x+
- ✅ Port 3000 available
- ✅ Modern browser (Chrome/Edge/Firefox)

### **Check Installations:**

```powershell
# Java
java -version

# Node.js
node --version

# npm
npm --version

# PowerShell
$PSVersionTable.PSVersion
```

---

## 🎨 Features Overview

### **Backend Features:**
- ✅ Socket-based quiz server
- ✅ Multi-client support
- ✅ Real-time question distribution
- ✅ Answer validation
- ✅ Score tracking
- ✅ Leaderboard generation
- ✅ Statistics and results

### **Frontend Features:**
- ✅ React admin dashboard
- ✅ Real-time updates
- ✅ Question management
- ✅ Student monitoring
- ✅ Results visualization
- ✅ Responsive design

---

## 📞 Support

### **Common Issues:**

1. **Java not found**
   - Install JDK 11+ from oracle.com
   - Add to PATH

2. **Node not found**
   - Install from nodejs.org
   - Restart PowerShell

3. **Permission denied**
   - Run PowerShell as Administrator
   - Set execution policy

4. **Port conflicts**
   - Close conflicting apps
   - Use different ports

---

## 🎓 Team Members' Components

**Member 1:** Server & Client Management  
**Member 2:** Quiz Question Management  
**Member 3:** Answer Collection & Validation  
**Member 4:** Score Management & Leaderboard  
**Member 5:** Results & Statistics  

All components verified by scripts! ✅

---

## 📝 Quick Reference

| Task | Command |
|------|---------|
| Start Everything | `.\start-quiz-system.ps1` → 1 |
| Start Backend | `cd backend; .\start-integrated-quiz.ps1` |
| Start Frontend | `.\start-frontend.ps1` |
| Stop All | Close PowerShell windows |
| Clean Restart | Kill Java → Run script again |

---

**Last Updated:** November 12, 2025  
**Version:** 1.0.0  
**Status:** Production Ready ✅
