# Simple PowerShell Test Script for ProcureAI Frontend
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "ProcureAI Frontend Application Test" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

$frontendUrl = "http://localhost:5176"
$backendUrl = "http://localhost:8080"

Write-Host "1. Testing Frontend Application..." -ForegroundColor Cyan
Write-Host ""

# Test if frontend is running
try {
    Write-Host "Checking frontend at $frontendUrl..." -ForegroundColor Yellow -NoNewline
    $response = Invoke-WebRequest -Uri $frontendUrl -Method GET -TimeoutSec 5 -UseBasicParsing
    if ($response.StatusCode -eq 200) {
        Write-Host " ✓ Frontend is running" -ForegroundColor Green
    }
}
catch {
    Write-Host " ✗ Frontend is not accessible" -ForegroundColor Red
    Write-Host "  Please run: npm run dev" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "2. Manual Testing Instructions..." -ForegroundColor Cyan
Write-Host ""
Write-Host "To test the login redirection fix:" -ForegroundColor White
Write-Host ""
Write-Host "1. Open browser: $frontendUrl" -ForegroundColor Gray
Write-Host "2. Should redirect to login page automatically" -ForegroundColor Gray
Write-Host "3. Login with test credentials (check backend for valid users)" -ForegroundColor Gray
Write-Host "4. After login, should redirect based on role:" -ForegroundColor Gray
Write-Host "   - CREATOR role -> /admin/dashboard" -ForegroundColor Gray
Write-Host "   - VENDOR role -> /vendor/dashboard" -ForegroundColor Gray

Write-Host ""
Write-Host "3. Development Commands..." -ForegroundColor Cyan
Write-Host ""
Write-Host "Start Frontend:" -ForegroundColor White
Write-Host "  npm run dev" -ForegroundColor Gray
Write-Host ""
Write-Host "Start Backend (from procure-api folder):" -ForegroundColor White
Write-Host "  ./mvnw spring-boot:run" -ForegroundColor Gray

Write-Host ""
Write-Host "4. Key Changes Made..." -ForegroundColor Cyan
Write-Host ""
Write-Host "✓ Fixed role names: ADMIN -> CREATOR" -ForegroundColor Green
Write-Host "✓ Fixed auction field names: startTime/endTime -> startDate/endDate" -ForegroundColor Green
Write-Host "✓ Updated all protected routes to use correct role checks" -ForegroundColor Green
Write-Host "✓ Login now redirects to / and App routing handles role-based redirect" -ForegroundColor Green

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "Test completed! Open browser to test manually." -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
