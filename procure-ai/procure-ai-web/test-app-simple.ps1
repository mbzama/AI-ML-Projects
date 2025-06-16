# PowerShell Test Script for ProcureAI Frontend Application
param(
    [string]$BaseUrl = "http://localhost:5176",
    [string]$ApiUrl = "http://localhost:8080/api",
    [string]$Username = "admin",
    [string]$Password = "password"
)

Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "ProcureAI Frontend Application Test Script" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host ""

# Function to test if a URL is accessible
function Test-Endpoint {
    param(
        [string]$Url,
        [string]$Description
    )
    
    try {
        Write-Host "Testing $Description..." -ForegroundColor Yellow -NoNewline
        $response = Invoke-WebRequest -Uri $Url -Method GET -TimeoutSec 10 -UseBasicParsing
        if ($response.StatusCode -eq 200) {
            Write-Host " ✓ PASS" -ForegroundColor Green
            return $true
        } else {
            Write-Host " ✗ FAIL (Status: $($response.StatusCode))" -ForegroundColor Red
            return $false
        }
    }
    catch {
        Write-Host " ✗ FAIL (Error: $($_.Exception.Message))" -ForegroundColor Red
        return $false
    }
}

# Function to test login API
function Test-LoginAPI {
    param(
        [string]$ApiUrl,
        [string]$Username,
        [string]$Password
    )
    
    try {
        Write-Host "Testing Login API..." -ForegroundColor Yellow -NoNewline
        
        $loginData = @{
            username = $Username
            password = $Password
        } | ConvertTo-Json
        
        $headers = @{
            'Content-Type' = 'application/json'
        }
        
        $response = Invoke-RestMethod -Uri "$ApiUrl/auth/signin" -Method POST -Body $loginData -Headers $headers -TimeoutSec 10
        
        if ($response.token) {
            Write-Host " ✓ PASS (Token received)" -ForegroundColor Green
            Write-Host "  - Username: $($response.username)" -ForegroundColor Gray
            Write-Host "  - Email: $($response.email)" -ForegroundColor Gray
            Write-Host "  - Roles: $($response.roles -join ', ')" -ForegroundColor Gray
            return $response.token
        } else {
            Write-Host " ✗ FAIL (No token in response)" -ForegroundColor Red
            return $null
        }
    }
    catch {
        Write-Host " ✗ FAIL (Error: $($_.Exception.Message))" -ForegroundColor Red
        return $null
    }
}

# Function to check if ports are available
function Test-Port {
    param(
        [string]$ComputerName = "localhost",
        [int]$Port
    )
    
    try {
        $tcpClient = New-Object System.Net.Sockets.TcpClient
        $tcpClient.Connect($ComputerName, $Port)
        $tcpClient.Close()
        return $true
    }
    catch {
        return $false
    }
}

# Main test execution
Write-Host "1. Checking if services are running..." -ForegroundColor Cyan
Write-Host ""

# Check frontend port
$frontendPort = [System.Uri]::new($BaseUrl).Port
if (Test-Port -Port $frontendPort) {
    Write-Host "Frontend port $frontendPort is accessible ✓" -ForegroundColor Green
} else {
    Write-Host "Frontend port $frontendPort is not accessible ✗" -ForegroundColor Red
    Write-Host "Please make sure the frontend development server is running with 'npm run dev'" -ForegroundColor Yellow
}

# Check backend port
$backendPort = [System.Uri]::new($ApiUrl).Port
if (Test-Port -Port $backendPort) {
    Write-Host "Backend port $backendPort is accessible ✓" -ForegroundColor Green
} else {
    Write-Host "Backend port $backendPort is not accessible ✗" -ForegroundColor Red
    Write-Host "Please make sure the backend Spring Boot application is running" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "2. Testing Frontend Application..." -ForegroundColor Cyan
Write-Host ""

# Test frontend endpoints
Test-Endpoint -Url $BaseUrl -Description "Frontend home page"
Test-Endpoint -Url "$BaseUrl/login" -Description "Login page"

Write-Host ""
Write-Host "3. Testing Backend API..." -ForegroundColor Cyan
Write-Host ""

# Test login
$token = Test-LoginAPI -ApiUrl $ApiUrl -Username $Username -Password $Password

Write-Host ""
Write-Host "4. Browser Test Instructions..." -ForegroundColor Cyan
Write-Host ""
Write-Host "To manually test the login redirection:" -ForegroundColor White
Write-Host "1. Open your browser and go to: $BaseUrl" -ForegroundColor Gray
Write-Host "2. You should be redirected to the login page" -ForegroundColor Gray
Write-Host "3. Login with credentials: $Username / $Password" -ForegroundColor Gray
Write-Host "4. After successful login, you should be redirected based on your role:" -ForegroundColor Gray
Write-Host "   - CREATOR role → /admin/dashboard" -ForegroundColor Gray
Write-Host "   - VENDOR role → /vendor/dashboard" -ForegroundColor Gray

Write-Host ""
Write-Host "5. Development Commands..." -ForegroundColor Cyan
Write-Host ""
Write-Host "Frontend (in procure-ai-web directory):" -ForegroundColor White
Write-Host "  npm run dev          # Start development server" -ForegroundColor Gray
Write-Host "  npm run build        # Build for production" -ForegroundColor Gray
Write-Host "  npm run preview      # Preview production build" -ForegroundColor Gray

Write-Host ""
Write-Host "Backend (in procure-api directory):" -ForegroundColor White
Write-Host "  ./mvnw spring-boot:run  # Start Spring Boot application" -ForegroundColor Gray
Write-Host "  java -jar target/procure-ai-api-0.0.1-SNAPSHOT.jar  # Run built JAR" -ForegroundColor Gray

Write-Host ""
Write-Host "===============================================" -ForegroundColor Cyan
Write-Host "Test completed!" -ForegroundColor Cyan
Write-Host "===============================================" -ForegroundColor Cyan
