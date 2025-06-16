# PowerShell script to setup PostgreSQL database for Procurement Application

# Check if PostgreSQL is running
Write-Host "Checking PostgreSQL service status..." -ForegroundColor Green

# Variables
$dbName = "procure_db"
$dbUser = "postgres"
$dbPassword = "sillicon"
$dbPort = "5435"
$sqlFile = "schema.sql"

Write-Host "Creating database and running schema..." -ForegroundColor Yellow

# Create database (if it doesn't exist)
$createDbCommand = "CREATE DATABASE $dbName;"
$checkDbCommand = "SELECT 1 FROM pg_database WHERE datname = '$dbName';"

Write-Host "Attempting to create database: $dbName" -ForegroundColor Cyan

# Set PGPASSWORD environment variable for this session
$env:PGPASSWORD = $dbPassword

# Check if database exists
$dbExists = & psql -h localhost -p $dbPort -U $dbUser -d postgres -t -c $checkDbCommand 2>$null

if (-not $dbExists) {
    Write-Host "Database doesn't exist. Creating..." -ForegroundColor Yellow
    & psql -h localhost -p $dbPort -U $dbUser -d postgres -c $createDbCommand
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database created successfully!" -ForegroundColor Green
    } else {
        Write-Host "Failed to create database. Please check PostgreSQL connection." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "Database already exists." -ForegroundColor Green
}

# Run schema.sql
if (Test-Path $sqlFile) {
    Write-Host "Running schema.sql..." -ForegroundColor Yellow
    & psql -h localhost -p $dbPort -U $dbUser -d $dbName -f $sqlFile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Schema and sample data loaded successfully!" -ForegroundColor Green
    } else {
        Write-Host "Failed to load schema. Please check the SQL file." -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "schema.sql file not found!" -ForegroundColor Red
    exit 1
}

Write-Host "Database setup complete!" -ForegroundColor Green
Write-Host "You can now start the Spring Boot application." -ForegroundColor Cyan
