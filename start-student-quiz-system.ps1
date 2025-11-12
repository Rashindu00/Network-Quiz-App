# Start Complete Student Quiz System
# Launches both WebSocket server and React frontend

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "   STARTING STUDENT QUIZ SYSTEM              " -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host ""

# Check if dependencies are installed
$libDir = "backend\lib"
$websocketJar = "$libDir\java-websocket-1.5.3.jar"
$jsonJar = "$libDir\json-20230227.jar"
$slf4jApiJar = "$libDir\slf4j-api-2.0.7.jar"
$slf4jSimpleJar = "$libDir\slf4j-simple-2.0.7.jar"

if (-not (Test-Path $websocketJar) -or -not (Test-Path $jsonJar) -or -not (Test-Path $slf4jApiJar) -or -not (Test-Path $slf4jSimpleJar)) {
    Write-Host "Dependencies not found. Installing..." -ForegroundColor Yellow
    .\setup-websocket.ps1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Failed to setup dependencies" -ForegroundColor Red
        exit 1
    }
}

# Check if WebSocket server is compiled
$websocketClass = "backend\src\main\java\com\quizapp\websocket\WebSocketServerLauncher.class"
if (-not (Test-Path $websocketClass)) {
    Write-Host "WebSocket server not compiled. Compiling..." -ForegroundColor Yellow
    .\compile-websocket.ps1
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[ERROR] Failed to compile WebSocket server" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Write-Host "==============================================" -ForegroundColor Green
Write-Host "   Prerequisites Check Complete              " -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
Write-Host ""

# Start WebSocket server in new window
Write-Host "Starting WebSocket Server (Port 8081)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; .\start-websocket-server.ps1"
Start-Sleep -Seconds 3

# Start frontend in new window
Write-Host "Starting React Frontend (Port 3000)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$PWD'; .\start-frontend.ps1"
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "==============================================" -ForegroundColor Green
Write-Host "   Student Quiz System Starting...           " -ForegroundColor Green
Write-Host "==============================================" -ForegroundColor Green
Write-Host ""

Write-Host "Services Running:" -ForegroundColor Yellow
Write-Host "   WebSocket Server: http://localhost:8081" -ForegroundColor Cyan
Write-Host "   React Frontend:   http://localhost:3000" -ForegroundColor Cyan
Write-Host ""

Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host "   1. Wait for frontend to open in browser" -ForegroundColor White
Write-Host "   2. Students click 'Student' button" -ForegroundColor White
Write-Host "   3. Students enter their names" -ForegroundColor White
Write-Host "   4. Go to WebSocket server window" -ForegroundColor White
Write-Host "   5. Type 'status' to see connected students" -ForegroundColor White
Write-Host "   6. Type 'start' to begin the quiz!" -ForegroundColor White
Write-Host ""

Write-Host "Useful Commands (in WebSocket server window):" -ForegroundColor Yellow
Write-Host "   status - Show connected students" -ForegroundColor Cyan
Write-Host "   start  - Begin the quiz" -ForegroundColor Cyan
Write-Host "   exit   - Shutdown server" -ForegroundColor Cyan
Write-Host ""

Write-Host "Browser will open automatically at:" -ForegroundColor Green
Write-Host "   http://localhost:3000" -ForegroundColor Cyan
Write-Host ""

Write-Host "Press any key to open browser manually..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

Start-Process "http://localhost:3000"

Write-Host ""
Write-Host "System is running! Good luck with your quiz!" -ForegroundColor Green
Write-Host ""
Write-Host "[NOTE] Keep this window open to monitor the system" -ForegroundColor Yellow
Write-Host "       Close the other windows to stop the services" -ForegroundColor Yellow
