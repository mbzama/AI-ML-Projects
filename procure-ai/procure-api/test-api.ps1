# PowerShell script to test the Procurement Application REST API

Write-Host "=====================================" -ForegroundColor Green
Write-Host "Procurement Application API Test Script" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

$baseUrl = "http://localhost:8080/api"

# Function to make HTTP requests using Invoke-RestMethod
function Test-Endpoint {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Description,
        [hashtable]$Headers = @{},
        [string]$Body = $null,
        [string]$ContentType = "application/json"
    )
    
    Write-Host "`n$Description" -ForegroundColor Yellow
    Write-Host "URL: $Method $Url" -ForegroundColor Cyan
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            Headers = $Headers
        }
        
        if ($Body) {
            $params.Body = $Body
            $params.ContentType = $ContentType
        }
        
        $response = Invoke-RestMethod @params
        Write-Host "✓ Success:" -ForegroundColor Green
        $response | ConvertTo-Json -Depth 3 | Write-Host
    }
    catch {
        Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Wait for application to start
Write-Host "`nWaiting for application to start..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Test 1: Check if application is running
Test-Endpoint -Method "GET" -Url "$baseUrl/actuator/health" -Description "1. Check Application Health"

# Test 2: Register a new user
$registerPayload = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
    firstName = "Test"
    lastName = "User"
    roles = @("VENDOR")
} | ConvertTo-Json

Test-Endpoint -Method "POST" -Url "$baseUrl/auth/register" -Description "2. Register New User" -Body $registerPayload

# Test 3: Login to get JWT token
$loginPayload = @{
    username = "admin"
    password = "password"
} | ConvertTo-Json

Write-Host "`n3. Login with Admin User" -ForegroundColor Yellow
try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST -Body $loginPayload -ContentType "application/json"
    $token = $loginResponse.token
    $authHeaders = @{ "Authorization" = "Bearer $token" }
    Write-Host "✓ Login successful. Token received." -ForegroundColor Green
}
catch {
    Write-Host "✗ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    $authHeaders = @{}
}

# Test 4: Get all users (Admin only)
Test-Endpoint -Method "GET" -Url "$baseUrl/users" -Description "4. Get All Users (Admin)" -Headers $authHeaders

# Test 5: Get all vendors
Test-Endpoint -Method "GET" -Url "$baseUrl/vendors" -Description "5. Get All Vendors" -Headers $authHeaders

# Test 6: Create a new RFQ
$rfqPayload = @{
    title = "Test RFQ for Office Supplies"
    description = "Testing RFQ creation via API"
    eventType = "RFQ"
    startDate = (Get-Date).AddDays(1).ToString("yyyy-MM-ddTHH:mm:ss")
    endDate = (Get-Date).AddDays(30).ToString("yyyy-MM-ddTHH:mm:ss")
    currency = "USD"
    termsAndConditions = "Net 30 days payment terms"
} | ConvertTo-Json

Test-Endpoint -Method "POST" -Url "$baseUrl/rfx-events" -Description "6. Create New RFQ" -Headers $authHeaders -Body $rfqPayload

# Test 7: Get all RFX events
Test-Endpoint -Method "GET" -Url "$baseUrl/rfx-events" -Description "7. Get All RFX Events" -Headers $authHeaders

# Test 8: Create an auction
$auctionPayload = @{
    title = "Test Auction for IT Services"
    description = "Testing auction creation via API"
    eventType = "AUCTION"
    startDate = (Get-Date).AddDays(1).ToString("yyyy-MM-ddTHH:mm:ss")
    endDate = (Get-Date).AddDays(7).ToString("yyyy-MM-ddTHH:mm:ss")
    currency = "USD"
    minimumBidIncrement = 10.00
} | ConvertTo-Json

Test-Endpoint -Method "POST" -Url "$baseUrl/rfx-events" -Description "8. Create New Auction" -Headers $authHeaders -Body $auctionPayload

# Test 9: Get active events
Test-Endpoint -Method "GET" -Url "$baseUrl/rfx-events/active" -Description "9. Get Active Events" -Headers $authHeaders

Write-Host "`n=====================================" -ForegroundColor Green
Write-Host "API Testing Complete!" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

Write-Host "`nAdditional endpoints you can test:" -ForegroundColor Cyan
Write-Host "- GET    $baseUrl/vendors/{id}" -ForegroundColor White
Write-Host "- POST   $baseUrl/vendors" -ForegroundColor White
Write-Host "- GET    $baseUrl/rfx-events/{id}" -ForegroundColor White
Write-Host "- PUT    $baseUrl/rfx-events/{id}" -ForegroundColor White
Write-Host "- POST   $baseUrl/rfx-events/{id}/line-items" -ForegroundColor White
Write-Host "- GET    $baseUrl/rfx-events/{id}/responses" -ForegroundColor White
Write-Host "- POST   $baseUrl/rfq-responses" -ForegroundColor White
Write-Host "- POST   $baseUrl/auction-bids" -ForegroundColor White
