# Test script for auction creation functionality
Write-Host "Testing Auction Creation API Endpoints" -ForegroundColor Green

# Login first to get authentication token
$loginUrl = "http://localhost:8080/api/auth/signin"
$auctionUrl = "http://localhost:8080/api/admin/auctions"

$loginData = @{
    username = "admin"
    password = "password"
} | ConvertTo-Json

Write-Host "1. Logging in as admin..." -ForegroundColor Yellow

try {
    $loginResponse = Invoke-RestMethod -Uri $loginUrl -Method POST -Body $loginData -ContentType "application/json"
    $token = $loginResponse.token
    Write-Host "   ✓ Login successful. Token obtained." -ForegroundColor Green
    
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    Write-Host "`n2. Testing auction creation..." -ForegroundColor Yellow
    
    # Test auction creation
    $auctionData = @{
        title = "Test IT Equipment Auction"
        description = "Testing auction creation functionality for IT equipment procurement"
        startDate = (Get-Date).AddDays(1).ToString("yyyy-MM-ddTHH:mm:ss")
        endDate = (Get-Date).AddDays(3).ToString("yyyy-MM-ddTHH:mm:ss")
        currency = "USD"
        minimumBidIncrement = 5.00
        termsAndConditions = "Payment within 30 days. Free shipping included."
    } | ConvertTo-Json
    
    $auctionResponse = Invoke-RestMethod -Uri $auctionUrl -Method POST -Body $auctionData -Headers $headers
    Write-Host "   ✓ Auction created successfully!" -ForegroundColor Green
    Write-Host "   Auction ID: $($auctionResponse.id)" -ForegroundColor Cyan
    Write-Host "   Title: $($auctionResponse.title)" -ForegroundColor Cyan
    Write-Host "   Status: $($auctionResponse.status)" -ForegroundColor Cyan
    
    $auctionId = $auctionResponse.id
    
    Write-Host "`n3. Testing line item addition..." -ForegroundColor Yellow
    
    # Add line items to the auction
    $lineItemUrl = "http://localhost:8080/api/admin/events/$auctionId/line-items"
    
    $lineItem1 = @{
        itemNumber = "AUC001"
        description = "Dell Laptop - High Performance"
        quantity = 10
        unit = "units"
        estimatedPrice = 1200.00
        specifications = "Intel i7, 16GB RAM, 512GB SSD"
    } | ConvertTo-Json
    
    $lineItem1Response = Invoke-RestMethod -Uri $lineItemUrl -Method POST -Body $lineItem1 -Headers $headers
    Write-Host "   ✓ Line item 1 added successfully!" -ForegroundColor Green
    Write-Host "   Item: $($lineItem1Response.description)" -ForegroundColor Cyan
    
    $lineItem2 = @{
        itemNumber = "AUC002"
        description = "Network Switches - 24 Port"
        quantity = 5
        unit = "units"
        estimatedPrice = 800.00
        specifications = "Gigabit Ethernet, Layer 2, Managed"
    } | ConvertTo-Json
    
    $lineItem2Response = Invoke-RestMethod -Uri $lineItemUrl -Method POST -Body $lineItem2 -Headers $headers
    Write-Host "   ✓ Line item 2 added successfully!" -ForegroundColor Green
    Write-Host "   Item: $($lineItem2Response.description)" -ForegroundColor Cyan
    
    Write-Host "`n4. Testing auction retrieval..." -ForegroundColor Yellow
    
    # Get all auctions
    $getAllAuctionsUrl = "http://localhost:8080/api/admin/auctions"
    $allAuctions = Invoke-RestMethod -Uri $getAllAuctionsUrl -Method GET -Headers $headers
    Write-Host "   ✓ Retrieved $($allAuctions.Count) auction(s)" -ForegroundColor Green
    
    # Get specific auction
    $getAuctionUrl = "http://localhost:8080/api/admin/auctions/$auctionId"
    $specificAuction = Invoke-RestMethod -Uri $getAuctionUrl -Method GET -Headers $headers
    Write-Host "   ✓ Retrieved specific auction: $($specificAuction.title)" -ForegroundColor Green
    
    Write-Host "`n5. Testing auction publication..." -ForegroundColor Yellow
    
    # Publish the auction
    $publishUrl = "http://localhost:8080/api/admin/auctions/$auctionId/publish"
    $publishResponse = Invoke-RestMethod -Uri $publishUrl -Method POST -Headers $headers
    Write-Host "   ✓ Auction published successfully!" -ForegroundColor Green
    Write-Host "   Response: $publishResponse" -ForegroundColor Cyan
    
    Write-Host "`n6. Verifying auction status..." -ForegroundColor Yellow
    
    # Verify the auction is now published
    $verifyAuction = Invoke-RestMethod -Uri $getAuctionUrl -Method GET -Headers $headers
    Write-Host "   ✓ Auction status: $($verifyAuction.status)" -ForegroundColor Green
    
    Write-Host "`n✅ All auction creation tests passed successfully!" -ForegroundColor Green
    Write-Host "`n📋 Summary:" -ForegroundColor Cyan
    Write-Host "   • Auction created with ID: $auctionId" -ForegroundColor White
    Write-Host "   • 2 line items added" -ForegroundColor White
    Write-Host "   • Auction published and ready for bidding" -ForegroundColor White
    
} catch {
    Write-Host "   ❌ Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $responseBody = $_.Exception.Response.Content.ReadAsStringAsync().Result
        Write-Host "   Response: $responseBody" -ForegroundColor Red
    }
}

Write-Host "`n🌐 You can also test the UI at: http://localhost:8080/api/create-auction" -ForegroundColor Magenta
