# Network Quiz App - Member 1 Starter Script
# This script helps you compile and run the Java server

Write-Host "=======================================" -ForegroundColor Cyan
Write-Host "  Network Quiz App - Member 1 Server  " -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# Set the backend directory
$backendDir = Join-Path $PSScriptRoot "backend\src\main\java"

# Change to backend directory
Set-Location $backendDir

Write-Host "📁 Current Directory: $backendDir" -ForegroundColor Yellow
Write-Host ""

# Compile Java files
Write-Host "🔨 Compiling Java files..." -ForegroundColor Green
javac -encoding UTF-8 com\quizapp\server\QuizServer.java com\quizapp\server\ClientHandler.java com\quizapp\server\ConnectedClientsManager.java com\quizapp\client\TestClient.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilation successful!" -ForegroundColor Green
    Write-Host ""
    
    # Ask user what to run
    Write-Host "What would you like to run?" -ForegroundColor Cyan
    Write-Host "1. Quiz Server" -ForegroundColor White
    Write-Host "2. Test Client" -ForegroundColor White
    Write-Host "3. Exit" -ForegroundColor White
    Write-Host ""
    
    $choice = Read-Host "Enter your choice (1-3)"
    
    switch ($choice) {
        "1" {
            Write-Host ""
            Write-Host "🚀 Starting Quiz Server..." -ForegroundColor Green
            Write-Host "   Press Ctrl+C to stop the server" -ForegroundColor Yellow
            Write-Host ""
            java com.quizapp.server.QuizServer
        }
        "2" {
            Write-Host ""
            Write-Host "🚀 Starting Test Client..." -ForegroundColor Green
            Write-Host "   You can run multiple instances in different terminals" -ForegroundColor Yellow
            Write-Host ""
            java com.quizapp.client.TestClient
        }
        "3" {
            Write-Host "Exiting..." -ForegroundColor Yellow
        }
        default {
            Write-Host "Invalid choice. Exiting..." -ForegroundColor Red
        }
    }
} else {
    Write-Host "❌ Compilation failed! Please check for errors above." -ForegroundColor Red
    Write-Host ""
    Write-Host "Common issues:" -ForegroundColor Yellow
    Write-Host "  - Make sure Java JDK is installed" -ForegroundColor White
    Write-Host "  - Check that javac is in your PATH" -ForegroundColor White
    Write-Host "  - Verify all .java files are present" -ForegroundColor White
}

Write-Host ""
Read-Host "Press Enter to exit"
