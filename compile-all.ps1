# Compile All Java Files Script
# Run this script to compile all backend Java files

Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Compiling All Java Files...          ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

Set-Location "backend\src\main\java"

# Compile in order (to resolve dependencies)
Write-Host "📦 Compiling Member 2: Quiz Management..." -ForegroundColor Yellow
javac -encoding UTF-8 com\quizapp\quiz\*.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Quiz files compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Error compiling Quiz files" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 Compiling Member 3: Answer Collection..." -ForegroundColor Yellow
javac -encoding UTF-8 com\quizapp\answer\*.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Answer files compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Error compiling Answer files" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 Compiling Member 4: Score Management..." -ForegroundColor Yellow
javac -encoding UTF-8 com\quizapp\score\*.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Score files compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Error compiling Score files" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 Compiling Member 5: Results..." -ForegroundColor Yellow
javac -encoding UTF-8 com\quizapp\results\*.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Results files compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Error compiling Results files" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 Compiling Member 1: Server..." -ForegroundColor Yellow
javac -encoding UTF-8 com\quizapp\server\*.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Server files compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Error compiling Server files" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "📦 Compiling Test Client..." -ForegroundColor Yellow
javac -encoding UTF-8 com\quizapp\client\*.java
if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Client files compiled successfully!" -ForegroundColor Green
} else {
    Write-Host "✗ Error compiling Client files" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  ✓ All Files Compiled Successfully!   ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""
Write-Host "Ready to run server: java com.quizapp.server.QuizServer" -ForegroundColor Cyan
