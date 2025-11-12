# ==============================================================================
# INTEGRATED QUIZ SERVER - QUICK START SCRIPT
# All 5 Members' Components Working Together
# ==============================================================================

Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  INTEGRATED QUIZ SERVER - QUICK START" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# Navigate to source directory
$sourceDir = "c:\Users\Rashindu\Desktop\GitHub Projects\Network-Quiz-App\backend\src\main\java"
Set-Location $sourceDir

Write-Host "Working Directory: $sourceDir" -ForegroundColor Yellow
Write-Host ""

# ==============================================================================
# STEP 1: Compile All Components
# ==============================================================================

Write-Host "========================================" -ForegroundColor Green
Write-Host "  STEP 1: Compiling All Components" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

$packages = @(
    "com\quizapp\quiz\*.java",
    "com\quizapp\answer\*.java",
    "com\quizapp\score\*.java",
    "com\quizapp\results\*.java",
    "com\quizapp\server\Integrated*.java",
    "com\quizapp\client\IntegratedTestClient.java"
)

$allSuccess = $true

foreach ($package in $packages) {
    $packageName = $package -replace "\\", "/" -replace "/\*\.java", ""
    Write-Host "Compiling $packageName..." -ForegroundColor Yellow
    
    $output = javac -encoding UTF-8 $package 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  [OK] Success" -ForegroundColor Green
    } else {
        Write-Host "  [FAIL] Failed" -ForegroundColor Red
        Write-Host $output
        $allSuccess = $false
    }
}

if (-not $allSuccess) {
    Write-Host ""
    Write-Host "[ERROR] Compilation failed! Please check errors above." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[SUCCESS] All components compiled successfully!" -ForegroundColor Green
Write-Host ""

# ==============================================================================
# STEP 2: Show What's Integrated
# ==============================================================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  STEP 2: Components Integrated" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[OK] Member 1: Server and Client Management" -ForegroundColor Green
Write-Host "[OK] Member 2: Quiz Question Management" -ForegroundColor Green
Write-Host "[OK] Member 3: Answer Collection and Validation" -ForegroundColor Green
Write-Host "[OK] Member 4: Score Management and Leaderboard" -ForegroundColor Green
Write-Host "[OK] Member 5: Results and Statistics" -ForegroundColor Green

# ==============================================================================
# STEP 3: Launch Instructions
# ==============================================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Magenta
Write-Host "  STEP 3: How to Run" -ForegroundColor Magenta
Write-Host "========================================" -ForegroundColor Magenta
Write-Host ""

Write-Host "Option 1: Run Server and Clients Manually" -ForegroundColor Yellow
Write-Host "------------------------------------------" -ForegroundColor Gray
Write-Host ""

Write-Host "  Terminal 1 (Server):" -ForegroundColor Cyan
Write-Host "  java com.quizapp.server.IntegratedQuizServer" -ForegroundColor White
Write-Host ""

Write-Host "  Terminal 2-4 (Clients):" -ForegroundColor Cyan
Write-Host "  java com.quizapp.client.IntegratedTestClient" -ForegroundColor White
Write-Host ""

Write-Host "Option 2: Use This Script's Auto-Launch" -ForegroundColor Yellow
Write-Host "------------------------------------------" -ForegroundColor Gray
Write-Host ""

$choice = Read-Host "Do you want to auto-launch server and 3 clients? (y/n)"

if (($choice -eq "y") -or ($choice -eq "Y")) {
    Write-Host ""
    Write-Host "[LAUNCH] Starting Integrated Quiz System..." -ForegroundColor Green
    Write-Host ""
    
    # Launch server in new window
    Write-Host "Starting Server..." -ForegroundColor Yellow
    $serverCmd = "cd '$sourceDir'; Write-Host 'INTEGRATED QUIZ SERVER' -ForegroundColor Cyan; Write-Host ''; java com.quizapp.server.IntegratedQuizServer"
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $serverCmd
    
    Start-Sleep -Seconds 3
    
    # Launch 3 clients in new windows
    for ($i = 1; $i -le 3; $i++) {
        Write-Host "Starting Client $i..." -ForegroundColor Yellow
        $clientCmd = "cd '$sourceDir'; Write-Host 'QUIZ CLIENT $i' -ForegroundColor Green; Write-Host ''; java com.quizapp.client.IntegratedTestClient"
        Start-Process powershell -ArgumentList "-NoExit", "-Command", $clientCmd
        Start-Sleep -Seconds 1
    }
    
    Write-Host ""
    Write-Host "[SUCCESS] Server and 3 clients launched in separate windows!" -ForegroundColor Green
    Write-Host "==========================================================" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Next Steps:" -ForegroundColor Cyan
    Write-Host "1. In each client window, enter a student name" -ForegroundColor White
    Write-Host "2. Wait for all clients to connect" -ForegroundColor White
    Write-Host "3. Server will auto-start quiz when ready" -ForegroundColor White
    Write-Host "4. Answer questions in client windows" -ForegroundColor White
    Write-Host "5. View leaderboard and final results" -ForegroundColor White
    Write-Host ""
    Write-Host "For detailed usage, see: INTEGRATED_USAGE_GUIDE.md" -ForegroundColor Yellow
    Write-Host ""
    
} else {
    Write-Host ""
    Write-Host "Manual Launch Commands:" -ForegroundColor Cyan
    Write-Host "==========================================================" -ForegroundColor Gray
    Write-Host ""
    Write-Host "Server:" -ForegroundColor Yellow
    Write-Host "java com.quizapp.server.IntegratedQuizServer" -ForegroundColor White
    Write-Host ""
    Write-Host "Clients:" -ForegroundColor Yellow
    Write-Host "java com.quizapp.client.IntegratedTestClient" -ForegroundColor White
    Write-Host ""
    Write-Host "For detailed usage, see: INTEGRATED_USAGE_GUIDE.md" -ForegroundColor Yellow
    Write-Host ""
}

# ==============================================================================
# STEP 4: Additional Information
# ==============================================================================

Write-Host ""
Write-Host "========================================" -ForegroundColor Blue
Write-Host "  Important Notes" -ForegroundColor Blue
Write-Host "========================================" -ForegroundColor Blue
Write-Host ""

Write-Host "[INFO] Server Port: 8080" -ForegroundColor Yellow
Write-Host "[INFO] Time per Question: 30 seconds" -ForegroundColor Yellow
Write-Host "[INFO] Questions per Quiz: 5" -ForegroundColor Yellow
Write-Host "[INFO] Min Participants: 1 (no limit)" -ForegroundColor Yellow
Write-Host ""
Write-Host "[TIP] To stop server/clients: Press Ctrl+C in their windows" -ForegroundColor Cyan
Write-Host "[TIP] To run again: Execute this script again" -ForegroundColor Cyan

Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host "  [READY] INTEGRATED QUIZ SYSTEM READY TO USE!" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
Write-Host ""

Write-Host "All 5 Members' Contributions Working Together!" -ForegroundColor Magenta
Write-Host ""
