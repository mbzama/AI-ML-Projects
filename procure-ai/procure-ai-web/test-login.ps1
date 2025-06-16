# Login Test Script
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "ProcureAI Login Redirection Test" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Testing login process..." -ForegroundColor Yellow
Write-Host ""

Write-Host "1. Frontend Status:" -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "http://localhost:5173" -Method GET -TimeoutSec 5 -UseBasicParsing
    Write-Host "   ✓ Frontend accessible (Status: $($response.StatusCode))" -ForegroundColor Green
}
catch {
    Write-Host "   ✗ Frontend not accessible" -ForegroundColor Red
}

Write-Host ""
Write-Host "2. Backend Login Test:" -ForegroundColor White

# Test admin login
$adminData = @{ username = "admin"; password = "password" } | ConvertTo-Json
$headers = @{ 'Content-Type' = 'application/json' }
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signin" -Method POST -Body $adminData -Headers $headers
    Write-Host "   ✓ Admin login successful" -ForegroundColor Green
    Write-Host "     - Username: $($response.username)" -ForegroundColor Gray
    Write-Host "     - Roles: $($response.roles -join ', ')" -ForegroundColor Gray
    Write-Host "     - Expected redirect: /admin/dashboard" -ForegroundColor Gray
}
catch {
    Write-Host "   ✗ Admin login failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Test vendor login
$vendorData = @{ username = "vendor1"; password = "password" } | ConvertTo-Json
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/signin" -Method POST -Body $vendorData -Headers $headers
    Write-Host "   ✓ Vendor login successful" -ForegroundColor Green
    Write-Host "     - Username: $($response.username)" -ForegroundColor Gray
    Write-Host "     - Roles: $($response.roles -join ', ')" -ForegroundColor Gray
    Write-Host "     - Expected redirect: /vendor/dashboard" -ForegroundColor Gray
}
catch {
    Write-Host "   ✗ Vendor login failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "3. Manual Test Instructions:" -ForegroundColor White
Write-Host ""
Write-Host "   Open browser: http://localhost:5173" -ForegroundColor Yellow
Write-Host ""
Write-Host "   Test Admin Login:" -ForegroundColor Cyan
Write-Host "   - Username: admin" -ForegroundColor Gray
Write-Host "   - Password: password" -ForegroundColor Gray
Write-Host "   - Should redirect to: /admin/dashboard" -ForegroundColor Gray
Write-Host ""
Write-Host "   Test Vendor Login:" -ForegroundColor Cyan
Write-Host "   - Username: vendor1" -ForegroundColor Gray
Write-Host "   - Password: password" -ForegroundColor Gray
Write-Host "   - Should redirect to: /vendor/dashboard" -ForegroundColor Gray

Write-Host ""
Write-Host "4. Changes Made:" -ForegroundColor White
Write-Host "   ✓ Fixed LoginResponse interface (accessToken, tokenType)" -ForegroundColor Green
Write-Host "   ✓ Fixed role prefix handling (ROLE_CREATOR -> CREATOR)" -ForegroundColor Green
Write-Host "   ✓ Added useEffect for post-login redirection" -ForegroundColor Green
Write-Host "   ✓ Added console logging for debugging" -ForegroundColor Green

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Ready for testing!" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
