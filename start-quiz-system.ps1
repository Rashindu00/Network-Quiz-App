# ========================================================
# Network Quiz App - Complete System Starter
# Starts Backend Server + Frontend Dashboard
# ========================================================

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  NETWORK QUIZ APP - COMPLETE SYSTEM STARTER" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "This script will start:" -ForegroundColor Yellow
Write-Host "  1. Backend Quiz Server (Java - Port 8080)" -ForegroundColor White
Write-Host "  2. Frontend Admin Dashboard (React - Port 3000)" -ForegroundColor White
Write-Host "  3. Test Clients (Optional)" -ForegroundColor White
Write-Host ""

# Ask what to start
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  SELECT COMPONENTS TO START" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Backend + Frontend (Full System)" -ForegroundColor Green
Write-Host "2. Backend Only (Quiz Server + Test Clients)" -ForegroundColor Yellow
Write-Host "3. Frontend Only (Admin Dashboard)" -ForegroundColor Yellow
Write-Host "4. Exit" -ForegroundColor Red
Write-Host ""

$choice = Read-Host "Enter your choice (1-4)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "[SELECTED] Full System - Backend + Frontend" -ForegroundColor Green
        Write-Host ""
        
        # Kill existing Java processes
        Write-Host "[CLEANUP] Stopping existing Java processes..." -ForegroundColor Yellow
        Get-Process | Where-Object {$_.Name -like "*java*"} | Stop-Process -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
        Write-Host "[OK] Cleanup complete" -ForegroundColor Green
        Write-Host ""
        
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "  STEP 1: Starting Backend Server" -ForegroundColor Cyan
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        
        # Start backend in new window
        $backendScript = Join-Path $PSScriptRoot "backend\start-integrated-quiz.ps1"
        if (Test-Path $backendScript) {
            Start-Process powershell -ArgumentList "-NoExit", "-File", "`"$backendScript`""
            Write-Host "[OK] Backend server window opened" -ForegroundColor Green
            Write-Host "[INFO] Check the new PowerShell window for backend status" -ForegroundColor Cyan
        } else {
            Write-Host "[ERROR] Backend script not found: $backendScript" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
        
        Write-Host ""
        Write-Host "[INFO] Waiting for backend to initialize..." -ForegroundColor Yellow
        Start-Sleep -Seconds 5
        Write-Host ""
        
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host "  STEP 2: Starting Frontend Dashboard" -ForegroundColor Cyan
        Write-Host "========================================" -ForegroundColor Cyan
        Write-Host ""
        
        # Start frontend in new window
        $frontendScript = Join-Path $PSScriptRoot "start-frontend.ps1"
        if (Test-Path $frontendScript) {
            Start-Process powershell -ArgumentList "-NoExit", "-File", "`"$frontendScript`""
            Write-Host "[OK] Frontend dashboard window opened" -ForegroundColor Green
            Write-Host "[INFO] Check the new PowerShell window for frontend status" -ForegroundColor Cyan
        } else {
            Write-Host "[ERROR] Frontend script not found: $frontendScript" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
        
        Write-Host ""
        Write-Host "========================================" -ForegroundColor Green
        Write-Host "  SYSTEM STARTED SUCCESSFULLY!" -ForegroundColor Green
        Write-Host "========================================" -ForegroundColor Green
        Write-Host ""
        Write-Host "Access Points:" -ForegroundColor Yellow
        Write-Host "  - Backend API:        http://localhost:8080" -ForegroundColor White
        Write-Host "  - Frontend Dashboard: http://localhost:3000" -ForegroundColor White
        Write-Host "  - Test Clients:       Running in separate windows" -ForegroundColor White
        Write-Host ""
        Write-Host "[TIP] Keep all windows open for the system to work" -ForegroundColor Cyan
        Write-Host "[TIP] Close individual windows to stop components" -ForegroundColor Cyan
        Write-Host ""
    }
    
    "2" {
        Write-Host ""
        Write-Host "[SELECTED] Backend Only" -ForegroundColor Yellow
        Write-Host ""
        
        # Kill existing Java processes
        Write-Host "[CLEANUP] Stopping existing Java processes..." -ForegroundColor Yellow
        Get-Process | Where-Object {$_.Name -like "*java*"} | Stop-Process -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
        Write-Host "[OK] Cleanup complete" -ForegroundColor Green
        Write-Host ""
        
        # Start backend
        $backendScript = Join-Path $PSScriptRoot "backend\start-integrated-quiz.ps1"
        if (Test-Path $backendScript) {
            & $backendScript
        } else {
            Write-Host "[ERROR] Backend script not found: $backendScript" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
    }
    
    "3" {
        Write-Host ""
        Write-Host "[SELECTED] Frontend Only" -ForegroundColor Yellow
        Write-Host ""
        
        # Start frontend
        $frontendScript = Join-Path $PSScriptRoot "start-frontend.ps1"
        if (Test-Path $frontendScript) {
            & $frontendScript
        } else {
            Write-Host "[ERROR] Frontend script not found: $frontendScript" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
    }
    
    "4" {
        Write-Host ""
        Write-Host "[INFO] Exiting..." -ForegroundColor Yellow
        exit 0
    }
    
    default {
        Write-Host ""
        Write-Host "[ERROR] Invalid choice!" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

Write-Host ""
Read-Host "Press Enter to exit"
