# ========================================================
# Network Quiz App - Frontend Quick Start Script
# ========================================================

Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  NETWORK QUIZ APP - FRONTEND QUICK START" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# Set the frontend directory
$frontendDir = Join-Path $PSScriptRoot "frontend"

# Check if directory exists
if (-Not (Test-Path $frontendDir)) {
    Write-Host "[ERROR] Frontend directory not found!" -ForegroundColor Red
    Write-Host "Expected location: $frontendDir" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Change to frontend directory
Set-Location $frontendDir
Write-Host "Working Directory: $frontendDir" -ForegroundColor Green
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  STEP 1: Checking Dependencies" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if Node.js is installed
Write-Host "Checking Node.js..." -NoNewline
try {
    $nodeVersion = node --version 2>$null
    if ($nodeVersion) {
        Write-Host "  [OK] $nodeVersion" -ForegroundColor Green
    } else {
        throw "Node.js not found"
    }
} catch {
    Write-Host "  [MISSING]" -ForegroundColor Red
    Write-Host ""
    Write-Host "[ERROR] Node.js is not installed!" -ForegroundColor Red
    Write-Host "Please download and install from: https://nodejs.org/" -ForegroundColor Yellow
    Write-Host "Recommended: LTS (Long Term Support) version" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if npm is installed
Write-Host "Checking npm..." -NoNewline
try {
    $npmVersion = npm --version 2>$null
    if ($npmVersion) {
        Write-Host "      [OK] v$npmVersion" -ForegroundColor Green
    } else {
        throw "npm not found"
    }
} catch {
    Write-Host "  [MISSING]" -ForegroundColor Red
    Write-Host ""
    Write-Host "[ERROR] npm is not installed!" -ForegroundColor Red
    Write-Host "npm should come with Node.js. Please reinstall Node.js." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host ""

# Check if package.json exists
if (-Not (Test-Path "package.json")) {
    Write-Host "[ERROR] package.json not found!" -ForegroundColor Red
    Write-Host "Make sure you're in the correct frontend directory." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check and install node_modules if needed
if (-Not (Test-Path "node_modules")) {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  STEP 2: Installing Dependencies" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "[INFO] Installing npm packages..." -ForegroundColor Yellow
    Write-Host "       This may take a few minutes..." -ForegroundColor Yellow
    Write-Host ""
    
    npm install
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "[SUCCESS] Dependencies installed successfully!" -ForegroundColor Green
    } else {
        Write-Host ""
        Write-Host "[ERROR] Failed to install dependencies!" -ForegroundColor Red
        Write-Host "Try running manually: npm install" -ForegroundColor Yellow
        Read-Host "Press Enter to exit"
        exit 1
    }
} else {
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  STEP 2: Dependencies Status" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "[OK] node_modules already exists" -ForegroundColor Green
    Write-Host ""
    
    # Ask if user wants to update dependencies
    $updateChoice = Read-Host "Do you want to update dependencies? (y/n)"
    if ($updateChoice -eq "y" -or $updateChoice -eq "Y") {
        Write-Host ""
        Write-Host "[INFO] Updating dependencies..." -ForegroundColor Yellow
        npm update
        Write-Host ""
        Write-Host "[SUCCESS] Dependencies updated!" -ForegroundColor Green
    }
}

Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  STEP 3: Starting Development Server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[INFO] React Development Server Configuration:" -ForegroundColor Yellow
Write-Host "  - URL: http://localhost:3000" -ForegroundColor White
Write-Host "  - Auto-reload: Enabled" -ForegroundColor White
Write-Host "  - Hot Module Replacement: Enabled" -ForegroundColor White
Write-Host ""

# Check if port 3000 is already in use
Write-Host "Checking port availability..." -NoNewline
$portInUse = Get-NetTCPConnection -LocalPort 3000 -State Listen -ErrorAction SilentlyContinue
if ($portInUse) {
    Write-Host "  [WARNING]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "[WARNING] Port 3000 is already in use!" -ForegroundColor Yellow
    Write-Host "Another application might be running on this port." -ForegroundColor Yellow
    Write-Host "React will try to use an alternative port." -ForegroundColor Yellow
    Write-Host ""
} else {
    Write-Host "  [OK]" -ForegroundColor Green
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Green
Write-Host "  STARTING REACT APP..." -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "[TIP] The browser will open automatically" -ForegroundColor Cyan
Write-Host "[TIP] Press Ctrl+C to stop the server" -ForegroundColor Cyan
Write-Host ""
Write-Host "Server starting in 3 seconds..." -ForegroundColor Yellow
Start-Sleep -Seconds 3
Write-Host ""

# Start the development server
npm start

# If npm start exits
Write-Host ""
Write-Host "========================================" -ForegroundColor Yellow
Write-Host "  SERVER STOPPED" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Yellow
Write-Host ""

if ($LASTEXITCODE -eq 0) {
    Write-Host "[INFO] Frontend server stopped successfully." -ForegroundColor Green
} else {
    Write-Host "[ERROR] Frontend server encountered an error!" -ForegroundColor Red
    Write-Host "Exit Code: $LASTEXITCODE" -ForegroundColor Yellow
}

Write-Host ""
Read-Host "Press Enter to exit"
