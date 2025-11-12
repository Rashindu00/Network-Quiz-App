# Start WebSocket Quiz Server
# Launches the WebSocket server for student quiz interface

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Starting WebSocket Quiz Server...    " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if compiled
$websocketClass = "backend\src\main\java\com\quizapp\websocket\WebSocketServerLauncher.class"
if (-not (Test-Path $websocketClass)) {
    Write-Host "[ERROR] WebSocket server not compiled!" -ForegroundColor Red
    Write-Host "   Please run: .\compile-websocket.ps1 first" -ForegroundColor Yellow
    exit 1
}

# Check if dependencies exist
$libDir = "backend\lib"
$websocketJar = "$libDir\java-websocket-1.5.3.jar"
$jsonJar = "$libDir\json-20230227.jar"
$slf4jApiJar = "$libDir\slf4j-api-2.0.7.jar"
$slf4jSimpleJar = "$libDir\slf4j-simple-2.0.7.jar"

if (-not (Test-Path $websocketJar) -or -not (Test-Path $jsonJar) -or -not (Test-Path $slf4jApiJar) -or -not (Test-Path $slf4jSimpleJar)) {
    Write-Host "[ERROR] Dependencies not found!" -ForegroundColor Red
    Write-Host "   Please run: .\setup-websocket.ps1 first" -ForegroundColor Yellow
    exit 1
}

# Check if questions.txt exists
if (-not (Test-Path "backend\src\main\java\questions.txt")) {
    Write-Host "[WARNING] questions.txt not found in backend\src\main\java\" -ForegroundColor Yellow
    Write-Host "   Server may not work properly without questions" -ForegroundColor Yellow
    Write-Host ""
}

Set-Location "backend\src\main\java"

# Build classpath - use proper variable expansion
$classpath = ".;..\..\..\..\$libDir\java-websocket-1.5.3.jar;..\..\..\..\$libDir\json-20230227.jar;..\..\..\..\$libDir\slf4j-api-2.0.7.jar;..\..\..\..\$libDir\slf4j-simple-2.0.7.jar"

Write-Host "Starting WebSocket server on port 8081..." -ForegroundColor Green
Write-Host ""
Write-Host "Server Commands:" -ForegroundColor Yellow
Write-Host "   start  - Start the quiz" -ForegroundColor Cyan
Write-Host "   status - Show server status" -ForegroundColor Cyan
Write-Host "   help   - Show all commands" -ForegroundColor Cyan
Write-Host "   exit   - Shutdown server" -ForegroundColor Cyan
Write-Host ""
Write-Host "Press Ctrl+C to force stop" -ForegroundColor Gray
Write-Host ""

# Run the WebSocket server
java -cp $classpath com.quizapp.websocket.WebSocketServerLauncher

Set-Location "..\..\..\..\"

Write-Host ""
Write-Host "Server stopped." -ForegroundColor Yellow
