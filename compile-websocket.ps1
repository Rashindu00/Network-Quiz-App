# Compile WebSocket Server
# Compiles the WebSocket adapter for student quiz interface

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Compiling WebSocket Server...        " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

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

Set-Location "backend\src\main\java"

# Build classpath - include current directory and libraries
$classpath = ".;..\..\..\..\$libDir\java-websocket-1.5.3.jar;..\..\..\..\$libDir\json-20230227.jar;..\..\..\..\$libDir\slf4j-api-2.0.7.jar;..\..\..\..\$libDir\slf4j-simple-2.0.7.jar"

# Compile in order (to resolve dependencies)
Write-Host "Compiling Quiz Management..." -ForegroundColor Yellow
javac -encoding UTF-8 com\quizapp\quiz\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Error compiling Quiz files" -ForegroundColor Red
    Set-Location "..\..\..\..\"
    exit 1
}
Write-Host "[OK] Quiz files compiled!" -ForegroundColor Green

Write-Host ""
Write-Host "Compiling WebSocket Components..." -ForegroundColor Yellow
javac -encoding UTF-8 -cp $classpath com\quizapp\websocket\*.java
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Error compiling WebSocket files" -ForegroundColor Red
    Set-Location "..\..\..\..\"
    exit 1
}
Write-Host "[OK] WebSocket files compiled!" -ForegroundColor Green

Set-Location "..\..\..\..\"

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Compilation Successful!              " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Ready to run WebSocket server!" -ForegroundColor Cyan
Write-Host "Run: .\start-websocket-server.ps1" -ForegroundColor Yellow
