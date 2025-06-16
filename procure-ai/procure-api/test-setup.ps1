# Test script for Procure AI application
Write-Host "Testing Procure AI Build and Run Script" -ForegroundColor Cyan
Write-Host "=======================================" -ForegroundColor Cyan

# Test if build-and-run.ps1 exists
if (Test-Path ".\build-and-run.ps1") {
    Write-Host "✓ build-and-run.ps1 found" -ForegroundColor Green
} else {
    Write-Host "✗ build-and-run.ps1 not found" -ForegroundColor Red
    exit 1
}

# Test Java installation
Write-Host "`nChecking Java installation..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java is installed" -ForegroundColor Green
    Write-Host "  Version info: $($javaVersion[0])" -ForegroundColor Gray
} catch {
    Write-Host "✗ Java not found - please install Java 17 or higher" -ForegroundColor Red
}

# Test Maven installation
Write-Host "`nChecking Maven installation..." -ForegroundColor Yellow
try {
    $mavenVersion = mvn -version 2>&1 | Select-Object -First 1
    Write-Host "✓ Maven is installed" -ForegroundColor Green
    Write-Host "  Version info: $mavenVersion" -ForegroundColor Gray
} catch {
    Write-Host "✗ Maven not found - please install Apache Maven" -ForegroundColor Red
}

# Check if pom.xml exists
if (Test-Path ".\pom.xml") {
    Write-Host "✓ pom.xml found" -ForegroundColor Green
} else {
    Write-Host "✗ pom.xml not found - make sure you're in the right directory" -ForegroundColor Red
}

Write-Host "`nUsage examples:" -ForegroundColor Cyan
Write-Host "  .\build-and-run.ps1            # Build and run the application" -ForegroundColor White
Write-Host "  .\build-and-run.ps1 build      # Build only" -ForegroundColor White
Write-Host "  .\build-and-run.ps1 test       # Run tests only" -ForegroundColor White
Write-Host "  .\build-and-run.ps1 dev        # Run in development mode" -ForegroundColor White

Write-Host "`nApplication URLs (when running):" -ForegroundColor Cyan
Write-Host "  Home Page:      http://localhost:8080" -ForegroundColor White
Write-Host "  Auctions Page:  http://localhost:8080/auctions" -ForegroundColor White
Write-Host "  Admin Panel:    http://localhost:8080/admin" -ForegroundColor White
Write-Host "  Login Page:     http://localhost:8080/login" -ForegroundColor White

Write-Host "`nTest completed!" -ForegroundColor Green
