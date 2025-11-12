# Network Quiz App - Frontend Starter Script
# This script helps you run the React frontend

Write-Host "=======================================" -ForegroundColor Cyan
Write-Host " Network Quiz App - Admin Dashboard   " -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan
Write-Host ""

# Set the frontend directory
$frontendDir = Join-Path $PSScriptRoot "frontend"

# Check if directory exists
if (-Not (Test-Path $frontendDir)) {
    Write-Host "❌ Frontend directory not found!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit
}

# Change to frontend directory
Set-Location $frontendDir

Write-Host "📁 Current Directory: $frontendDir" -ForegroundColor Yellow
Write-Host ""

# Check if node_modules exists
if (-Not (Test-Path "node_modules")) {
    Write-Host "📦 Installing dependencies..." -ForegroundColor Green
    Write-Host "   This may take a few minutes..." -ForegroundColor Yellow
    Write-Host ""
    npm install
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Dependencies installed successfully!" -ForegroundColor Green
        Write-Host ""
    } else {
        Write-Host "❌ Failed to install dependencies!" -ForegroundColor Red
        Write-Host ""
        Write-Host "Make sure Node.js and npm are installed:" -ForegroundColor Yellow
        Write-Host "  Download from: https://nodejs.org/" -ForegroundColor White
        Read-Host "Press Enter to exit"
        exit
    }
}

# Start the development server
Write-Host "🚀 Starting React development server..." -ForegroundColor Green
Write-Host "   The browser will open automatically" -ForegroundColor Yellow
Write-Host "   Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

npm start
