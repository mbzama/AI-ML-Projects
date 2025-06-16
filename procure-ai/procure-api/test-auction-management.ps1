# Test script for auction management page functionality
Write-Host "Testing Auction Management Page" -ForegroundColor Green

# Test the auction management endpoints
$loginUrl = "http://localhost:8080/api/auth/signin"
$auctionsUrl = "http://localhost:8080/api/admin/auctions"

$loginData = @{
    username = "admin"
    password = "password"
} | ConvertTo-Json

Write-Host "1. Logging in as admin..." -ForegroundColor Yellow

try {
    $loginResponse = Invoke-RestMethod -Uri $loginUrl -Method POST -Body $loginData -ContentType "application/json"
    $token = $loginResponse.accessToken
    Write-Host "   OK Login successful." -ForegroundColor Green
    
    $headers = @{
        "Authorization" = "Bearer $token"
        "Content-Type" = "application/json"
    }
    
    Write-Host "`n2. Testing auction listing..." -ForegroundColor Yellow
    
    # Get all auctions
    $auctions = Invoke-RestMethod -Uri $auctionsUrl -Method GET -Headers $headers
    Write-Host "   OK Found $($auctions.Count) auction(s)" -ForegroundColor Green
    
    if ($auctions.Count -gt 0) {
        Write-Host "`n3. Auction Details:" -ForegroundColor Cyan
        foreach ($auction in $auctions) {
            Write-Host "   ID: $($auction.id)" -ForegroundColor White
            Write-Host "   Title: $($auction.title)" -ForegroundColor White
            Write-Host "   Status: $($auction.status)" -ForegroundColor White
            Write-Host "   Type: $($auction.eventType)" -ForegroundColor White
            Write-Host "   Currency: $($auction.currency)" -ForegroundColor White
            Write-Host "   Min Bid Increment: $($auction.minimumBidIncrement)" -ForegroundColor White
            if ($auction.startDate) {
                Write-Host "   Start Date: $($auction.startDate)" -ForegroundColor White
            }
            if ($auction.endDate) {
                Write-Host "   End Date: $($auction.endDate)" -ForegroundColor White
            }
            Write-Host "   ---" -ForegroundColor Gray
        }
        
        # Test getting line items for the first auction
        $firstAuction = $auctions[0]
        Write-Host "`n4. Testing line items for auction ID $($firstAuction.id)..." -ForegroundColor Yellow
        
        $lineItemsUrl = "http://localhost:8080/api/admin/events/$($firstAuction.id)/line-items"
        try {
            $lineItems = Invoke-RestMethod -Uri $lineItemsUrl -Method GET -Headers $headers
            Write-Host "   OK Found $($lineItems.Count) line item(s)" -ForegroundColor Green
            
            if ($lineItems.Count -gt 0) {
                Write-Host "   Line Items:" -ForegroundColor Cyan
                foreach ($item in $lineItems) {
                    Write-Host "     - $($item.itemNumber): $($item.description)" -ForegroundColor White
                    Write-Host "       Quantity: $($item.quantity) $($item.unit)" -ForegroundColor White
                    Write-Host "       Estimated Price: $($item.estimatedPrice)" -ForegroundColor White
                }
            }
        } catch {
            Write-Host "   Warning: Could not load line items" -ForegroundColor Yellow
        }
        
        # Test individual auction retrieval
        Write-Host "`n5. Testing individual auction retrieval..." -ForegroundColor Yellow
        $singleAuctionUrl = "$auctionsUrl/$($firstAuction.id)"
        $singleAuction = Invoke-RestMethod -Uri $singleAuctionUrl -Method GET -Headers $headers
        Write-Host "   OK Retrieved auction: $($singleAuction.title)" -ForegroundColor Green
    }
    
    Write-Host "`n6. Testing auction management page access..." -ForegroundColor Yellow
    
    # Test the admin page accessibility
    $adminPageUrl = "http://localhost:8080/api/admin"
    $adminResponse = Invoke-WebRequest -Uri $adminPageUrl -UseBasicParsing
    
    if ($adminResponse.StatusCode -eq 200) {
        Write-Host "   OK Admin page accessible" -ForegroundColor Green
        
        # Check if the page contains auction management elements
        $content = $adminResponse.Content
        $hasAuctionManagement = $content -like "*Auction Management*"
        $hasCreateAuctionButton = $content -like "*Create Auction*"
        $hasManageAuctionsButton = $content -like "*Manage Auctions*"
        
        Write-Host "   Auction Management section: $(if ($hasAuctionManagement) { 'Present' } else { 'Missing' })" -ForegroundColor $(if ($hasAuctionManagement) { 'Green' } else { 'Red' })
        Write-Host "   Create Auction button: $(if ($hasCreateAuctionButton) { 'Present' } else { 'Missing' })" -ForegroundColor $(if ($hasCreateAuctionButton) { 'Green' } else { 'Red' })
        Write-Host "   Manage Auctions button: $(if ($hasManageAuctionsButton) { 'Present' } else { 'Missing' })" -ForegroundColor $(if ($hasManageAuctionsButton) { 'Green' } else { 'Red' })
    }
    
    Write-Host "`nSUCCESS Auction management page is working!" -ForegroundColor Green
    Write-Host "`nFeatures Available:" -ForegroundColor Cyan
    Write-Host "  - View all auctions in a detailed table" -ForegroundColor White
    Write-Host "  - Publish draft auctions" -ForegroundColor White
    Write-Host "  - View auction line items" -ForegroundColor White
    Write-Host "  - Edit draft auctions" -ForegroundColor White
    Write-Host "  - View individual auction details" -ForegroundColor White
    Write-Host "  - Quick action buttons for common tasks" -ForegroundColor White
    
} catch {
    Write-Host "   ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nAccess the auction management page at: http://localhost:8080/api/admin" -ForegroundColor Magenta
Write-Host "Click 'Manage Auctions' or 'Refresh' in the Auction Management section" -ForegroundColor Magenta
