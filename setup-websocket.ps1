# Setup WebSocket Dependencies
# Downloads required libraries for WebSocket server

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Setting Up WebSocket Dependencies    " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Create lib directory if it doesn't exist
$libDir = "backend\lib"
if (-not (Test-Path $libDir)) {
    New-Item -ItemType Directory -Path $libDir | Out-Null
    Write-Host "[OK] Created lib directory" -ForegroundColor Green
}

Write-Host "Downloading Java-WebSocket library..." -ForegroundColor Yellow
Write-Host ""

$websocketUrl = "https://repo1.maven.org/maven2/org/java-websocket/Java-WebSocket/1.5.3/Java-WebSocket-1.5.3.jar"
$websocketJar = "$libDir\java-websocket-1.5.3.jar"

$jsonUrl = "https://repo1.maven.org/maven2/org/json/json/20230227/json-20230227.jar"
$jsonJar = "$libDir\json-20230227.jar"

$slf4jApiUrl = "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.7/slf4j-api-2.0.7.jar"
$slf4jApiJar = "$libDir\slf4j-api-2.0.7.jar"

$slf4jSimpleUrl = "https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.7/slf4j-simple-2.0.7.jar"
$slf4jSimpleJar = "$libDir\slf4j-simple-2.0.7.jar"

# Download Java-WebSocket library
if (-not (Test-Path $websocketJar)) {
    try {
        Write-Host "   Downloading Java-WebSocket 1.5.3..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $websocketUrl -OutFile $websocketJar
        Write-Host "   [OK] Java-WebSocket downloaded" -ForegroundColor Green
    } catch {
        Write-Host "   [ERROR] Failed to download Java-WebSocket" -ForegroundColor Red
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "   [OK] Java-WebSocket already exists" -ForegroundColor Green
}

# Download JSON library
if (-not (Test-Path $jsonJar)) {
    try {
        Write-Host "   Downloading JSON library..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $jsonUrl -OutFile $jsonJar
        Write-Host "   [OK] JSON library downloaded" -ForegroundColor Green
    } catch {
        Write-Host "   [ERROR] Failed to download JSON library" -ForegroundColor Red
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "   [OK] JSON library already exists" -ForegroundColor Green
}

# Download SLF4J API
if (-not (Test-Path $slf4jApiJar)) {
    try {
        Write-Host "   Downloading SLF4J API..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $slf4jApiUrl -OutFile $slf4jApiJar
        Write-Host "   [OK] SLF4J API downloaded" -ForegroundColor Green
    } catch {
        Write-Host "   [ERROR] Failed to download SLF4J API" -ForegroundColor Red
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "   [OK] SLF4J API already exists" -ForegroundColor Green
}

# Download SLF4J Simple
if (-not (Test-Path $slf4jSimpleJar)) {
    try {
        Write-Host "   Downloading SLF4J Simple..." -ForegroundColor Cyan
        Invoke-WebRequest -Uri $slf4jSimpleUrl -OutFile $slf4jSimpleJar
        Write-Host "   [OK] SLF4J Simple downloaded" -ForegroundColor Green
    } catch {
        Write-Host "   [ERROR] Failed to download SLF4J Simple" -ForegroundColor Red
        Write-Host "   Error: $($_.Exception.Message)" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "   [OK] SLF4J Simple already exists" -ForegroundColor Green
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  Dependencies Setup Complete!         " -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Dependencies installed in: $libDir" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Run: .\compile-websocket.ps1" -ForegroundColor Cyan
Write-Host "  2. Run: .\start-websocket-server.ps1" -ForegroundColor Cyan
